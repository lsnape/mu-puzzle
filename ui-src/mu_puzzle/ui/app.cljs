(ns mu-puzzle.ui.app
  (:require [mu-puzzle.ui.game :as game]
            [clojure.string :as s]
            [reagent.core :as reagent :refer [atom]]
            simple-brepl.client)
  (:require-macros [cljs.core.async.macros :refer [go go-loop]]))

(enable-console-print!)

(defn render-miu-component [{:keys [miu-string]}]
  (for [ch miu-string]
    [:span.miu-ch ch]))

(defn render-buttons []
  [:div.miu-buttons-outer
   (for [[button-name handler] {"double" (fn []
                                           (prn "TODO - double"))

                                "append U" (fn []
                                             (prn "TODO - append U"))
                               
                                "undo" (fn []
                                         (prn "TODO - undo"))}]
     [:div.miu-buttons
      [:button.btn.btn-default {:on-click handler}
       button-name]])])

(defn miu-component []
  (let [!miu-state (atom {:miu-string "MIU"})]
    (fn []
      [:div.miu-component
       (render-miu-component @!miu-state)
       (render-buttons)])))

(set! (.-onload js/window)
      (fn []
        (reagent/render [miu-component] js/document.body)))
