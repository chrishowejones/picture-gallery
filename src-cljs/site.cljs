(ns site
  (:require [domina :refer [by-class nodes sel attr]]
            [domina.css :refer [sel]]))

(defn rgb-string
  "returns a string representing the rgb call to set red,
    green and blue values passed."
  [[r g b]]
  (str "rgb(" r ", " g ", " b ")"))

(defn set-color
  "sets the color of the background and foreground."
  [style foreground background]
  (set! (.-color style) (rgb-string foreground))
  (set! (.-backgroundColor style) (rgb-string background)))

(defn img-url
  "Takes a div containing an img tag and retrieves
   the src attribute in the img tag."
  [div]
  (-> div (sel "img") (attr "src")))

(defn set-colors
  "Set the colors on an img using album colors."
  [div]
  (.getColors (js/AlbumColors. (img-url div))
              (fn [[background _ foreground]]
                (set-color (.-style div) foreground background))))

(defn ^:export init []
  (doseq [div (nodes (by-class "thumbnail"))]
    (set-colors div)))
