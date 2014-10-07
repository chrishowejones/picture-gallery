(ns picture-gallery.views.layout
  (:require [hiccup.page :refer [html5 include-css]]
            [hiccup.element :refer [link-to]]
            [noir.session :as session]
            [hiccup.form :refer :all]))

(defn guest-menu
  "Display guest menu."
  []
  [:div (link-to "/register" "register")
   (form-to [:post "/login"]
            (text-field {:placeholder "screen name"} "id")
            (password-field {:placeholder "password"} "pass")
            (submit-button "login"))])

(defn user-menu
  "Display user menu."
  [user]
  (list
   [:div (link-to "/upload" "upload images")]
   [:div (link-to "/logout" (str "logout " user))]))

(defn base
  "Create base page and include content."
  [& content]
  (html5
    [:head
     [:title "Welcome to picture-gallery"]
     (include-css "/css/screen.css")]
    [:body content]))

(defn common
  "Create common page elements like logout and login links."
  [& content]
  (base
   (if-let [user (session/get :user)]
     (user-menu user)
     (guest-menu))
   content))




