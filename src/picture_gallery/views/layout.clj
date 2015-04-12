(ns picture-gallery.views.layout
  (:require [noir.session :as session]
            [ring.util.response :refer [content-type response]]
            [compojure.response :refer [Renderable]]
            [selmer.parser :as parser]))

(def template-folder "picture_gallery/views/templates/")

(defn utf-8-response
  "Set response header to utf-8 and text/html"
  [html]
  (content-type (response html) "text/html; charset=utf-8"))

(deftype RenderablePage [template params]
  Renderable
  (render [this request]
    (->> (assoc params
                :context (:context request)
                :user (session/get :user))
         (parser/render-file (str template-folder template))
         utf-8-response)))

(defn render-template [template & [params]]
  (RenderablePage. template params))

(defn base
  "Create base page and include content."
  [& content]
  (comment RenderablePage. content))
