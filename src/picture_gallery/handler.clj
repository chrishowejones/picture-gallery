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
            [com.postspectacular.rotor :as rotor]))

(defn user-page [_]
  (session/get :user))

(defn init []
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
                      {:path "picture-gallery.log" :max-size (* 512 1024) :backlog 10})
  (timbre/info "picture-gallery is starting"))

(defn destroy []
  (timbre/info "picture-gallery is shutting down"))

(defroutes app-routes
  (route/resources "/")
  (route/not-found "Not Found"))

(def app (noir-middleware/app-handler [auth-routes
                                home-routes
                                upload-routes
                                gallery-routes
                                app-routes]
                               :access-rules [user-page]))
