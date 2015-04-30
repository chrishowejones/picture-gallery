(defproject picture-gallery "0.1.0-SNAPSHOT"
  :description "Application to upload and display pictures."
  :url "http://github.com/chrishowejones/picture-gallery"
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [compojure "1.1.6"]
                 [hiccup "1.0.5"]
                 [ring-server "0.3.1"]
                 [org.postgresql/postgresql "9.2-1002-jdbc4"]
                 [com.h2database/h2 "1.4.186"]
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
                 [korma "0.4.0"]
                 [joplin.core "0.2.10"]
                 [joplin.jdbc "0.2.10"]
                 [log4j "1.2.15"
                  :exclusions [javax.mail/mail
                               javax.jms/jms
                               com.sun.jdmk/jmxtools
                               com.sun.jmx/jmxri]]]
  :plugins [[lein-ring "0.8.10"]
            [lein-environ "0.4.0"]
            [lein-cucumber "1.0.2"]
            [lein-cljsbuild "0.3.4"]
            [lein-midje "3.0.0"]
            [joplin.lein "0.2.10"]
            [lein-cloverage "1.0.2"]]
  :source-paths ["src" "joplin"]
  :cucumber-feature-paths ["test/features/"]
  :codox {:src-dir-uri "http://github.com/chrishowejones/picture-gallery/tree/master/"
          :src-linenum-anchor-prefix "L"}
  :min-lein-version "2.0.0"
  :ring {:handler picture-gallery.handler/app
         :init picture-gallery.handler/init
         :destroy picture-gallery.handler/destroy}
  :main picture-gallery.main
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
  :joplin {
           :migrators {:sql-mig "joplin/migrators/sql"}  ;; A path for a folder with migration files
           :databases {:sql-dev {:type :jdbc, :url "jdbc:h2:./gallery?user=sa&password=;database_to_upper=false"}
                       :sql-test {:type :jdbc, :url "jdbc:postgresql://127.0.0.1/circle_test?user=ubuntu"}
                       :sql-prod {:type :jdbc, :url "jdbc:postgresql://ec2-50-16-201-126.compute-1.amazonaws.com:5432/d9a5nmdvh9s83t?user=vvsepkkukewkop&password=kak7gjRMtx7kzt2fK7COzZTg7D"}}
           :environments {:dev [{:db :sql-dev :migrator :sql-mig}]
                          :test [{:db :sql-test :migrator :sql-mig}]
                          :prod [{:db :sql-prod :migrator :sql-mig}]}}
  :profiles
  {:uberjar {:aot :all}
   :production
   {:ring
    {:open-browser? false, :stacktrace? false, :auto-reload? false}
    :env {:galleries-path "galleries"}}
   :dev
   {:dependencies [[ring-mock "0.1.5"]
                   [ring/ring-devel "1.2.1"]
                   [lein-cucumber "1.0.2"]
                   [info.cukes/cucumber-core "1.1.1"]
                   [midje "1.6.3"]
                   [kerodon "0.6.0-SNAPSHOT"]
                   [org.apache.httpcomponents/httpclient "4.3.6"]
                   [clj-webdriver "0.6.1":exclusions [org.clojure/core.cache]]
                   ]
    :env    {:port 3000,
             :host "localhost"
             :db-uri "jdbc:h2:./gallery?user=sa&password=;database_to_upper=false"
             :galleries-path "galleries"}}}
  :aliases
  {
   "migrate-dev" ["do" ["joplin" "migrate" "dev"] ["cloverage"] ["cucumber" "--format" "pretty"]]
   "migrate-test" ["do" ["joplin" "migrate" "test"]]
   })
