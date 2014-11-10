(ns mu-puzzle.core
  (:require [mu-puzzle.game :as game]
            [om.core :as om :include-macros true]
            [om-tools.dom :as dom :include-macros true]
            [om-tools.core :refer-macros [defcomponent]]
            [sablono.core :as html :refer-macros [html]]
            [cljs.core.async :as a]))

(def app-state (atom {:text "MI"}))

(defcomponent mu-text [text owner]
  (render-state [_ _]
    (html
     [:p {:on-click (fn [e]
                      (js/alert "woooo"))

          :style {:text-align "center"
                  :margin "8em auto"}}
      text])))

(defn main []
  (om/root
      (fn [{:keys [text] :as app} owner]
        (reify
          om/IRender
          (render [_]
            (html
             [:h1 "MU Puzzle"
              [:div
               (om/build mu-text text)]]))))
    app-state
    {:target (. js/document (getElementById "app"))}))
