(ns try-stuff
  (:require [try-stuff :refer :all]
            [midje.sweet :refer :all]))

(defn factorial [n]
  (if (zero? n)
    1
    (* n (factorial (dec n)))))

(defn factorial-recur
  ([n] (factorial-recur 1 n))
  ([acc n] (if (zero? n)
             acc
             (recur (* acc n) (dec n)))))

(defn factorial-reduce [n]
  (reduce * (range 1N (inc n))))



(defprotocol JSON
  (to-json [this]))

(extend-protocol JSON
  clojure.lang.Keyword
  (to-json [data]
    (to-json (name data)))
  java.lang.String
  (to-json [data]
    (str "\"" data "\""))
  clojure.lang.Symbol
  (to-json [data]
    (to-json (name data)))
  clojure.lang.Seqable
  (to-json [data]
    (map to-json data)))

(to-json "fred")

(to-json :fred)

(to-json 'fred)

(map to-json [:a "fred"])

(nth (.split #" " (with-out-str (time (factorial-reduce 100)))) 2)

(let [values (repeat 5000 (with-out-str (time (factorial-reduce 100))))
      doubles (map #(Double. (nth (.split #" " %) 2)) values)]
  (str "Count : " (count doubles) ", "
   "Average time : "
   (/ (apply + doubles)
      (count doubles))))

(def fib
  (lazy-cat [0N 1N] (map + (rest fib) fib)))

(take 1 (drop 40000 fib))

(take 20 (iterate (fn [[a b]] [b (+ a b)]) [0 1]))
