(ns mu-puzzle.ui.app
  (:require [mu-puzzle.ui.util :refer [on]]
            [mu-puzzle.ui.game :as game]
            [clojure.string :as s]
            [reagent.core :as reagent :refer [atom]]
            simple-brepl.client)
  (:require-macros [cljs.core.async.macros :refer [go go-loop]]))

(enable-console-print!)

(defn child []
  [:div "Hello World!"])

(set! (.-onload js/window)
      (fn []
        (reagent/render [child] js/document.body)))
