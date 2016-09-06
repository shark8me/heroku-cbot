(ns heroku-cbot.web
  (:require [compojure.core :refer [defroutes GET PUT POST DELETE ANY]]
            [compojure.handler :refer [site]]
            [compojure.route :as route]
            [clojure.java.io :as io]
            [cheshire.core :as js]
            [ring.adapter.jetty :as jetty]
            [clj-http.client :as hc]
            [environ.core :refer [env]]))


;;set these via the command line using
;;heroku config:set verify-token=<your token>
(def verify-token (env :verify-token))
(def page-token (env :page-token))

(def send-url "https://graph.facebook.com/v2.6/me/messages?access_token=")


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


(defn echo-msg
  [m]
  (mapv (fn [[k v]]
          (mapv (fn [v1]
                  (let [jso (js/generate-string {:recipient {:id k}
                                                 :message {:text v1}})
                        resp (:status  (hc/post (str send-url page-token)
                                                {:body jso
                                                 :content-type :json}))]
                    (println "echomsg " jso)
                    (if (= 200 resp) :OK :ERR))) v)) m))

(defroutes app
  (GET "/" [& z :as p]
    (do (println "/" z)
        (if (.equals verify-token (z "hub.verify_token"))
          (str (z "hub.challenge"))
          (str ""))))
  (POST "/" [x :as p]
    (let [b (slurp (:body p))
          re (read-msg b)
          resp (echo-msg re)]
      (println (str "post /" b " parsed " re " \n " resp))
      ""))
  (GET "/env" [& z :as p]
       (str "resp " p " :: " (keys z) " kk :: " (mapv env (keys z))))
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
