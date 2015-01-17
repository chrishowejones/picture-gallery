(ns gallery
  (:require [goog.dom :as dom]
            [domina :refer [by-id nodes append!]]
            [domina.events :refer [listen!]]
            [domina.css :refer [sel]]
            [ajax.core :refer [POST]]))

(defn handle-response [response]
  (js/alert (str response)))

(defn find-selected []
  (->> (sel "input:checked")
       nodes
       (map #(.-name %))
       not-empty))

(defn deleteImages [_]
  (if-let [selected (find-selected)]
    (POST "/delete" {:params {:names selected}
                     :handler handle-response})
    (js/alert "no images selected")))


(defn clickedDelete [_]
  (let [selected (find-selected)]
    (js/alert (str "clicked delete!" (handler (str "handler: " selected))))))

(defn ^:export init []
  (listen! (by-id "delete") :click deleteImages))
