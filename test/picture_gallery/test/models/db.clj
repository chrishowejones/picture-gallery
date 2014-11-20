(ns picture-gallery.test.models.db
  (:require [picture-gallery.models.db :refer :all]
            [environ.core :refer [env]]
            [midje.sweet :refer :all])
  (:import [java.lang System]))


(fact "db use env to populate itself if DATABASE_URL not set."
      (with-redefs [env {:db-url "//localhost/gallery",
                         :db-user "admin",
                         :db-pass "admin",
                         :galleries-path "galleries"}]
        ((-database-url) :subprotocol) => "postgresql"
        ((-database-url) :subname) => "//localhost/gallery"
        ((-database-url) :user) => "admin"
        ((-database-url) :password) => "admin"))
