(ns mu-puzzle.service.page-frame
  (:require [mu-puzzle.service.routes :refer [site-routes]]
            [mu-puzzle.service.css :as css]
            [bidi.bidi :as bidi]
            [hiccup.page :refer [html5 include-css include-js]]
            [phoenix.modules.cljs :as cljs]
            [ring.util.response :refer [response content-type]]
            [simple-brepl.service :refer [brepl-js]]))

(defn page-handler [cljs-compiler]
  (fn [req]
    (-> (response
         (html5
          [:head
           [:title "MIU Puzzle"]

           (include-js "http://fb.me/react-0.12.1.js")
           (include-js "//cdnjs.cloudflare.com/ajax/libs/jquery/2.0.3/jquery.min.js")
           (include-js "//netdna.bootstrapcdn.com/bootstrap/3.0.0/js/bootstrap.min.js")
           (include-css "//netdna.bootstrapcdn.com/bootstrap/3.0.0/css/bootstrap.min.css")

           [:script (brepl-js)]

           (include-js (get-in (cljs/compiler-settings cljs-compiler) [:modules :main]))
           (include-css (bidi/path-for site-routes :site-css :request-method :get))]
    
          [:body]))

        (content-type "text/html"))))
