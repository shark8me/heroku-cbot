(ns heroku-cbot.gkg
  ;;API for the google knowledge graph
  (:require [compojure.core :refer [defroutes GET PUT POST DELETE ANY]]
            [compojure.handler :refer [site]]
            [compojure.route :as route]
            [clojure.java.io :as io]
            [cheshire.core :as js]
            [ring.adapter.jetty :as jetty]
            [clj-http.client :as hc]
            [environ.core :refer [env]]))

(def gkg-url "https://kgsearch.googleapis.com/v1/entities:search")

