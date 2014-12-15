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
