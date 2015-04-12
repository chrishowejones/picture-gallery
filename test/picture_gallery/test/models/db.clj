(ns picture-gallery.test.models.db
  (:require [picture-gallery.models.db :refer :all]
            [environ.core :refer [env]]
            [midje.sweet :refer :all]
            [clojure.test :refer [deftest]])
  (:import [java.lang System]))


(deftest test-database-url
 (fact "db use env to populate itself if DATABASE_URL not set."
        (with-redefs [
                      env {:db-uri "jdbc:postgresql//localhost/gallery?user=admin&password=admin",
                           :galleries-path "galleries"}]
          (-database-url)  => {:connection-uri "jdbc:postgresql//localhost/gallery?user=admin&password=admin"})))
