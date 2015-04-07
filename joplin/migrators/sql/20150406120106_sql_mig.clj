(ns migrators.sql.20150406120106-sql-mig
  (:use [joplin.jdbc.database])
  (:require [picture-gallery.models.schema :refer :all]))

(defn up [db]
  ;; up migration
  (create-users-table)
  (create-images-table)
  )

(defn down [db]
  ;; down migration
  (drop-images-table)
  (drop-users-table)
  )
