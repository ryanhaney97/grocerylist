(ns grocerylist.subs.forms.item
  (:require
    [re-frame.core :as re-frame]
    [grocerylist.subs :as subs]))

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
  (fn [form]
    (:location form "")))