(ns mu-puzzle.ui.app
  (:require [mu-puzzle.ui.game :as game]
            [clojure.string :as s]
            [cljs.core.async :as a]
            [reagent.core :as reagent :refer [atom]]
            [medley.core :refer [update]]
            simple-brepl.client)
  (:require-macros [cljs.core.async.macros :refer [go go-loop]]))

(enable-console-print!)

(defn render-miu-char [miu-char game-ch char-event-ch]
  [:span.miu-ch {:on-mouse-over (fn [e]
                                  (a/put! game-ch {:action ::char-over})
                                  (.stopPropagation e))
                 
                 :on-click (fn [e]
                             (a/put! game-ch {:action ::char-clicked})
                             (.stopPropagation e))}
   miu-char])

(defn handle-char-events! [char-event-ch game-ch !char-state]
  (go-loop []
    (let [{:keys [action idx]} (a/<! char-event-ch)]
      (cond
        (= action ::char-over) (prn "char over")
        (= action ::char-clicked) (prn "char clicked")))

    (recur)))

(defn render-miu-component [!miu-state game-ch]
  
  (let [char-event-ch (doto (a/chan)
                        (handle-char-events! game-ch (atom nil)))]
    (fn []
      [:div
       (for [[idx miu-char] (map-indexed vector (:miu-string @!miu-state))]
         ^{:key idx} [render-miu-char miu-char char-event-ch])])))

(defn render-buttons [game-ch]
  [:div.miu-buttons-outer
   (for [[action button-name] {::double "double"
                               ::append-u "append U"
                               ::undo "undo"}]
     
     ^{:key action} [:div.miu-buttons
                     [:button.btn.btn-default {:on-click (fn [e]
                                                           (a/put! game-ch {:action action})
                                                           (.stopPropagation e))}
                      button-name]])])

(defn update-miu-string [action]
  (condp = action
    ::double game/copy
    ::append-u game/i->iu
    ::iii->u identity
    ::uu-> identity))

(defn handle-game-events! [game-ch !miu-state]
  (let [!undo-history (atom nil)

        undo-game-event! #(when-let [[previous-state] (seq @!undo-history)]
                            (reset! !miu-state previous-state)
                            (swap! !undo-history rest))]

    (go-loop []
      (let [{:keys [action] :as game-event} (a/<! game-ch)]

        (cond
          (= action ::undo) (undo-game-event!)

          :game-events (let [current-state @!miu-state]
                         (swap! !miu-state update :miu-string (update-miu-string action))

                         (when-not (= current-state @!miu-state)
                           (swap! !undo-history (fnil conj (list)) current-state)))))
      (recur))))

(defn miu-component []
  (let [!miu-state (atom {:miu-string "MI"})

        game-ch (doto (a/chan)
                  (handle-game-events! !miu-state))]
    
    (fn []
      [:div.miu-component
       [render-miu-component !miu-state game-ch]
       [render-buttons game-ch]])))

(set! (.-onload js/window)
      (fn []
        (reagent/render [miu-component] js/document.body)))
