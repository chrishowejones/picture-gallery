(ns picture-gallery.routes.upload-test
  (:require [picture-gallery.routes.upload :refer :all]
            [midje.sweet :refer :all]
            [noir.util.test :as noir-test]
            [ring.mock.request :refer :all]
            [noir.session :as session]
            [clojure.test :refer [deftest]]))

(deftest test-delete-images-upload
 (fact "delete-images should delete the selected image for the user"
       (let [userid "testuser" name "filename.jpg"]
         (with-redefs [delete-image (fn [userid name] "ok")
                       session/get (fn [_] userid)
                       ]
           (read-string  (:body (delete-images [{:name name}]))) => (contains {:name {:name "filename.jpg"} :status "ok"})
           (comment contains (str "{:name {:name \"filename.jpg\"}, :status \"ok\"}"))
           (:headers (delete-images [{:name name}])) => {"Content-Type" "application/edn; charset=utf-8"}))))
