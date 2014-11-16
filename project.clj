(defproject picture-gallery "0.1.0-SNAPSHOT"
  :description "Application to upload and display pictures."
  :url "http://github.com/chrishowejones/picture-gallery"
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [compojure "1.1.6"]
                 [hiccup "1.0.5"]
                 [ring-server "0.3.1"]
                 [postgresql/postgresql "9.1-901.jdbc4"]
                 [org.clojure/java.jdbc "0.2.3"]
                 [lib-noir "0.7.6"]
                 [com.taoensso/timbre "2.6.1"]
                 [com.postspectacular/rotor "0.1.0"]
                 [environ "0.4.0"]
                 [http-kit "2.1.12"]
                 [org.immutant/web "2.0.0-alpha2"]
                 [org.immutant/caching "2.0.0-alpha2"]
                 [org.immutant/messaging "2.0.0-alpha2"]
                 [org.immutant/scheduling "2.0.0-alpha2"]
                 [clojurewerkz/urly "1.0.0"]
                 [midje "1.6.3"]]
  :plugins [[lein-ring "0.8.10"]
            [lein-environ "0.4.0"]]
  :min-lein-version "2.0.0"
  :ring {:handler picture-gallery.handler/app
         :init picture-gallery.handler/init
         :destroy picture-gallery.handler/destroy}
  :aot :all
  :profiles
  {:production
   {:ring
    {:open-browser? false, :stacktraces? false, :auto-reload? false}
    :env {:port 3000,
             :db-url "//localhost/gallery",
             :db-user "admin",
             :db-pass "admin",
             :galleries-path "galleries"}}
   :dev
   {:dependencies [[ring-mock "0.1.5"] [ring/ring-devel "1.2.1"]]
    :env    {:port 3000,
             :db-url "//localhost/gallery",
             :db-user "admin",
             :db-pass "secretProdPassword",
             :galleries-path "galleries"}}}
  :main picture-gallery.main)
