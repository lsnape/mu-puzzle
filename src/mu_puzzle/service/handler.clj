(ns mu-puzzle.service.handler
  (:require [mu-puzzle.service.css :as css]
            [mu-puzzle.service.routes :as routes]
            [mu-puzzle.service.page-frame :refer [page-handler]]
            [modular.ring :refer [WebRequestHandler]]
            [bidi.ring :refer [make-handler]]
            [com.stuartsierra.component :refer [Lifecycle]]
            [ring.util.response :refer [response content-type]]))

(defn site-handlers [cljs-compiler]
  {::page-handler (page-handler cljs-compiler)
   ::site-css (fn [req]
                (-> (response (css/site-css))
                    (content-type "text/css")))})

(defn api-handlers []
  {})

(defrecord AppHandler []
  Lifecycle
  (start [this] this)
  (stop [this] this)

  WebRequestHandler
  (request-handler [{:keys [db cljs-compiler]}]
    (make-handler ["" [routes/site-routes
                       routes/api-routes
                       
                       (let [{:keys [web-context-path resource-prefix]} cljs-compiler]
                         [web-context-path (bidi.ring/resources {:prefix resource-prefix})])]]
                  
                  (some-fn (site-handlers cljs-compiler)
                           (api-handlers)
                           #(when (fn? %) %)))))
