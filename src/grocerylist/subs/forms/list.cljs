(ns grocerylist.subs.forms.list
  (:require
    [re-frame.core :as re-frame]
    [grocerylist.subs :as subs]))

(re-frame/reg-sub
  ::form
  :<- [::subs/forms]
  :-> :list)
(re-frame/reg-sub
  ::name
  :<- [::form]
  (fn [form]
    (:name form "")))