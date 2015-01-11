(ns mu-puzzle.service.css
  (:require [garden.core :as css]
            [garden.units :refer [em percent]]))

(def bg-color
  "rgb(255, 215, 0)")

(defn miu-component-css []
  [[:.miu-component {:margin [[(em 7) :auto]]
                     :text-align :center}
    
    [:.miu-ch {:font-family ["courier"]
               :display :inline-block
               :color "rgb(100,100,100)"
               :cursor :default
               :font-size (em 3)}]

    [:div.miu-string {:margin [[(em 0) :auto]]
                      :max-width (percent 90)}]
    
    [:div.miu-buttons-outer {:position :fixed
                             :background-color "rgb(255, 215, 0)"
                             :opacity 0.95
                             :width (percent 100)
                             :top (em 0)}
     
     [:div.miu-buttons {:display :inline-block
                        :margin [[(em 2) (em 0.2)]]}]]]])

(defn site-css []
  (css/css (concat [[:body {:background-color bg-color}]]
                   (miu-component-css))))

;; to check for CSS compilation errors:
(site-css)
