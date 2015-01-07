(ns mu-puzzle.game-test
  (:require ;; [mu-puzzle.ui.game :refer :all]
            [clojure.test :refer :all]))

;; TODO cljs testinate

(comment
  (deftest idx->group-test
    (are [mu-string idx group] (= group (idx->group mu-string idx))
         "MIII" 1 #{1 2 3}
         "MIII" 2 #{1 2 3}
         "MIII" 3 #{1 2 3}
         "MIII" 4 nil

         "MIUIUIIIUIIUU" 11 #{11 12}
         "MIUIUIIIUIIUU" 12 #{11 12}
         "MIUIUIIIUIIUU" 13 nil)))

