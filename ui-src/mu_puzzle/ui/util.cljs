(ns mu-puzzle.ui.util)

(defn on [$el event f]
  (.addEventListener js/document (name event) f))
