(ns picture-gallery.cukes-core
  (:use [clojure.test]
        [leiningen.cucumber]
        [picture-gallery.handler])
  (:require [org.httpkit.server :refer [run-server]]
            [environ.core :refer [env]])
  (:import [cucumber.api.cli Main]))

(comment use-fixtures
  :once
  (fn [f]
    (let [server (run-server
                    #'app
                    {:port (env :port) :join? false})]
      (try
        (f)
        (finally
          (server :timeout 100))))))

(comment
  deftest run-cukes
  (testing "dummy test to invoke cucumber."
    (. Main (main (into-array
                   ["--format" "pretty" "--glue" "test" "test/features"])))))
