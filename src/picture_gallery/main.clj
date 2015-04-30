(ns picture-gallery.main
  (:use [picture-gallery.handler :only [app]]
        [org.httpkit.server :only [run-server]]
        [ring.middleware file-info file])
  (:gen-class))

(defn -main
  "Main function invoked when the application is run from command line."
  [& [port]]
  (let [port (if port (Integer/parseInt port) 3000)]
    (run-server app {:port port})
    (println (format "You can view the site on //localhost:%s" port))))
