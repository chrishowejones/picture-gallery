(ns migrators.sql.20150406120106-sql-mig
  (:use [joplin.jdbc.database])
  (:require [clojure.java.jdbc :as sql]))

(defn create-users-table [db]
  (sql/db-do-commands db
    (sql/create-table-ddl
     :users
     [:id "varchar(32) PRIMARY KEY"]
     [:pass "varchar(100)"])))

(defn create-images-table [db]
  (sql/db-do-commands db
    (sql/create-table-ddl
     :images
     [:userid "varchar(32)"]
     [:name "varchar(100)"])))

(defn drop-users-table [db]
  (sql/db-do-commands db
                      (sql/drop-table-ddl
                       :users)))

(defn drop-images-table [db]
  (sql/db-do-commands db
                      (sql/drop-table-ddl
                       :images)))


(defn up [db]
  ;; up migration
  (create-users-table db)
  (create-images-table db)
  )

(defn down [db]
  ;; down migration
  (drop-images-table db)
  (drop-users-table db)
  )
