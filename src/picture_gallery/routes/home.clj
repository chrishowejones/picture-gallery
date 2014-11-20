(ns picture-gallery.routes.home
  (:require [compojure.core :refer :all]
            [picture-gallery.views.layout :as layout]
            [picture-gallery.util :refer [thumb-prefix]]
            [noir.session :as session]
            [picture-gallery.models.db :as db]))

(defn home []
  (layout/render-template "home.html"
                 {:thumb-prefix thumb-prefix
                  :galleries (db/get-gallery-previews) }))

(defroutes home-routes
  (GET "/" [] (home)))
