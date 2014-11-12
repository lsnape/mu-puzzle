(ns mu-puzzle.dev
  (:require [environ.core :refer [env]]
            [net.cgrand.enlive-html :refer [set-attr prepend append html]]
            [cemerick.piggieback :as piggieback]
            [weasel.repl.websocket :as weasel]
            [leiningen.core.main :as lein]))

(def is-dev? (env :is-dev))

(def inject-devmode-html
  (comp
     (set-attr :class "is-dev")
     (prepend (html [:script {:type "text/javascript" :src "/js/out/goog/base.js"}]))
     (prepend (html [:script {:type "text/javascript" :src "/react/react.js"}]))
     (prepend (html [:script {:type "text/javascript" :src "https://code.jquery.com/jquery-2.1.1.js"}]))
     (prepend (html [:script {:type "text/javascript" :src "//maxcdn.bootstrapcdn.com/bootstrap/3.3.0/js/bootstrap.min.js"}]))
     (prepend (html [:script {:type "text/css" :src "//maxcdn.bootstrapcdn.com/bootstrap/3.3.0/css/bootstrap.min.css"}]))
     (append  (html [:script {:type "text/javascript"} "goog.require('mu_puzzle.dev')"]))))

(defn browser-repl []
  (piggieback/cljs-repl :repl-env (weasel/repl-env :ip "0.0.0.0" :port 9001)))

(defn start-figwheel []
  (future
    (print "Starting figwheel.\n")
    (lein/-main ["figwheel"])))
