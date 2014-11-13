(ns mu-puzzle.game
  (:require [clojure.string :as s]
            [medley.core :refer [filter-keys filter-vals]]))

(defn idx->pattern [mu-string idx]
  (let [letter (get mu-string idx)]
    (get {\I "III", \U "UU"} letter)))

(defn idx->range [mu-string idx]
  (let [ss-length (-> mu-string (idx->pattern idx) count)]
    (->> (range (- (inc idx) ss-length) (+ ss-length idx))
         (remove neg?)
         set)))

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

(defn uu-> [string idx]
  (replace-at string idx "UU" ""))

(defn indexes-of [s ss]
  (->> s
       (partition (count ss) 1)
       (map-indexed vector)
       (filter (comp #{(seq ss)} second))
       (map first)))

(defn substring-steps [start-indexes steps]
  (-> (mapcat #(range % (+ % steps)) start-indexes)
      set))

(defn idx->group [mu-string idx]
  (let [pattern (idx->pattern mu-string idx)
        pattern-length (count pattern)]

    (-> (for [[idx substring] (->> (partition pattern-length 1 mu-string)
                                   (map s/join)
                                   (map-indexed vector))
              
              :when (= substring pattern)]
          
          (->> (iterate inc idx)
               (take pattern-length)
               set))

        first)))
