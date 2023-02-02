(ns grocerylist.subs.lists
  (:require [re-frame.core :as re-frame]))

(re-frame/reg-sub
  ::current-list-id
  :-> :current-list-id)

(re-frame/reg-sub
  ::lists
  :-> :lists)

(re-frame/reg-sub
  ::count
  :<- [::lists]
  :-> count)

(re-frame/reg-sub
  ::current-list
  :<- [::lists]
  :<- [::current-list-id]
  (fn [[lists current-list-id]]
    (get lists current-list-id)))

(re-frame/reg-sub
  ::name-list
  :<- [::lists]
  (fn [lists]
    (reduce-kv (fn [coll id list]
                 (conj coll {:id id
                             :name (:name list)})) [] lists)))