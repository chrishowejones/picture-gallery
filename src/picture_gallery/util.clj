(ns picture-gallery.util
  (:require [noir.session :as session]
            [hiccup.util :refer [url-encode]]
            [environ.core :refer [env]]
            [clojure.string :as str])
  (:import java.io.File))

(def galleries (env :galleries-path))

(defn gallery-path
  "Construct path to gallery for the currently logged in user."
  []
  (str galleries File/separator (session/get :user)))

(def thumb-size 150)

(def thumb-prefix "thumb_")

(defn image-uri [userid file-name]
  (str "/img/" userid "/" (str/replace (url-encode file-name) #"\+" "%20")))

(defn thumb-uri [userid file-name]
  (image-uri userid (str thumb-prefix file-name)))
