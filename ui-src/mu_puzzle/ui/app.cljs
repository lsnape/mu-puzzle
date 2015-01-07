(ns mu-puzzle.ui.app
  (:require [mu-puzzle.ui.game :as game]
            [clojure.string :as s]
            [cljs.core.async :as a]
            [reagent.core :as reagent :refer [atom]]
            simple-brepl.client)
  (:require-macros [cljs.core.async.macros :refer [go go-loop]]))

(enable-console-print!)

(defn render-miu-component [{:keys [miu-string]} game-ch]
  (for [ch miu-string]
    [:span.miu-ch ch]))

(defn render-buttons [game-ch]
  [:div.miu-buttons-outer
   (for [[action button-name :as button] {::double "double"
                                          ::append-u "append U"
                                          ::undo "undo"}]
     
     ^{:key action} [:div.miu-buttons
                     [:button.btn.btn-default {:on-click (fn [e]
                                                           (a/put! game-ch action)
                                                           (.stopPropagation e))}
                      button-name]])])

(defn handle-game-events! [game-ch !miu-string]
  (go-loop []
    (condp = (a/<! game-ch)
      ::double (prn "double")
      ::append-u (prn "append u")
      ::undo (prn "undo"))
    
    (recur)))

(defn miu-component []
  (let [!miu-state (atom {:miu-string "MIU"})
        game-ch (doto (a/chan)
                  (handle-game-events! !miu-state))]
    (fn []
      [:div.miu-component
       (render-miu-component @!miu-state game-ch)
       (render-buttons game-ch)])))

(set! (.-onload js/window)
      (fn []
        (reagent/render [miu-component] js/document.body)))
