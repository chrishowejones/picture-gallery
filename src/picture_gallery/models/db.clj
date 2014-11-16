(ns picture-gallery.models.db
  (:require [clojure.java.jdbc :as sql]
            [environ.core :refer [env]]
            [clojurewerkz.urly.core :refer [url-like host-of port-of path-of user-info-of]]
            [taoensso.timbre :as timbre]))

(defn -database-url []
  (if-let [database_url (env :DATABASE_URL)]
    (let [url (url-like database_url)]
             {:subprotocol "postgresql"
              :subname (str (host-of url) ":" (port-of url) (path-of url))
              :user (get (clojure.string/split (user-info-of url) #":") 0)
              :password (get (clojure.string/split (user-info-of url) #":") 1)})
    {:subprotocol "postgresql"
     :subname (env :db-url)
     :user (env :db-user)
     :password (env :db-pass)}))

(def db
  (let [db-map (-database-url)]
    (do
      (timbre/info "database = " db-map)
      db-map)))

(defmacro with-db [f & body]
  `(sql/with-connection db (~f ~@body)))

(defn create-user [user]
  (with-db
    sql/insert-record :users user))

(defn get-user [id]
  (with-db
    sql/with-query-results res
    ["select * from users where id = ?" id]
    (first res)))

(defn add-image [userid name]
  (with-db
    sql/transaction
    (if (sql/with-query-results
          res
          ["select userid from images where userid = ? and name = ?" userid name]
          (empty? res))
      (sql/insert-record :images {:userid userid :name name})
      (throw
       (Exception. "you have already uploaded an image with the same name")))))

(defn images-by-user
  "Fetch all the images uploaded by a user."
  [userid]
  (with-db
    sql/with-query-results
    res ["select * from images where userid = ?" userid]
    (doall res)))

(defn get-gallery-previews
  "Get first image for users"
  []
  (with-db
    sql/with-query-results
    res
    ["select * from 
        (select *, row_number() over (partition by userid)
             as row_number from images)
             as rows where row_number = 1"]
    (doall res)))

(defn delete-image
  "Delete an image from the database."
  [userid name]
  (with-db
    sql/delete-rows :images ["userid = ? and name = ?" userid name]))

(defn delete-user
  "Delete user from the database."
  [userid]
  (with-db
    sql/delete-rows :users ["id=?" userid]))
