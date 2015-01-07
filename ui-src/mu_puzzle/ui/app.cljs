(ns mu-puzzle.ui.app
  (:require [mu-puzzle.ui.util :refer [on]]
            [mu-puzzle.ui.game :as game]
            [clojure.string :as s]
            simple-brepl.client)
  (:require-macros [cljs.core.async.macros :refer [go go-loop]]))

(enable-console-print!)

(set! (.-onload js/window)
      (fn []
        ;; slot on reagent here
        ))
