(ns picture-gallery.routes.gallery
  (:require [compojure.core :refer :all]
            [hiccup.element :refer :all]
            [hiccup.page :refer :all]
            [hiccup.form :refer [check-box]]
            [picture-gallery.views.layout :as layout]
            [picture-gallery.util :refer [thumb-prefix image-uri thumb-uri]]
            [picture-gallery.models.db :as db]
            [noir.session :as session]))

(defn thumbnail-link
  "Display a thumbnail as a link to the larger image."
  [{:keys [userid name]}]
  [:div.thumbnail
   [:a {:href (image-uri userid name)}
    (image (thumb-uri userid name))
    (if (= userid (session/get :user)) (check-box name))]])

(defn display-gallery
  "Display gallery for a user."
  [userid]
  (if-let [gallery (not-empty (map thumbnail-link (db/images-by-user userid)))]
    [:div
     [:div#error]
     gallery
     (if (= userid (session/get :user))
       [:input#delete {:type "submit" :value "delete images"}])]  
   [:p "The user " userid " does not have any galleries"]))

(defn gallery-link
  "Display a link to the users gallery."
  [{:keys [userid name]}]
  [:div.thumbnail
   [:a {:href (str "/gallery/" userid)}
    (image (thumb-uri userid name))
    userid "'s gallery"]])

(defn show-galleries
  "Display the galleries for a user."
  []
  (map gallery-link (db/get-gallery-previews)))

(defroutes gallery-routes
  "Define routes to gallery"
  (GET "/gallery/:userid" [userid] (layout/common
                                    (include-js "/js/gallery.js")
                                    (display-gallery userid))))
