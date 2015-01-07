(ns mu-puzzle.service.css
  (:require [garden.core :as css]
            [garden.units :refer [em]]))

(defn miu-component-css []
  [[:.miu-component {:margin [[(em 5) :auto]]
                     :text-align :center}
    

    [:.miu-ch {:font-family ["courier"]
               :font-size (em 3)}]
    
    [:div.miu-buttons-outer
     [:div.miu-buttons {:display :inline-block
                        :margin [[(em 2) (em 0.2)]]}]]]])

(defn site-css []
  (css/css (concat (miu-component-css))))

;; to check for CSS compilation errors:
(site-css)

