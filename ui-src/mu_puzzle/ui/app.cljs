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
    (let [{:keys [action idx]} (a/<! char-ch)
          {:keys [miu-string]} @!miu-state
          highlights (game/idx->group miu-string idx)]
      
      (condp = action
        ::char-over (swap! !miu-state assoc :highlights highlights)
        
        ::char-clicked (when-let [action (get {"III" ::iii->u "UU" ::uu->}
                                              (game/idx->pattern miu-string idx))]
                         
                         (a/put! game-ch {:action action :idx idx}))
        
        ::mouse-out (swap! !miu-state assoc :highlights nil)))
    
    (recur)))

(defn render-miu-char [{:keys [miu-char idx]} !miu-state char-ch]
  (let [marked? (-> @!miu-state
                    :highlights
                    (contains? idx))]

    [:span.miu-ch {:style {:background-color (when marked?
                                               :blue)}

                   :on-mouse-over (fn [e]
                                    (a/put! char-ch {:action ::char-over, :idx idx})
                                    (.stopPropagation e))
                 
                   :on-click (fn [e]
                               (a/put! char-ch {:action ::char-clicked, :idx idx})
                               (.stopPropagation e))}
     miu-char]))

(defn render-miu-component [!miu-state game-ch]
  (let [!char-highlights (-> (:highlights @!miu-state)
                             (wrap swap! !miu-state assoc :highlights))

        char-event-ch (doto (a/chan)
                        (handle-char-events! game-ch !miu-state))]
    (fn []
      [:div {:on-mouse-out (fn [e]
                             (a/put! char-event-ch {:action ::mouse-out})
                             (.stopPropagation e))}
       
       (for [[idx miu-char] (map-indexed vector (:miu-string @!miu-state))]
         ^{:key idx} [render-miu-char {:idx idx, :miu-char miu-char} !miu-state char-event-ch])])))

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

(defn update-miu-string [action idx]
  (condp = action
    ::double game/copy
    ::append-u game/i->iu
    ::iii->u #(game/iii->u % idx)
    ::uu-> #(game/uu-> % idx)))

(defn handle-game-events! [game-ch !miu-state]
  (let [!undo-history (atom nil)

        undo-game-event! #(when-let [[previous-state] (seq @!undo-history)]
                            (swap! !miu-state assoc :miu-string previous-state)
                            (swap! !undo-history rest))]

    (go-loop []
      (let [{:keys [action idx] :as game-event} (a/<! game-ch)]

        (cond
          (= action ::undo) (undo-game-event!)

          :game-events (let [current-miu-string (:miu-string @!miu-state)]
                         (swap! !miu-state update :miu-string (update-miu-string action idx))

                         (when-not (= current-state @!miu-state)
                           (swap! !undo-history (fnil conj (list)) current-miu-string)))))
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
