(ns grocerylist.views.add-item
  (:require
    [re-frame.core :as re-frame]
    [semantic-ui-reagent.core :as sui]
    [grocerylist.subs.locations :as subs.locations]
    [grocerylist.subs.forms.item :as subs.forms.item]
    [grocerylist.subs.errors :as errors]
    [grocerylist.events.forms.item :as events.forms.item]
    [grocerylist.views.util :as u]))

(defn item-name-errors []
  [u/display-errors ::errors/item-form :name])

(defn item-location-errors []
  [u/display-errors ::errors/item-form :location])

(defn item-name-input []
  (let [item-name (re-frame/subscribe [::subs.forms.item/name])
        on-change (fn [event] (re-frame/dispatch-sync [::events.forms.item/update-name (.-value (.-target event))]))]
    (fn []
      [sui/Input {:name "name"}
       [:input {:value @item-name
                :on-change on-change}]])))

(defn item-location-input []
  (let [locations (re-frame/subscribe [::subs.locations/options])
        current-location (re-frame/subscribe [::subs.forms.item/location])
        on-location-change (fn [_ props]
                             (re-frame/dispatch [::events.forms.item/update-location (.-value props)]))]
    (fn []
      [sui/Select {:options @locations
                   :value @current-location
                   :on-change on-location-change
                   :name "location"}])))

(defn reset-button []
  (let [on-click #(re-frame/dispatch [::events.forms.item/reset])]
    (fn []
      [sui/Button {:on-click on-click
                   :type "reset"}
       "Reset"])))

(defn add-button []
  [sui/Button {:primary true
               :type "submit"}
   "Add"])

(defn error-label [errors]
  (when errors
    [sui/Label {:pointing "above"
                :style {:white-space "pre-wrap"}
                :color "red"}
     (apply str (interpose "\n" errors))]))

(defn location-field []
  (let [errors @(re-frame/subscribe [::errors/item-form :location])]
    [sui/FormField {:error (boolean errors)}
     [:label {:for "location"} "Item Location: "]
     [item-location-input]
     [error-label errors]]))

(defn name-field []
  (let [errors @(re-frame/subscribe [::errors/item-form :name])]
    [sui/FormField {:error (boolean errors)}
     [:label {:for "name"} "Item Name: "]
     [item-name-input]
     [error-label errors]]))

(defn add-item-form []
  (let [on-submit #(re-frame/dispatch [::events.forms.item/submit])]
    (fn []
      [sui/Form {:on-submit on-submit}
       [location-field]
       [name-field]
       [reset-button]
       [add-button]])))

(defn add-item-panel []
  (let [locations-empty? @(re-frame/subscribe [::subs.locations/empty?])]
    [sui/Container
     [u/nav-button :list "Back"]
     [sui/Header {:as         "h1"
                  :text-align "center"}
      "Add New Item"]
     (if locations-empty?
       [sui/Container {:text-align "center"}
        [sui/Message {:compact true}
         "This list doesn't have any locations! Please add some first."]]
       [add-item-form])]))