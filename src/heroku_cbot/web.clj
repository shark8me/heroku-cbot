(ns heroku-cbot.web
  (:require [compojure.core :refer [defroutes GET PUT POST DELETE ANY]]
            [compojure.handler :refer [site]]
            [compojure.route :as route]
            [clojure.java.io :as io]
            [cheshire.core :as js]
            [ring.adapter.jetty :as jetty]
            [clj-http.client :as hc]
            [environ.core :refer [env]]))
(def js1
  "{\"object\":\"page\",\"entry\":[{\"id\":\"168497013587504\",\"time\":\"1472831064518\",\"messaging\":[{\"sender\":{\"id\":\"979241748869838\"},\"recipient\":{\"id\":\"168497013587504\"},\"timestamp\":1472829317206,\"message\":{\"mid\":\"mid.1472829314305:d34a5b5612cbea5871\"   ,\"seq\":2,\"text\":\"hello\"}}]}]}")

(def js2 "{\"object\":\"page\",\"entry\":[{\"id\":\"168497013587504\",\"time\":1472884306131,\"messaging\":[{\"sender\":{\"id\":\"979241748869838\"},\"recipient\":{\"id\":\"168497013587504\"},\"timestamp\":1472878078273,\"read\":{\"watermark\":1472878073404,\"seq\":33}},{\"sender\":{\"id\":\"979241748869838\"},\"recipient\":{\"id\":\"168497013587504\"},\"timestamp\":1472878078292,\"message\":{\"mid\":\"mid.1472878078284:346d1579256ff3d713\",\"seq\":34,\"text\":\"22\"}},{\"sender\":{\"id\":\"979241748869838\"},\"recipient\":{\"id\":\"168497013587504\"},\"timestamp\":1472878080384,\"message\":{\"mid\":\"mid.1472878080376:c00414c1a8e2913272\",\"seq\":35,\"text\":\"33\"}},{\"sender\":{\"id\":\"979241748869838\"},\"recipient\":{\"id\":\"168497013587504\"},\"timestamp\":1472878096777,\"message\":{\"mid\":\"mid.1472878096770:6e9cadb75856365a71\",\"seq\":36,\"text\":\"11\"}},{\"sender\":{\"id\":\"979241748869838\"},\"recipient\":{\"id\":\"168497013587504\"},\"timestamp\":0,\"delivery\":{\"mids\":[\"mid.1472877930692:fa1bdf97a40be37a00\"],\"watermark\":1472877930710,\"seq\":37}},{\"sender\":{\"id\":\"979241748869838\"},\"recipient\":{\"id\":\"168497013587504\"},\"timestamp\":0,\"delivery\":{\"mids\":[\"mid.1472877380723:c60d13bfd7168c9743\",\"mid.1472877401022:4542ca485fa3eee906\",\"mid.1472877930692:fa1bdf97a40be37a00\",\"mid.1472878073270:502ffffcbefe4e1232\"],\"watermark\":1472878073404,\"seq\":38}},{\"sender\":{\"id\":\"168497013587504\"},\"recipient\":{\"id\":\"979241748869838\"},\"timestamp\":1472884148102,\"message\":{\"is_echo\":true,\"app_id\":294512704252205,\"mid\":\"mid.1472884148085:2ebc0f3b2d7c116f60\",\"seq\":39,\"text\":\"hello\"}},{\"sender\":{\"id\":\"979241748869838\"},\"recipient\":{\"id\":\"168497013587504\"},\"timestamp\":0,\"delivery\":{\"mids\":[\"mid.1472884148085:2ebc0f3b2d7c116f60\"],\"watermark\":1472884148102,\"seq\":40}},{\"sender\":{\"id\":\"979241748869838\"},\"recipient\":{\"id\":\"168497013587504\"},\"timestamp\":1472884156259,\"read\":{\"watermark\":1472884148102,\"seq\":41}},{\"sender\":{\"id\":\"979241748869838\"},\"recipient\":{\"id\":\"168497013587504\"},\"timestamp\":1472884279023,\"message\":{\"mid\":\"mid.1472884279016:9d805af1d08f586005\",\"seq\":42,\"text\":\"abc\"}}]}]}")

(def jp (js/parse-string js2 true))
(comment
  ((-> jp  :entry first :messaging) 4)
  (->>  (-> jp  :entry first :messaging)
        (remove (fn [k] (or (-> k :read) (-> k :message :is_echo true?) (-> k :delivery))))
        (mapv (fn [k] {(-> k :sender :id) [(-> k :message :text)]}))
        (reduce  (partial merge-with into))))

(def pgtok
  "EAAEL25UeaS0BAECZANlljPUiMwfjsTOrjZAqLZBmwRMZB0ngdDGXkdpZAYIY4Eoieev9gwGULZCOkMggJ9MZAxE0kbTfpEpBWz8hwRWF6epwmAmNAKiLZAIwYZBgtqQNtLl6Li1ZAdohcW6i4nWHznaRh4ulpAAZCkCqBl8WsxlB90xIQZDZD")
(defn read-msg
  [body]
  (try
    (let [jp (-> (js/parse-string body true) :entry first :messaging)]
      (->> jp
           (remove (fn [k] (or (-> k :read) (-> k :message :is_echo) (-> k :delivery))))
           (mapv (fn [k] {(-> k :sender :id) [(-> k :message :text)]}))
           (reduce (partial merge-with into))))
    (catch Exception e
      (clojure.stacktrace/print-stack-trace e)
      (println " err body " (js/parse-string body true))
      {})))
(read-msg js1)
(def send-url "https://graph.facebook.com/v2.6/me/messages?access_token=")
(defn echo-msg
  [m]
  (mapv (fn [[k v]]
          (mapv (fn [v1]
                  (let [jso (js/generate-string {:recipient {:id k}
                                                 :message {:text v1}})
                        resp (:status  (hc/post (str send-url pgtok)
                                                {:body jso
                                                 :content-type :json}))]
                    (println "echomsg " jso)
                    (if (= 200 resp) :OK :ERR))) v)) m))
;(echo-msg {"979241748869838" ["988"]})
;(def g4  (-> js2 read-msg echo-msg))

(comment (hc/post (str send-url pgtok)
                  {:body (js/generate-string {:recipient {:id "979241748869838"}
                                              :message {:text "hello123"}})
                   :content-type :json}))

(defroutes app
  (GET "/" [& z :as p]
    (do (println "/" z)
        (if (.equals pgtok (z "hub.verify_token"))
          (str (z "hub.challenge"))
          (str ""))))
  #_(GET "/subscriptions" [& z :as p]
    (do (println "/subscriptions" z)
        (if (.equals pgtok (z "hub.verify_token"))
          (str (z "hub.challenge"))
          (str ""))))
  (POST "/" [x :as p]
    (let [b (slurp (:body p))
          _ (println "remsg body " b)
          re (read-msg b)
          _ (println "remsg " re)
          resp (echo-msg re)]
      (println (str "post /subscriptions " b " parsed " re " \n " resp))
      ""))
  (ANY "*" [x :as p]
    (do (println " matched ANY " p))
    (route/not-found (slurp (io/resource "404.html")))))

(defn -main [& [port]]
  (let [port (Integer. (or port (env :port) 5000))]
    (jetty/run-jetty (site #'app) {:port port :join? false})))

;; For interactive development:
(comment
  (.stop server)
  (def server (-main)))
