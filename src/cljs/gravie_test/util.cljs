(ns gravie-test.util
  (:require
    [clojure.string :refer [blank?]]
    [clojure.walk :refer [postwalk]]
    [ajax.core :as ajax]
    [camel-snake-kebab.core :refer [->kebab-case]]
    [re-frame.core :refer [dispatch]]))

(defn- format-hashmap-key
  "Formats a given key value pair by a given `format-fn` and returns as a vector."
  [format-fn [k v]]
  (if (or (string? k)
          (keyword? k))
    [(format-fn k) v]
    [k v]))


(defn- format-keys
  "Maps a format function onto a hash-map keys"
  [format-fn data]
  (let [f (partial format-hashmap-key format-fn)]
    (postwalk (fn [x]
                (if (map? x)
                  (into {} (map f x))
                  x))
              data)))


(def ->kebab (partial format-keys ->kebab-case))

(defn- xhrio->response-vec
  "Takes a pure `goog.net.XhrIO` objects and returns it's response as a vector with a response and headers.
   Both items are CLJS maps."
  [xhrio]
  (let [expected-statuses #{200 201 400 401}
        response (if (and (contains? expected-statuses (.getStatus xhrio))
                          (-> xhrio .getResponseText blank? not))
                   (-> xhrio .getResponseJson (js->clj :keywordize-keys true))
                   (.getResponseText xhrio))]
    [response (-> xhrio .getResponseHeaders (js->clj :keywordize-keys true) ->kebab)]))

(defn- xhrio->vec
  [xhrio]
  (cond
    (nil? xhrio) [nil nil 0]
    (zero? (.getStatus xhrio)) [nil nil 0]
    (blank? (.getResponseText xhrio)) [nil
                                       (-> xhrio
                                           .getResponseHeaders
                                           (js->clj :keywordize-keys true)
                                           ->kebab)
                                       (.getStatus xhrio)]
    :else (conj (xhrio->response-vec xhrio) (.getStatus xhrio))))

(defn- wrap-handlers
  "Split xhr object into [response headers] for handlers."
  [handler error-handler kwargs]
  (-> kwargs
      (assoc :handler #(handler (xhrio->vec %)))
      (assoc :error-handler #(error-handler (xhrio->vec (:response %))))))

(def initial-http-params
  {:headers {"Accept" "application/json"}
   :format :json
   :response-format {:read identity :description "raw"}
   :keywords? true})

(defn- call-http-method
  [method url & {:keys [handler error-handler headers format response-format keywords? params]
                 :as kwargs
                 :or {error-handler #(dispatch [:handle-error %])}}]
  (apply method (->> kwargs
                     (merge initial-http-params)
                     (wrap-handlers handler error-handler)
                     (into [])
                     flatten
                     (concat [url]))))


(def GET (partial call-http-method ajax/GET))

(def POST (partial call-http-method ajax/POST))

(def PUT (partial call-http-method ajax/PUT))
