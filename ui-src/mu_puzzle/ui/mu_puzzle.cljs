(ns mu-puzzle.ui.mu-puzzle
  (:require [clojure.string :as s]))

(defn i->iu [string]
  (cond-> string
    (re-matches #"M[UI]*I" string) (str "U")))

(defn copy [string]
  (str string (s/join (rest string))))

(defn- replace-at [string idx ss ss-replace]
  (let [[prefix suffix] ((juxt #(s/join (take idx %))
                               #(s/join (drop idx %))) string)]

    (str prefix (s/replace-first suffix (re-pattern ss) ss-replace))))

(defn iii->u [string idx]
  (replace-at string idx "III" "U"))

(defn uuu-> [string idx]
  (replace-at string idx "UU" ""))

(defn indexes-of [s ss]
  (->> s
       (partition (count ss) 1)
       (map-indexed vector)
       (filter (comp #{(seq ss)} second))
       (map first)))

(defn valid-chars [iii-indexes uu-indexes]
  (-> (apply merge
             (mapcat #(range % (+ % 3)) iii-indexes)
             (mapcat #(range % (+ % 2)) uu-indexes))
      set))
