(ns grocerylist.subs.forms.location
  (:require
    [re-frame.core :as re-frame]
    [grocerylist.subs :as subs]))

(re-frame/reg-sub
  ::form
  :<- [::subs/forms]
  :-> :location)
(re-frame/reg-sub
  ::name
  :<- [::form]
  (fn [form]
    (:name form "")))