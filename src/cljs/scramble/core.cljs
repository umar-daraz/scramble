(ns scramble.core)

(def messages
  {:scramble "Success: Values are scramble"
   :no-scramble "Failed: Values are not scramble"
   :invalid-data "Invalid: Strings should not contains lower case letters"})

(defn scramble? [str1 str2]
  (-> (js/fetch "/api/scramble"
                (clj->js  {:method "POST"
                           :body (.stringify js/JSON (clj->js {:str1 str1 :str2 str2}))
                           :headers {"Content-Type" "application/json"}}))
      (.then #(.json %))
      (.then #(js->clj %))))

(defn input-value [element-id]
  (.-value (.getElementById js/document element-id)))

(defn show-message [msg]
  (-> js/document
      (.getElementById "message")
      (.-innerHTML)
      (set! msg)))

(defn init []
  (-> (.getElementById js/document "submit-btn")
      (.addEventListener "click"
                         (fn []
                           (-> (scramble? (input-value "str1") (input-value "str2"))
                               (.then (fn [data]
                                        (if (not (get data "error" false))
                                          (show-message (if (get data "scramble")
                                                          (:scramble messages)
                                                          (:no-scramble messages)))
                                          (show-message ((keyword (get data "cause")) messages))))))))))
