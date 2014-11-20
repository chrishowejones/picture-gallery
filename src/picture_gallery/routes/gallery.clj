(ns picture-gallery.routes.gallery
  (:require [compojure.core :refer :all]
            [hiccup.element :refer :all]
            [hiccup.page :refer :all]
            [hiccup.form :refer [check-box]]
            [picture-gallery.views.layout :as layout]
            [picture-gallery.util :refer [thumb-prefix image-uri thumb-uri]]
            [picture-gallery.models.db :as db]
            [noir.session :as session]
            [picture-gallery.util :refer [gallery-path]]
            [hiccup.element :refer [link-to]]))

(defn display-gallery
  "Display gallery for a user."
  [userid]
  (layout/render-template "gallery.html"
                          {:thumb-prefix thumb-prefix
                           :page-owner userid
                           :pictures (db/images-by-user userid)}))

(defroutes gallery-routes
  "Define routes to gallery"
  (GET "/gallery/:userid" [userid] (display-gallery userid)))
