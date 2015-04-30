(ns picture-gallery.models.db
  "Collection of functions that persist users and their images to a database."
  (:require [clojure.java.jdbc :as sql]
            [environ.core :refer [env]]
            [taoensso.timbre :as timbre]
            [korma.db :refer :all]
            [korma.core :refer :all]))

(defn -database-url
  "Define database url from DATABASE_URL environment variable or
   from db-uri in project file."
  []
  (let [db-map
        (if-let [database_url (env :database-url)]
          database_url
          {:connection-uri (env :db-uri)})
        ]
    (timbre/info db-map)
    db-map))

(defdb korma-db (-database-url))

(defentity users)

(defentity images)

(defn create-user
  "Create user in database. User is a map containing :id :pass keys."
  [user]
  (insert users (values user)))

(defn get-user
  "Get a user from the database if it's :id matches the function argument."
  [id]
  (first (select users
                 (where {:id id})
                 (limit 1))))

(defn add-image [userid imgname]
  (transaction
   (if (empty? (select images
                       (where {:userid userid :name imgname})
                       (limit 1)))
     (insert images (values {:userid userid :name imgname}))
     (throw
      (Exception. "you have already uploaded an image with the same name")))))

(defn images-by-user
  "Fetch all the images uploaded by a user."
  [userid]
  (select images (where {:userid userid})))

(defn get-gallery-previews
  "Get a sequence of the first images for all users"
  []
  (exec-raw
   ["select i1.userid, i1.name
       from images i1
       where name =
       (select name from images i2 where i2.userid=i1.userid limit 1)"
    []]
   :results))


(defn delete-image
  "Delete an image from the database if its :userid and :name matches the supplied argument."
  [userid imgname]
  (delete images (where {:userid userid :name imgname})))

(defn delete-user
  "Delete user from the database, if its :id matches the supplied argument."
  [userid]
  (delete users (where {:id userid})))
