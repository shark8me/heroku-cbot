(ns heroku-cbot.web-test
  (:require [clojure.test :refer :all]
            [heroku-cbot.web :as w]))

(def js1
  "{\"object\":\"page\",\"entry\":[{\"id\":\"168497013587504\",\"time\":\"1472831064518\",\"messaging\":[{\"sender\":{\"id\":\"979241748869838\"},\"recipient\":{\"id\":\"168497013587504\"},\"timestamp\":1472829317206,\"message\":{\"mid\":\"mid.1472829314305:d34a5b5612cbea5871\"   ,\"seq\":2,\"text\":\"hello\"}}]}]}")

(def js2 "{\"object\":\"page\",\"entry\":[{\"id\":\"168497013587504\",\"time\":1472884306131,\"messaging\":[{\"sender\":{\"id\":\"979241748869838\"},\"recipient\":{\"id\":\"168497013587504\"},\"timestamp\":1472878078273,\"read\":{\"watermark\":1472878073404,\"seq\":33}},{\"sender\":{\"id\":\"979241748869838\"},\"recipient\":{\"id\":\"168497013587504\"},\"timestamp\":1472878078292,\"message\":{\"mid\":\"mid.1472878078284:346d1579256ff3d713\",\"seq\":34,\"text\":\"22\"}},{\"sender\":{\"id\":\"979241748869838\"},\"recipient\":{\"id\":\"168497013587504\"},\"timestamp\":1472878080384,\"message\":{\"mid\":\"mid.1472878080376:c00414c1a8e2913272\",\"seq\":35,\"text\":\"33\"}},{\"sender\":{\"id\":\"979241748869838\"},\"recipient\":{\"id\":\"168497013587504\"},\"timestamp\":1472878096777,\"message\":{\"mid\":\"mid.1472878096770:6e9cadb75856365a71\",\"seq\":36,\"text\":\"11\"}},{\"sender\":{\"id\":\"979241748869838\"},\"recipient\":{\"id\":\"168497013587504\"},\"timestamp\":0,\"delivery\":{\"mids\":[\"mid.1472877930692:fa1bdf97a40be37a00\"],\"watermark\":1472877930710,\"seq\":37}},{\"sender\":{\"id\":\"979241748869838\"},\"recipient\":{\"id\":\"168497013587504\"},\"timestamp\":0,\"delivery\":{\"mids\":[\"mid.1472877380723:c60d13bfd7168c9743\",\"mid.1472877401022:4542ca485fa3eee906\",\"mid.1472877930692:fa1bdf97a40be37a00\",\"mid.1472878073270:502ffffcbefe4e1232\"],\"watermark\":1472878073404,\"seq\":38}},{\"sender\":{\"id\":\"168497013587504\"},\"recipient\":{\"id\":\"979241748869838\"},\"timestamp\":1472884148102,\"message\":{\"is_echo\":true,\"app_id\":294512704252205,\"mid\":\"mid.1472884148085:2ebc0f3b2d7c116f60\",\"seq\":39,\"text\":\"hello\"}},{\"sender\":{\"id\":\"979241748869838\"},\"recipient\":{\"id\":\"168497013587504\"},\"timestamp\":0,\"delivery\":{\"mids\":[\"mid.1472884148085:2ebc0f3b2d7c116f60\"],\"watermark\":1472884148102,\"seq\":40}},{\"sender\":{\"id\":\"979241748869838\"},\"recipient\":{\"id\":\"168497013587504\"},\"timestamp\":1472884156259,\"read\":{\"watermark\":1472884148102,\"seq\":41}},{\"sender\":{\"id\":\"979241748869838\"},\"recipient\":{\"id\":\"168497013587504\"},\"timestamp\":1472884279023,\"message\":{\"mid\":\"mid.1472884279016:9d805af1d08f586005\",\"seq\":42,\"text\":\"abc\"}}]}]}")

(deftest read
  (let  [k (w/read-msg js1)]
    (is (= ["hello"] (-> k vals first)))))
(deftest read-js2
  (let  [k (w/read-msg js2)]
    (is (= 4 (-> k vals first count)))))

;(read-msg js1)
;(echo-msg {"979241748869838" ["988"]})
;(def g4  (-> js2 read-msg echo-msg))
