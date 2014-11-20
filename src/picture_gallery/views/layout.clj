(ns picture-gallery.views.layout
  (:require [hiccup.page :refer [html5 include-css include-js]]
            [hiccup.element :refer [link-to]]
            [noir.session :as session]
            [hiccup.form :refer :all]
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

(defn make-menu
  "Make menu."
  [& items]
  [:div#usermenu (for [item items]
          [:div.menuitem item])])

(defn guest-menu
  "Display guest menu."
  []
  (make-menu
   (link-to "/" "home")
   (link-to "/register" "register")
   (form-to [:post "/login"]
            (text-field {:placeholder "screen name"} "id")
            (password-field {:placeholder "password"} "pass")
            (submit-button "login"))))

(defn user-menu
  "Display user menu."
  [user]
  (make-menu
   (link-to "/" "home")
   (link-to "/upload" "upload images")
   (link-to "/logout" (str "logout " user))
   (link-to "/delete-account" "delete account")))

(defn base
  "Create base page and include content."
  [& content]
  (comment RenderablePage. content))

(defn common
  "Create common page elements like logout and login links."
  [& content]
  (base
   (if-let [user (session/get :user)]
     (user-menu user)
     (guest-menu))
   [:div.content content]))




