(ns picture-gallery.test.handler
  (:require [noir.util.crypt :refer [encrypt]]
            [picture-gallery.handler :refer :all]
            [ring.mock.request :refer :all]
            [clojure.test :refer :all]
            [kerodon.test :refer :all]
            [kerodon.core :refer :all]
            [net.cgrand.enlive-html :as enlive])
  (:import [java.util HashMap ArrayList]))

(defn mock-get-user [id]
  (if (= id "foo")
    {:id "foo" :pass (encrypt "12345")}))

(deftest test-login
  (testing "login success"
    (with-redefs [picture-gallery.models.db/get-user mock-get-user]
      (is
       (-> (request :post "/login" {:id "foo" :pass "12345"})
           app :headers (get "Set-Cookie")))))
  (testing "password mismatch"
   (with-redefs [picture-gallery.models.db/get-user mock-get-user]
     (is
      (-> (request :post "/login" {:id "foo" :pass "123456"})
          app :headers (get "Set-Cookie") empty?))))
  (testing "user not found"
    (with-redefs [picture-gallery.models.db/get-user mock-get-user]
      (is
       (-> (request :post "/login" {:id "bar" :pass "12345"})
           app :headers (get "Set-Cookie") empty?)))))


(deftest test-delete-images
  (testing "delete images success"
    (with-redefs [noir.session/get (fn [_] "user")
                  picture-gallery.routes.upload/delete-image (fn [_ _] "ok")]
      (is
       (= (str '({:name "image.jpg" :status "ok"} {:name "image2.jpg" :status "ok"}))
        (-> (request :post "/delete" "{:names (\"image.jpg\" \"image2.jpg\")}")
            (content-type "application/edn")
            app
            :body))))))
