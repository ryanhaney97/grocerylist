(ns grocerylist.views.add-item
  (:require
    [grocerylist.events.forms.item :as events.forms.item]
    [grocerylist.subs.errors :as errors]
    [grocerylist.subs.forms.item :as subs.forms.item]
    [grocerylist.subs.locations :as subs.locations]
    [grocerylist.views.util :refer [<sub >evt] :as u]
    [re-frame.core :as re-frame]
    [reagent.core :as r]
    [semantic-ui-reagent.core :as sui]))

(defn item-name-input []
  (r/with-let [on-change (fn [event] (re-frame/dispatch-sync [::events.forms.item/update-name (.-value (.-target event))]))]
    [sui/Input {:name "name"}
     [:input {:value (<sub [::subs.forms.item/name])
              :on-change on-change}]]))

(defn item-location-input []
  (r/with-let [on-location-change (fn [_ props]
                                    (re-frame/dispatch [::events.forms.item/update-location (.-value props)]))]
    [sui/Select {:options (<sub [::subs.locations/options])
                 :value (<sub [::subs.forms.item/location])
                 :on-change on-location-change
                 :name "location"}]))

(defn reset-button []
  [sui/Button {:on-click (>evt [::events.forms.item/reset])
               :type "reset"}
   "Reset"])

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
  (let [errors (<sub [::errors/item-form :location])]
    [sui/FormField {:error (boolean errors)}
     [:label {:for "location"} "Item Location: "]
     [item-location-input]
     [error-label errors]]))

(defn name-field []
  (let [errors (<sub [::errors/item-form :name])]
    [sui/FormField {:error (boolean errors)}
     [:label {:for "name"} "Item Name: "]
     [item-name-input]
     [error-label errors]]))

(defn add-item-form []
  [sui/Form {:on-submit (>evt [::events.forms.item/submit])}
   [location-field]
   [name-field]
   [reset-button]
   [add-button]])

(defn add-item-panel []
  [sui/Container
   [u/nav-button :list "Back"]
   [sui/Header {:as         "h1"
                :text-align "center"}
    "Add New Item"]
   (if (<sub [::subs.locations/empty?])
     [sui/Container {:text-align "center"}
      [sui/Message {:compact true}
       "This list doesn't have any locations! Please add some first."]]
     [add-item-form])])