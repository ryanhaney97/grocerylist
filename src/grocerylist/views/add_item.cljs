(ns grocerylist.views.add-item
  (:require
    [re-frame.core :as re-frame]
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
  (u/form-text-input-factory
    "name"
    ::subs.forms.item/name
    ::events.forms.item/update-name
    ::events.forms.item/submit))
(defn item-location-input []
  (let [locations (re-frame/subscribe [::subs.locations/list])
        current-location (re-frame/subscribe [::subs.forms.item/location])
        on-location-change (fn [event]
                             (re-frame/dispatch [::events.forms.item/update-location (.-value (.-target event))]))]
    (fn []
      [:select {:value @current-location
                :on-change on-location-change
                :name "location"}
       (map
         (fn [location]
           [:option {:value location
                     :key location}
            location]) @locations)])))

(def reset-button
  (u/button-factory
    "Reset"
    (fn []
      (re-frame/dispatch [::events.forms.item/reset]))))

(def add-button
  (u/button-factory
    "Add"
    (fn []
      (re-frame/dispatch [::events.forms.item/submit]))))
(defn add-item-panel []
  [:div
   [u/nav-button :list "Back"]
   [:h1 "Add New Item"]
   [:div
    [:label {:for "location"} "Item Location: "]
    [item-location-input]
    [item-location-errors]]
   [:div
    [:label {:for "name"} "Item Name: "]
    [item-name-input]
    [item-name-errors]]
   [reset-button]
   [add-button]])