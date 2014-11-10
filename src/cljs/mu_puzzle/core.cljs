(ns mu-puzzle.core
  (:require [om.core :as om :include-macros true]
            [om-tools.dom :as dom :include-macros true]
            [om-tools.core :refer-macros [defcomponent]]
            [sablono.core :as html :refer-macros [html]]))

(def app-state (atom {:text "MU Puzzle"}))

(defn main []
  (om/root
    (fn [app owner]
      (reify
        om/IRender
        (render [_]
          (html
           [:h1 {:style {:text-align "center", :margin "10em auto"}}
            (:text app)]))))
    app-state
    {:target (. js/document (getElementById "app"))}))
