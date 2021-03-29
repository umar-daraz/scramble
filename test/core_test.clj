(ns core-test
  (:require [clojure.test :refer [deftest testing is]]
            [scramble.core :refer  [app scramble?]]
            [muuntaja.core :as m]))

(deftest test-scramble
  (testing "Example tests for scramble"
    (is (true? (scramble? "rekqodlw" "world")))
    (is (true? (scramble? "cedewaraaossoqqyt" "codewars")))
    (is (false? (scramble? "katas" "steak")))))

(deftest empty-string-test
  (testing "empty-string"
    (is (thrown? IllegalArgumentException (scramble? "" "Abc"))
        "Empty str1 throws an exception"))
  (testing "Empty str2 throws an exception"
    (is (thrown? IllegalArgumentException (scramble? "Abc" "")
                 "Empty str2 throws an exception"))))

(deftest invalid-characters-test
  (testing "invalid-characters"
    (is (thrown? IllegalArgumentException (scramble? "a-b12" "abc"))
        "Digits or punctuations in str1 throws exception")
    (is (thrown? IllegalArgumentException (scramble? "abc" "a-b12"))
        "Digits or punctuations in str2 throws exception")))

;; (update response :body (m/decode-response-body response))
(defn decode-response-body [response] 
  (let [response-body (m/decode-response-body response)]
    (assoc response :body response-body)))

(defn- request
  ([method uri]
   (request method uri nil))
  ([method uri body]
   (-> (app
        {:uri            uri
         :request-method method
         :headers {"content-type" "application/edn"}
         :body-params    body}) 
        decode-response-body
        (select-keys [:status :body]))))

(request :post "/api/scramble" {:str1 "" :str2 "ABC"})

(deftest test-scramble-api 
  (is (= {:status 200 :body {:scramble true}}
         (request :post "/api/scramble" {:str1 "rekqodlw" :str2 "world"})))
  (is (= {:status 200 :body {:scramble false}}
         (request :post "/api/scramble" {:str1 "katas" :str2 "steak"})))
  (is (= {:status 400 :body {:cause "invalid-data" :error true}}
         (request :post "/api/scramble" {:str1 "" :str2 "Abc"}))))
