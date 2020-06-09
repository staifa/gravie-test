(ns gravie-test.subs
  (:require
   [re-frame.core :as re-frame]))

(re-frame/reg-sub
 ::name
 (fn [db]
   (:name db)))

(re-frame/reg-sub
 ::active-panel
 (fn [db _]
   (:active-panel db)))

(re-frame/reg-sub
 ::search-value
 (fn [db _]
   (:search-value db)))

(re-frame/reg-sub
 ::error
 (fn [db _]
   (:error db)))

(re-frame/reg-sub
 ::games
 (fn [db _]
   (:games db)))

(re-frame/reg-sub
 ::basket
 (fn [db _]
   (:basket db)))

(re-frame/reg-sub
 ::api-key
 (fn [db _]
   (:api-key db)))
