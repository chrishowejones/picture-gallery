(ns picture-gallery.handler
  (:require [compojure.core :refer [defroutes]]
            [compojure.route :as route]
            [picture-gallery.routes.home :refer [home-routes]]
            [noir.util.middleware :as noir-middleware]
            [picture-gallery.routes.auth :refer [auth-routes]]
            [picture-gallery.routes.upload :refer [upload-routes]]
            [noir.session :as session]
            [picture-gallery.routes.gallery :refer [gallery-routes]]
            [taoensso.timbre :as timbre]
            [com.postspectacular.rotor :as rotor]
            [ring.middleware.format :refer [wrap-restful-format]]))

(defn user-page
  "Get user from session to use as access rule."
  [_]
  (session/get :user))

(defn init
  "Initialise app"
  []
  (timbre/set-config! [:timestamp-pattern] "yyyy-MM-dd HH:mm:ss")
  (timbre/set-config! [:appenders :error]
                      {:min-level :error
                       :enabled? true
                       :async? false
                       :max-message-per-msecs nil
                       :fn rotor/append})
  (timbre/set-config! [:appenders :rotor]
                      {:min-level :info
                       :enabled? true
                       :async? false
                       :max-message-per-msecs nil
                       :fn rotor/append})
  (timbre/set-config! [:shared-appender-config :error]
                      {:path "error.log" :max-size (* 512 1024) :backlog 10})
  (timbre/set-config! [:shared-appender-config :rotor]
                      {:path "picture-gallery.log"
                       :max-size (* 512 1024)
                       :backlog 10})
  (timbre/info "picture-gallery is starting"))

(defn destroy
  "Tear down for app."
  []
  (timbre/info "picture-gallery is shutting down"))

(defroutes app-routes
  "Define app routes in handler."
  (route/resources "/")
  (route/not-found "Not Found"))

(def
  app
  "Defines the start point of the web application."
  (noir-middleware/app-handler
          [auth-routes
           home-routes
           upload-routes
           gallery-routes
           app-routes]
          :middleware [wrap-restful-format]
          :access-rules [user-page]))
