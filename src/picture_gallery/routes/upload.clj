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
            [noir.util.route :refer [restricted]]
            [picture-gallery.util :refer [gallery-path galleries thumb-prefix thumb-uri thumb-size]]
            [taoensso.timbre :refer [trace debug info warn error fatal]])
  (:import [java.io File FileInputStream FileOutputStream]
           [java.awt.image AffineTransformOp BufferedImage]
           java.awt.RenderingHints
           java.awt.geom.AffineTransform
           javax.imageio.ImageIO))

(defn delete-image
  "Delete an image by userid and name"
  [userid name]
  (try
    (db/delete-image userid name)
    (io/delete-file (str (gallery-path) File/separator name))
    (io/delete-file (str (gallery-path) File/separator thumb-prefix name))
    "ok"
    (catch Exception ex
      (error ex "an error has occurred while deleting " name)
      (.getMessage ex))))

(defn delete-images
  "Delete images that have been selected"
  [names]
  (let [userid (session/get :user)]
    (resp/json
     (for [name names] {:name name :status (delete-image userid name)}))))

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

(defn upload-page [params]
  (layout/render-template "upload.html" params))

(defn handle-upload [{:keys [filename] :as file}]
  (upload-page
   (if (empty? filename)
     {:error "please select a file to upload"}
     (try
       ;; upload file and save thumbnail
       (upload-file (gallery-path) file :create-path? true)
       (save-thumbnail file)
       (db/add-image (session/get :user) filename)
       ;; display thumbnail
       {:image (thumb-uri (session/get :user) filename)}
       (catch Exception ex
         (error ex "an error has occurred while uploading" filename)
         {:error (str "error uploading file " (.getMessage ex))})))))

(defn serve-file [user-id file-name]
  (file-response (str galleries File/separator user-id File/separator file-name)))

(defroutes upload-routes
  (GET "/upload" [info] (restricted (upload-page {:info info})))
  (POST "/upload" [file] (restricted (handle-upload file)))
  (GET "/img/:user-id/:file-name" [user-id file-name] (serve-file user-id file-name))
  (POST "/delete" [names] (restricted (delete-images names))))
