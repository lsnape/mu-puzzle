(ns mu-puzzle.ui.app
  (:require [mu-puzzle.ui.game :as game]
            [clojure.string :as s]
            [cljs.core.async :as a]
            [reagent.core :as reagent :refer [atom wrap]]
            [medley.core :refer [update]]
            simple-brepl.client)
  (:require-macros [cljs.core.async.macros :refer [go go-loop]]))

(enable-console-print!)

(defn handle-char-events! [char-ch game-ch !miu-state]
  (go-loop []
    (let [{:keys [action idx]} (a/<! char-ch)]
      (cond
        (= action ::char-over) (prn "over" idx)
        (= action ::char-clicked) (prn "clicked" idx)))

    (recur)))

(defn render-miu-char [{:keys [miu-char idx]} !miu-state char-ch]
  [:span.miu-ch {:on-mouse-over (fn [e]
                                  (a/put! char-ch {:action ::char-over, :idx idx})
                                  (.stopPropagation e))
                 
                 :on-click (fn [e]
                             (a/put! char-ch {:action ::char-clicked, :idx idx})
                             (.stopPropagation e))}
   miu-char])

(defn render-miu-component [!miu-state game-ch]
  (let [!char-state (-> (:highlights @!miu-state)
                        (wrap swap! !miu-state assoc :highlights))

        char-event-ch (doto (a/chan)
                        (handle-char-events! game-ch !char-state))]
    (fn []
      [:div
       (for [[idx miu-char] (map-indexed vector (:miu-string @!miu-state))]
         
         ^{:key idx} [render-miu-char {:idx idx, :miu-char miu-char} !char-state char-event-ch])])))

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
