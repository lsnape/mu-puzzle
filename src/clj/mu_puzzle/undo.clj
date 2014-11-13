(ns mu-puzzle.undo)

(defmacro with-undo-history [data !history & body]
  `(let [current-state# @~data]
     
     ~@body
     
     (if-not (= current-state# @~data)
       (swap! ~!history conj current-state#))))




