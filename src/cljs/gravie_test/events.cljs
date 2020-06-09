(ns gravie-test.events
  (:require
   [re-frame.core :refer [reg-event-db]]
   [gravie-test.db :as db]
   [gravie-test.middlewares :refer [search-games]]))

(reg-event-db
 :initialize-db
 (fn [_ _]
   db/default-db))

(reg-event-db
 :set-active-panel
 (fn [db [_ active-panel]]
   (assoc db :active-panel active-panel)))

(reg-event-db
 :set-search-value
 (fn [db [_ search-value]]
   (assoc db :search-value search-value)))

(reg-event-db
 :search-games
 [search-games]
 (fn [db _]
   db))

(reg-event-db
 :handle-search-games
 (fn [db [_ body]]
   (let [results (-> body first :results)
         with-price (map #(assoc % :price 5) results)]
     (assoc db :games with-price))))

(reg-event-db
 :handle-error
 (fn [db [_ result]]
   (assoc db :error result)))

(reg-event-db
 :add-to-basket
 (fn [db [_ game]]
   (assoc db :basket (conj (:basket db) game))))

(reg-event-db
 :remove-from-basket
 (fn [db [_ game]]
   (assoc db :basket (remove #{game} (:basket db)))))

(reg-event-db
 :checkout
 (fn [db [_ game]]
   (dissoc db :basket :games :search-value)))

(reg-event-db
 :set-api-key-value
 (fn [db [_ api-key]]
   (assoc db :api-key api-key)))
