(ns picture-gallery.routes.auth
  (:require [hiccup.form :refer :all]
            [compojure.core :refer :all]
            [picture-gallery.routes.home :refer :all]
            [picture-gallery.views.layout :as layout]
            [noir.session :as session]
            [noir.response :as resp]
            [noir.validation :as vali]
            [noir.util.crypt :as crypt]
            [picture-gallery.models.db :as db]
            [picture-gallery.util :refer [gallery-path]]
            [noir.util.route :refer [restricted]]
            [picture-gallery.routes.upload :refer [delete-image]]
            [taoensso.timbre :as timbre])
  (:import java.io.File))

(defn create-gallery-path
  "Create path to gallery directory for user.
   Create directory if it's not present."
  []
  (let [user-path (File. (gallery-path))]
    (if-not (.exists user-path)
      (.mkdir user-path))
    (str (.getAbsolutePath user-path) File/separator)))

(defn valid?
  "Check if user and password are valid.
   Store error messages if not returns true if valid."
  [id pass pass1]
  (vali/rule (vali/has-value? id)
             [:id "user id is required"])
  (vali/rule (vali/min-length? pass 5)
             [:pass "password must be at least 5 characters"])
  (vali/rule (= pass pass1)
             [:pass "entered passwords do not match"])
  (vali/rule (nil? (db/get-user id))
             [:id (str "The user with id " id " already exists!")])
  (not (vali/errors? :id :pass :pass1)))

(defn error-item
  "Render error item."
  [[error]]
  [:div.error error])

(defn control
  "Render a page control from the id, label and field."
  [id label field]
  (list
   (vali/on-error id error-item)
   label field
   [:br]))

(defn registration-page
  "Render registration page."
  [& [id]]
  (layout/render-template
   "registration.html"
   {:id id
    :id-error (first (vali/get-errors :id))
    :pass-error (first (vali/get-errors :pass))}))

(defn format-error
  "Format error messages on page."
  [id ex]
  (do
    (timbre/error ex)
    (str  "An error has occurred while processing the request. Error is " ex)))

(defn handle-registration
  "Register user."
  [id pass pass1]
  (if (valid? id pass pass1)
    (do
     (timbre/info (str "Registering " id))
     (try
        (db/create-user {:id id :pass (crypt/encrypt pass)})
        (session/put! :user id)
        (create-gallery-path)
        (resp/redirect "/")
        (catch Exception ex
          (vali/rule false [:id (format-error id ex)])
          (registration-page))))
    (registration-page id)))

(defn handle-login
  "Login user."
  [id pass]
  (let [user (db/get-user id)]
    (and user (crypt/compare pass (user :pass))
         (do
           (timbre/info (str "Logged on as " id))
           (session/put! :user id))))
  (resp/redirect "/"))

(defn handle-logout
  "Logout user."
  []
  (timbre/infof "Logout %s" (session/get :user) )
  (session/clear!)
  (resp/redirect "/"))

(defn delete-account-page
  "Render delete account page."
  []
  (layout/render-template "deleteAccount.html"))

(defn handle-confirm-delete
  "Handle the confirmation of the deletion of an account."
  []
  (let [user (session/get :user)]
    (doseq [{:keys [name]} (db/images-by-user user)]
      (delete-image user name))
    (clojure.java.io/delete-file (gallery-path))
    (db/delete-user user))
  (session/clear!)
  (resp/redirect "/"))

(defroutes auth-routes
  (GET "/register" []
       (registration-page))
  (POST "/register" [id pass pass1]
        (handle-registration id pass pass1))
  (POST "/login" [id pass]
        (handle-login id pass))
  (GET "/logout" []
       (handle-logout))
  (GET "/delete-account" []
       (restricted (delete-account-page)))
  (POST "/confirm-delete" []
        (restricted (handle-confirm-delete))))
