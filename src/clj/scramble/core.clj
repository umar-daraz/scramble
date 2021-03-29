(ns scramble.core (:gen-class)
    (:require [ring.adapter.jetty :as jetty]
              [reitit.ring :as ring]
              [muuntaja.core :as m]
              [reitit.ring.middleware.muuntaja :as muuntaja]))

(defn scramble? [str1 str2]
  (if (and (re-matches #"[a-z]+" str1)
           (re-matches #"[a-z]+" str2))
    (let [str1-letter->count (frequencies str1)
          str2-letter->count (frequencies str2)]
      (every? (fn [[letter letter-count]]
                (<= letter-count (get str1-letter->count letter 0))) str2-letter->count))
    (throw (IllegalArgumentException.))))

(defn scramble-handler [{{:keys [str1 str2]} :body-params}]
  (try
    {:status 200
     :body {:scramble (scramble? str1 str2)}}
    (catch java.lang.IllegalArgumentException _
      {:status 400
       :body
       {:error true
        :cause :invalid-data}})))

(def app
  (ring/ring-handler
   (ring/router
    ["/api"
     ["/scramble" {:post  scramble-handler}]]
    {:data {:muuntaja m/instance
            :middleware [muuntaja/format-middleware]}})
   (ring/routes
    (ring/create-resource-handler {:path "/"})
    (ring/create-default-handler))))

(defn -main []
  (println "Starting server at port 3000")
  (jetty/run-jetty app {:port  3000
                        :join? false}))

(comment
  (jetty/run-jetty #'app {:port  3000
                          :join? false}))
