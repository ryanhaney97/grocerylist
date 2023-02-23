(ns grocerylist.subs.forms.item
  (:require
    [re-frame.core :as re-frame]
    [grocerylist.subs :as subs]
    [grocerylist.subs.locations :as subs.locations]))

(re-frame/reg-sub
  ::form
  :<- [::subs/forms]
  :-> :item)
(re-frame/reg-sub
  ::name
  :<- [::form]
  (fn [form]
    (:name form "")))
(re-frame/reg-sub
  ::location
  :<- [::form]
  :<- [::subs.locations/first]
  (fn [[form default-location]]
    (:location form default-location)))