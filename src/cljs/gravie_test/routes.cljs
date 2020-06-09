(ns gravie-test.routes
  (:require-macros [secretary.core :refer [defroute]])
  (:import [goog History]
           [goog.history EventType])
  (:require
   [secretary.core :as secretary]
   [goog.events :as gevents]
   [re-frame.core :as re-frame]
   [gravie-test.events]))
   

(defn hook-browser-navigation! []
  (doto (History.)
    (gevents/listen
     EventType/NAVIGATE
     (fn [event]
       (secretary/dispatch! (.-token event))))
    (.setEnabled true)))

(defn app-routes []
  (secretary/set-config! :prefix "#")

  (defroute "/" []
    (re-frame/dispatch [:set-active-panel :api-key-panel]))
    
  (defroute "/search" []
    (re-frame/dispatch [:set-active-panel :search-panel]))

  (defroute "/checkout" []
    (re-frame/dispatch [:set-active-panel :checkout-panel]))

  (defroute "/thank-you" []
    (re-frame/dispatch [:set-active-panel :thank-you-panel]))

  (hook-browser-navigation!))
