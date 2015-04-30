(ns picture-gallery.util
  "Utilities for picture gallery app."
  (:require [noir.session :as session]
            [hiccup.util :refer [url-encode]]
            [environ.core :refer [env]]
            [clojure.string :as str])
  (:import java.io.File))

(def galleries "Get gallery path from environment variable GALLERIES_PATH"
  (env :galleries-path))

(def thumb-size "Define default thumbnail picture size." 150)

(def thumb-prefix "Define prefix used on generated thumbnails images."
  "thumb_")

(defn gallery-path
  "Construct path to gallery for the currently logged in user."
  []
  (str galleries File/separator (session/get :user)))

(defn- encode-filename
  "Deal with encoding of spaces, etc. in filename"
  [file-name]
  (str/replace (url-encode file-name) #"\+" "%20"))

(defn image-uri
  "Construct uri for image file."
  [userid file-name]
  (str "/img/" userid "/" (encode-filename file-name)))

(defn thumb-uri
  "Construct uri for thumbnail image."
  [userid file-name]
  (image-uri userid (str thumb-prefix (encode-filename file-name))))
