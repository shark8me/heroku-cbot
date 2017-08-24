(ns heroku-cbot.reminder
  (:require [compojure.core :refer [defroutes GET PUT POST DELETE ANY]]
            [compojure.handler :refer [site]]
            [compojure.route :as route]
            [clojure.java.io :as io]
            [cheshire.core :as js]
            [ring.adapter.jetty :as jetty]
            [clj-http.client :as hc]
            [environ.core :refer [env]]))
(def witurl
  "https://api.wit.ai/message")

(defn query-with
  [qtext]
  (let [res (hc/get witurl
                    {:query-params {"v" "20160912" "q" qtext}
                     :headers {"Authorization"
                               "Bearer 2UZDFZVLEXSEERC26GWWG7NAPVQLZ2JY"}})]
    (js/parse-string (:body res) true)))

(defn get-agenda
  [k]
  (let [ent (-> k :entities)
        agenda (cond (:reminder ent) (map :value  (:reminder ent))
                     (:agenda_entry ent) (map :value (:agenda_entry ent))
                     (:message_body ent) (map :value (:message_body ent))
                   :default nil)
        at (if-let [ae (:datetime ent)]
             (map :value ae)  nil)
        intent (if-let [ae (:intent ent)]
             (map :value ae)  nil) 
        ]
    (println "get-agenda " ent)
    {:agenda agenda :at at :intent intent}))

(defn parse
  [k]
  (-> k query-with get-agenda))

(comment
  (get-agenda k1)
  (-> "email Jhan at 11am tomorrow" query-with get-agenda)
  (-> "pick up the car" query-with get-agenda)
  (-> "remind me to pick up the car tomorrow at 7pm" query-with get-agenda)
  (-> "remind me to pick up the car tomorrow at 7pm" query-with get-agenda)

  (parse "show todo list")
  (parse "view todo list")
  (parse "todo list for tomorrow")
  (parse "see tasks")
(def k (query-with "email Johan at 11am tomorrow"))
(def k1 (query-with "email Johan"))
(pr-str k1)
  )
