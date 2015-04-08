(ns picture-gallery.models.db
  (:require [clojure.java.jdbc :as sql]
            [environ.core :refer [env]]
            [taoensso.timbre :as timbre]
            [korma.db :refer :all]
            [korma.core :refer :all]))

(defn -database-url []
  (let [db-map
        (if-let [database_url (env :DATABASE_URL)]
          database_url
          {:connection-uri (env :db-uri)})
        ]
    (timbre/info db-map)
    db-map))

(defonce db (-database-url))

(defdb korma-db4 db)

(defentity users)

(defentity images)

(defn create-user [user]
  (insert users (values user)))

(defn get-user [id]
  (first (select users
                 (where {:id id})
                 (limit 1))))

(defn add-image [userid name]
  (transaction
   (if (empty? (select images
                       (where {:userid userid :name name})
                       (limit 1)))
     (insert images (values {:userid userid :name name}))
     (throw
      (Exception. "you have already uploaded an image with the same name")))))

(defn images-by-user
  "Fetch all the images uploaded by a user."
  [userid]
  (select images (where {:userid userid})))

(defn get-gallery-previews
  "Get first image for users"
  []
  (exec-raw
   ["select i1.userid, i1.name
       from images i1
       where name = (select name from images i2 where i2.userid=i1.userid limit 1)" []]
   :results))


(defn delete-image
  "Delete an image from the database."
  [userid name]
  (delete images (where {:userid userid :name name})))

(defn delete-user
  "Delete user from the database."
  [userid]
  (delete users (where {:id userid})))
