(ns picture-gallery.views.layout-test
  (:require [picture-gallery.views.layout :refer :all]
            [midje.sweet :refer :all]
            [clojure.test :refer [deftest]]))

(deftest test-utf-8-response
  (fact "response header set to utf-8 and text/html."
        (utf-8-response "<html></html>") => (contains {:headers {"Content-Type" "text/html; charset=utf-8"}})))
