(defproject heroku-cbot "1.0.0-SNAPSHOT"
  :description "web app"
  :url "http://clojure-getting-started.herokuapp.com"
  :license {:name "Eclipse Public License v1.0"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [compojure "1.4.0"]
                 [ring/ring-jetty-adapter "1.4.0"]
                 [cheshire "5.6.3"]
                 [clj-http "2.2.0"]
                 [environ "1.1.0"]]
  ;:ring  {:handler hello-world.core/handler}
  :min-lein-version "2.0.0"
  :plugins [[lein-environ "1.1.0"]]
  ;:hooks [environ.leiningen.hooks]
  :uberjar-name "heroku-cbot.jar"
  :profiles {:production {:env {:production false}}})
