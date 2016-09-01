(ns heroku-cbot.web
  (:require [compojure.core :refer [defroutes GET PUT POST DELETE ANY]]
            [compojure.handler :refer [site]]
            [compojure.route :as route]
            [clojure.java.io :as io]
            [ring.adapter.jetty :as jetty]
            [environ.core :refer [env]]))

(defn splash []
  {:status 200
   :headers {"Content-Type" "text/plain"}
   :body "Hello from Heroku"})

(defn verify []
  {:status 200
   :headers {"Content-Type" "text/plain"}
   :body "Hello from me"})

(defroutes app
  (GET "/" [& z]
      (do (println "/" z)
       (splash)))
  (GET "/subscriptions" [ & z]
       (do (println "/subscriptions" z)
       (if (.equals "pantulu" (z "hub.verify_token"))
       (str (z "hub.challenge"))
       (str ""))))
  (ANY "*" []
       (route/not-found (slurp (io/resource "404.html")))))

(defn -main [& [port]]
  (let [port (Integer. (or port (env :port) 5000))]
    (jetty/run-jetty (site #'app) {:port port :join? false})))

;; For interactive development:
(comment
 (.stop server)
 (def server (-main)))
