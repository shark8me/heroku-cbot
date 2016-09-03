(ns heroku-cbot.web
  (:require [compojure.core :refer [defroutes GET PUT POST DELETE ANY]]
            [compojure.handler :refer [site]]
            [compojure.route :as route]
            [clojure.java.io :as io]
            [cheshire.core :as js]
            [ring.adapter.jetty :as jetty]
            [environ.core :refer [env]]))
(def js1
"{\"object\":\"page\",\"entry\":[{\"id\":\"168497013587504\",\"time\":\"1472831064518\",\"messaging\":[{\"sender\":{\"id\":\"979241748869838\"},\"recipient\":{\"id\":\"168497013587504\"},\"timestamp\":1472829317206,\"message\":{\"mid\":\"mid.1472829314305:d34a5b5612cbea5871\"   ,\"seq\":2,\"text\":\"hello\"}}]}]}") 

(def pgtok
  "EAAEL25UeaS0BAECZANlljPUiMwfjsTOrjZAqLZBmwRMZB0ngdDGXkdpZAYIY4Eoieev9gwGULZCOkMggJ9MZAxE0kbTfpEpBWz8hwRWF6epwmAmNAKiLZAIwYZBgtqQNtLl6Li1ZAdohcW6i4nWHznaRh4ulpAAZCkCqBl8WsxlB90xIQZDZD")
(defn echo-msg
  [body]
  (-> (js/parse-string body true) :entry first :messaging first :message :text))


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
  (GET "/subscriptions" [ & z :as p]
       (do (println "/subscriptions" z )
       (if (.equals pgtok (z "hub.verify_token"))
       (str (z "hub.challenge"))
       (str ""))))
  
  ;FB sends 2 messages, one post to /subscriptions, and another post to /?*=/subscriptions
  (POST "/" [x :as p] 
       (let [b (slurp (:body p))] 
         (println (str "post / " p " parsed " (echo-msg b))
         "")))
  (POST "/subscriptions" [x :as p] 
       (let [b (slurp (:body p))] 
         (println (str "post /subscriptions " p " parsed " (echo-msg b))
         "")))
  (ANY "*" [x :as p] 
       (do (println " matched ANY " p ))
       (route/not-found (slurp (io/resource "404.html")))))

(defn -main [& [port]]
  (let [port (Integer. (or port (env :port) 5000))]
    (jetty/run-jetty (site #'app) {:port port :join? false})))

;; For interactive development:
(comment
 (.stop server)
 (def server (-main)))
