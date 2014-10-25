(ns mu-puzzle.service.handler
  (:require [compojure.core :refer [routes GET]]
            [compojure.handler :refer [api]]
            [compojure.route :refer [resources]]
            [frodo.web :refer [App]]
            [hiccup.page :refer [html5 include-css include-js]]
            [ring.util.response :refer [response]]))

(defn page-frame []
  (html5
   [:html
    [:head
     (include-js "https://code.jquery.com/jquery-2.1.1.js")
     (include-js "//maxcdn.bootstrapcdn.com/bootstrap/3.2.0/js/bootstrap.min.js")
     (include-css "//maxcdn.bootstrapcdn.com/bootstrap/3.2.0/css/bootstrap.min.css")]

    (include-js "http://fb.me/react-0.11.1.js")
     (include-js "/js/goog/base.js")
     (include-js "/js/mu-puzzle.js")
    
    [:body
     [:div#mu-puzzle]
     [:script {:type "text/javascript"} "goog.require('mu_puzzle.ui.app');"]]]))

(defn app-routes []
  (routes
    (GET "/" [] (response (page-frame)))
    (resources "/js" {:root "js"})))

(def app
  (reify App
    (start! [_]
      {:frodo/handler (-> (app-routes)
                          api)})
    (stop! [_ system])))
