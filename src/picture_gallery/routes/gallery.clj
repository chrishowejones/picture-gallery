(ns picture-gallery.routes.gallery
  (:require [compojure.core :refer :all]
           [hiccup.element :refer :all]
           [picture-gallery.views.layout :as layout]
           [picture-gallery.util :refer [thumb-prefix image-uri thumb-uri]]
           [picture-gallery.models.db :as db]
           [noir.session :as session]))

(defn thumbnail-link
  "Display a thumbnail as a link to the larger image."
  [{:keys [userid name]}]
  [:div.thumbnail
   [:a {:href (str "/gallery/" userid)}
    (image (thumb-uri userid name))]])

(defn display-gallery
  "Display gallery for a user."
  [userid]
  (or
   (not-empty (map thumbnail-link (db/images-by-user userid)))
   [:p "The user " userid " does not have any galleries"]))

(defn gallery-link
  "Display a link to the users gallery."
  [{:keys [userid name]}]
  [:div.thumbnail
   [:a {:href (str "/gallery/" userid)}
    (image (thumb-uri userid name))
    userid "'s gallery"]])

(comment
  (defn show-galleries
    "Display the galleries for a user."
    []
    (map gallery-link (db/get-gallery-previews))))

(defroutes gallery-routes
  "Define routes to gallery"
  (GET "/gallery/:userid" [userid] (layout/common (display-gallery userid))))
