(ns picture-gallery.models.schema
  (:require [picture-gallery.models.db :refer :all]
            [clojure.java.jdbc :as sql]))

(defn create-users-table []
  (sql/with-connection (-database-url)
    (sql/create-table
     :users
     [:id "varchar(32) PRIMARY KEY"]
     [:pass "varchar(100)"])))

(defn create-images-table []
  (sql/with-connection (-database-url)
    (sql/create-table
     :images
     [:userid "varchar(32)"]
     [:name "varchar(100)"])))

(defn -main []
  (print "Creating database structure...") (flush)
  (create-users-table)
  (create-images-table)
  (println " done"))
