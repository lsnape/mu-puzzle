(ns mu-puzzle.ui.app
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]))

(enable-console-print!)

(defn widget [{:keys [text]} owner]
  (reify
    om/IRender
    (render [this]
      (dom/h1 nil text))))

(om/root widget {:text "Hello world!"}
         {:target (js/document.getElementById "mu-puzzle")})
