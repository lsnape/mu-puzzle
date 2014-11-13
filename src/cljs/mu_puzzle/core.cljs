(ns mu-puzzle.core
  (:require [mu-puzzle.util :refer [on]]
            [mu-puzzle.game :as game]
            [om.core :as om :include-macros true]
            [om-tools.dom :as dom :include-macros true]
            [om-tools.core :refer-macros [defcomponent]]
            [sablono.core :as html :refer-macros [html]]
            [cljs.core.async :as a])
  (:require-macros [cljs.core.async.macros :refer [go go-loop]]))

(enable-console-print!)

(def !app-state
  (atom {:mu-string "MI"}))

(defcomponent mu-letter [{:keys [idx letter highlight?] :as mu-char} owner]
  (render-state [_ {:keys [mouse-event-ch]}]
    (html
     [:span {:style {:cursor "default"
                     :background-color (if highlight?
                                         "#eee")}
             
             :on-mouse-over #(a/put! mouse-event-ch
                                     {:action :char-over
                                      :idx idx})

             :on-click #(when highlight?
                          (a/put! mouse-event-ch
                                  {:action :char-clicked
                                   :idx idx}))}
      letter])))

(defcomponent mu-letters [{:keys [mu-string] :as app} owner]
  (init-state [_]
    {:mouse-event-ch (a/chan)})

  (will-mount [_]
    (go-loop []
      (let [{:keys [idx action]} (a/<! (om/get-state owner :mouse-event-ch))]
        (condp = action
          :focus (om/set-state! owner :highlight? true)
          :unfocus (om/set-state! owner :highlight? false)
          :char-over (om/set-state! owner :highlights (game/idx->group (:mu-string @!app-state) idx))
          :char-clicked (om/transact! app :mu-string (fn [mu-string]
                                                       (if (= (get mu-string idx) \I)
                                                         (game/iii->u mu-string idx)
                                                         (game/uu-> mu-string idx))))))
      (recur)))
  
  (render-state [_ {:keys [mouse-event-ch]}]
    (html
     [:p {:style {:text-align "center"
                  :margin "4em auto"}
          
          :on-mouse-over #(a/put! mouse-event-ch {:action :focus})
          :on-mouse-out #(a/put! mouse-event-ch {:action :unfocus})}
      
      (let [{:keys [highlight? highlights]} (om/get-state owner)]
        
        (om/build-all mu-letter
                    
                      (for [[idx letter] (map-indexed vector mu-string)]
                        {:idx idx
                         :letter letter
                         :highlight? (and highlight?
                                          (contains? highlights idx))})
                       
                      {:init-state {:mouse-event-ch mouse-event-ch}}))])))

(defcomponent mu-buttons [_ owner]
  (render-state [_ {:keys [mu-event-ch]}]
    (html
     [:div
      (for [[button-k button-text] {:copy "copy"
                                    :i->iu "append U"
                                    :undo "undo"}]
        
        [:button.btn.btn-default.btn-small
         {:on-click (fn [_]
                      (a/put! mu-event-ch button-k))}
         
         button-text])])))

(defcomponent mu-component [{:keys [mu-string] :as app} owner]
  (init-state [_]
    {:mu-event-ch (a/chan)})

  (will-mount [_]
    (go-loop []
      (let [mu-event-ch (om/get-state owner :mu-event-ch)
            event (a/<! mu-event-ch)]
        
        (condp = event
          :copy (om/transact! app :mu-string game/copy)
          :i->iu (om/transact! app :mu-string game/i->iu)
          :default nil)
        
        (recur))))

  (render-state [_ {:keys [mu-event-ch]}]
    (html
     [:div
      (om/build mu-letters app {:init-state {:mu-event-ch mu-event-ch}})
      (om/build mu-buttons mu-string {:init-state {:mu-event-ch mu-event-ch}})])))

(defn main []
  (om/root
      (fn [app owner]
        (reify
          om/IRender
          (render [_]
            (html
             [:div.container
              [:h1 "MU Puzzle"
               (om/build mu-component app)]]))))
    !app-state
    {:target (. js/document (getElementById "app"))}))
