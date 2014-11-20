(ns picture-gallery.routes.home-test
  (:require [picture-gallery.routes.home :refer :all]
            [midje.sweet :refer :all]
            [ring.mock.request :refer :all]
            [noir.util.test :as noir-test]
            [picture-gallery.views.layout :as layout]
            [picture-gallery.models.db :as db]))

(fact "Get for / returns home.html templated for dynamic data"
      (with-redefs [db/get-gallery-previews (fn [] (list {:userid "testuser" :row-number 1 :name "testimg.jpg"}))]
        (let [html-body (:body
           (noir-test/with-noir (.render (home) (request :get "/"))))]
          html-body  => (contains "<a href=\"/gallery/testuser\">")
          html-body  => (contains "<img src=\"/img/testuser/thumb_testimg.jpg\"/>"))))      
      

