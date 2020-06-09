(ns gravie-test.views
  (:require
   [re-frame.core :refer [dispatch subscribe]]
   [gravie-test.subs :as subs]
   [gravie-test.events]))

(defn search-panel []
  (let [games (subscribe [::subs/games])
        basket (subscribe [::subs/basket])
        error (subscribe [::subs/error])
        search-value (subscribe [::subs/search-value])]
    [:div
     [:h1 (str "Search games")]

     (if @error
      [:h1 (str "Error occured\n" @error)])

     [:form.form-inline
      [:input {:id "search-field"
               :class "form-control"
               :type "text"
               :value @search-value
               :on-change #(dispatch [:set-search-value (-> % .-target .-value)])}]
      [:button {:class "btn btn-primary"
                :id "search-button"
                :type "button"
                :disabled (= @search-value "")
                :on-click #(dispatch [:search-games])}
       "Search games"]]
     [:h3
      [:a {:href "#/checkout"}
       "Checkout"]]

     [:div (str "You have " (count @basket) " item(s) in your basket.")]

     [:div.list-group
      (doall (for [game @games]
               ^{:key (:guid game)}
               [:div.list-group-item
                [:h3 (:name game)]
                [:img {:src (-> game :image :thumb_url)}]
                [:div (:deck game)]
                [:h4 (str (:price game) "$")]
                (if (some #(= game %) @basket)
                  [:button {:id "remove-from-basket-button"
                            :class "btn btn-danger"
                            :type "button"
                            :on-click #(dispatch [:remove-from-basket game])}
                   "Remove from basket"]
                  [:button {:id "add-to-basket-button"
                            :class "btn btn-success"
                            :type "button"
                            :on-click #(dispatch [:add-to-basket game])}
                   "Add to basket"])]))]]))
     
(defn checkout-panel []
  (let [games (subscribe [::subs/basket])]
    [:div
     [:h1 "Checkout"]

     [:h3
      [:a {:href "#/search"}
       "Back to search"]]
   
     [:div "Order summary"]
     
     [:div.list-group
      (for [game @games]
        ^{:key (:guid game)}
        [:div.list-group-item
         (str (:name game) ": " (:price game) "$")])
      [:div.list-group-item
       (str "Total: " (->> @games (map :price) (reduce +)) "$")]]
     
     [:button {:id "checkout-button"
               :class "btn btn-success"
               :type "button"
               :on-click #(do (dispatch [:checkout])
                              (set! (.. js/window -location -href) "#/thank-you"))}
      "Pay"]]))

(defn thank-you-panel []
  [:div
   [:h1 "Thank you for your purchase!"]
   [:h3
    [:a {:href "#/search"}
     "Back to search"]]])

(defn api-key-panel []
  (let [api-key (subscribe [::subs/api-key])]
    [:div
     [:h1 "Fill in your Giant Bomb API key"]
     [:form.form-inline
       [:input {:id "api-field"
                :class "form-control"
                :type "text"
                :on-change #(dispatch [:set-api-key-value (-> % .-target .-value)])}]
       [:button {:id "api-button"
                 :class "btn btn-primary"
                 :type "button"
                 :disabled (= @api-key "")
                 :on-click #(set! (.. js/window -location -href) "#/search")}
        "Proceed"]]]))

(defn- panels [panel-name]
  (case panel-name
    :search-panel [search-panel]
    :checkout-panel [checkout-panel]
    :thank-you-panel [thank-you-panel]
    :api-key-panel [api-key-panel]
    [:div]))

(defn show-panel [panel-name]
  [panels panel-name])

(defn main-panel []
  (let [active-panel (subscribe [::subs/active-panel])]
    [show-panel @active-panel]))
