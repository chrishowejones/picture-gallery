(ns cukes-core
  (:use [clojure.test])
  (:use [leiningen.cucumber])
  (:import [cucumber.api.cli Main]))

(deftest run-cukes []
  (. Main (main (into-array ["--format" "pretty" "--glue" "test" "test/features"])))
  (println "run-cukes"))
