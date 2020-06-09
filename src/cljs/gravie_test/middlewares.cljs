(ns gravie-test.middlewares
  (:require
   [cemerick.url :refer [url-encode]]
   [re-frame.core :refer [after dispatch]]
   [gravie-test.util :refer [GET]]))

(def search-games
  (after
    (fn [db _]
      (GET (str "https://cors-anywhere.herokuapp.com/"
                "https://www.giantbomb.com/api/search/?"
                "api_key=" (:api-key db) "&"
                "format=json&"
                "query=" (-> db :search-value url-encode) "&"
                "resources=game")
           :handler #(dispatch [:handle-search-games %])
           :error-handler #(dispatch [:handle-error %])))))
