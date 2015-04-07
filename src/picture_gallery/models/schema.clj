(ns picture-gallery.models.schema
  (:require [picture-gallery.models.db :refer :all]
            [clojure.java.jdbc :as sql]))

(defn create-users-table []
  (sql/db-do-commands (-database-url)
    (sql/create-table-ddl
     :users
     [:id "varchar(32) PRIMARY KEY"]
     [:pass "varchar(100)"])))

(defn create-images-table []
  (sql/db-do-commands (-database-url)
    (sql/create-table-ddl
     :images
     [:userid "varchar(32)"]
     [:name "varchar(100)"])))

(defn drop-users-table []
  (sql/db-do-commands (-database-url)
                      (sql/drop-table-ddl
                       :users)))

(defn drop-images-table []
  (sql/db-do-commands (-database-url)
                      (sql/drop-table-ddl
                       :images)))

(defn -main []
  (print "Creating database structure...") (flush)
  (create-users-table)
  (create-images-table)
  (println " done"))
