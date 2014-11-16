(ns picture-gallery.test.models.db
  (:require [picture-gallery.models.db :refer :all]
            [environ.core :refer [env]]
            [midje.sweet :refer :all]))

(fact "db should use env var DATABASE_URL to populate itself if set."
      (with-redefs [env {:DATABASE_URL "postgres://xkcgyuwhkrhydu:q2x5JZX9P0Nlc6X7Xshchc1_vQ@ec2-54-204-39-187.compute-1.amazonaws.com:5432/dbtll0s50koo6m"}]
        ((-database-url) :subprotocol) => "postgresql"
        ((-database-url) :subname) => "ec2-54-204-39-187.compute-1.amazonaws.com:5432/dbtll0s50koo6m"
        ((-database-url) :user) => "xkcgyuwhkrhydu"
        ((-database-url) :password) => "q2x5JZX9P0Nlc6X7Xshchc1_vQ"))

(fact "db use env to populate itself if DATABASE_URL not set."
      (with-redefs [env {:db-url "//localhost/gallery",
                         :db-user "admin",
                         :db-pass "admin",
                         :galleries-path "galleries"}]
        ((-database-url) :subprotocol) => "postgresql"
        ((-database-url) :subname) => "//localhost/gallery"
        ((-database-url) :user) => "admin"
        ((-database-url) :password) => "admin"))
