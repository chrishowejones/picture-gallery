(ns picture-gallery.util
  (:require [noir.session :as session]
            [hiccup.util :refer [url-encode]])
  (:import java.io.File))

(def galleries "galleries")

(defn gallery-path []
  (str galleries File/separator (session/get :user)))

(def thumb-size 150)

(def thumb-prefix "thumb_")

(defn image-uri [userid file-name]
  (str "/img/" userid "/" (clojure.string/replace (url-encode file-name) #"\+" "%20")))

(defn thumb-uri [userid file-name]
  (image-uri userid (str thumb-prefix file-name)))


