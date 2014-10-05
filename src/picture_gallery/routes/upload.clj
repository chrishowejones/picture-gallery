(ns picture-gallery.routes.upload
  (:require [compojure.core :refer [defroutes GET POST]]
            [hiccup.form :refer :all]
            [hiccup.element :refer [image]]
            [hiccup.util :refer [url-encode escape-html]]
            [picture-gallery.views.layout :as layout]
            [noir.io :refer [upload-file resource-path]]
            [noir.session :as session]
            [noir.response :as resp]
            [noir.util.route :refer [restricted]]
            [clojure.java.io :as io]
            [ring.util.response :refer [file-response]]
            [picture-gallery.models.db :as db]
            [noir.util.route :refer [restricted]])
  (:import [java.io File FileInputStream FileOutputStream]
           [java.awt.image AffineTransformOp BufferedImage]
           java.awt.RenderingHints
           java.awt.geom.AffineTransform
           javax.imageio.ImageIO))

(def galleries "galleries")

(defn gallery-path []
  (str galleries File/separator (session/get :user)))

(def thumb-size 150)

(def thumb-prefix "thumb_")

(defn scale [img ratio width height]
  (let [scale (AffineTransform/getScaleInstance
               (double ratio) (double ratio))
        transform-op (AffineTransformOp.
                     scale
                     AffineTransformOp/TYPE_BILINEAR)]
    (.filter transform-op img (BufferedImage. width height (.getType img)))))

(defn scale-image [file]
  (let [img (ImageIO/read file)
        img-height (.getHeight img)
        img-width  (.getWidth img)
        img-ratio  (/ thumb-size img-height)]
    (scale img img-ratio (int (* img-width img-ratio)) thumb-size)))

(defn save-thumbnail [{:keys [filename]}]
  (let [path (str (gallery-path) File/separator)]
    (ImageIO/write
     (scale-image (io/input-stream (str path filename)))
     "jpeg"
     (File. (str path thumb-prefix filename)))))

(defn upload-page [info]
  (do
   (println "upload-page called") 
   (layout/common
     [:h2 "Upload an image"]
     [:p info]
     (form-to {:enctype "multipart/form-data"}
              [:post "/upload"]
              (file-upload :file)
              (submit-button "upload")))))

(defn handle-upload [{:keys [filename] :as file}]
  (upload-page
   (if (empty? filename)
     "please select a file to upload"
     (try
       ;; upload file and save thumbnail
       (noir.io/upload-file (gallery-path) file :create-path? true)
       (save-thumbnail file)
       ;; display thumbnail
       (image {:height "150px"}
              (str "/img/"
                   (session/get :user)
                   File/separator
                   thumb-prefix
                   (clojure.string/replace (url-encode filename) #"\+" "%20")))
       (catch Exception ex
         (str "error uploading file " (.getMessage ex)))))))

(defn serve-file [user-id file-name]
  (file-response (str galleries File/separator user-id File/separator file-name)))



(defroutes upload-routes
  (GET "/upload" [info] (restricted (upload-page info)))
  (POST "/upload" [file] (restricted (handle-upload file)))
  (GET "/img/:user-id/:file-name" [user-id file-name] (serve-file user-id file-name)))
