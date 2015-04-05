(defproject picture-gallery "0.1.0-SNAPSHOT"
  :description "Application to upload and display pictures."
  :url "http://github.com/chrishowejones/picture-gallery"
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [compojure "1.1.6"]
                 [hiccup "1.0.5"]
                 [ring-server "0.3.1"]
                 [postgresql/postgresql "9.1-901.jdbc4"]
                 [com.h2database/h2 "1.4.186"]
                 [org.clojure/java.jdbc "0.2.3"]
                 [lib-noir "0.7.6"]
                 [com.taoensso/timbre "2.6.1"]
                 [com.postspectacular/rotor "0.1.0"]
                 [environ "0.4.0"]
                 [selmer "0.5.4"]
                 [org.clojure/tools.reader "0.7.10"]
                 [org.clojure/clojurescript "0.0-1806"]
                 [domina "1.0.2"]
                 [cljs-ajax "0.2.2"]
                 [http-kit "2.1.16"]
                 [ragtime/ragtime.sql.files "0.3.7"]]
  :plugins [[lein-ring "0.8.10"]
            [lein-environ "0.4.0"]
            [lein-cucumber "1.0.2"]
            [lein-cljsbuild "0.3.4"]
            [lein-midje "3.0.0"]
            [ragtime/ragtime.lein "0.3.7"]]
  :cucumber-feature-paths ["test/features/"]
  :min-lein-version "2.0.0"
  :ring {:handler picture-gallery.handler/app
         :init picture-gallery.handler/init
         :destroy picture-gallery.handler/destroy}
  :cljsbuild
  {:builds
   {:dev {:source-paths ["src-cljs"]
          :compiler
          {:pretty-print false
           :output-to "resources/public/js/gallery-cljs.js"}}
    :prod {:source-paths ["src-cljs"]
           :compiler
           {:optimizations :advanced
            :externs ["resources/externs.js"]
            :output-to "resources/public/js/gallery-cljs.js"}}}}
  :profiles
  {:uberjar {:aot :all}
   :production
   {:ring
    {:open-browser? false, :stacktrace? false, :auto-reload? false}}
   :dev
   {:dependencies [[ring-mock "0.1.5"]
                   [ring/ring-devel "1.2.1"]
                   [lein-cucumber "1.0.2"]
                   [info.cukes/cucumber-core "1.1.1"]
                   [midje "1.6.3"]
                   [kerodon "0.6.0-SNAPSHOT"]
                   ;; TODO - figure out why adding clj-webdriver causes tests to fail to compile/run
                   ;; [org.apache.httpcomponents/httpclient "4.3.6"]
                   ;; [clj-webdriver "0.6.1"]
                   ]
    :env    {:port 3000,
             :db-subprotocol "postgresql"
             :db-url "//localhost/gallery",
             :db-user "admin",
             :db-pass "",
             :galleries-path "galleries"}}})
