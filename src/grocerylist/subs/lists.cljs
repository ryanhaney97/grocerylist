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
  ::id-list
  :<- [::lists]
  :-> (comp (partial into []) keys))

(re-frame/reg-sub
  ::list
  :<- [::lists]
  (fn [lists [_ id]]
    (get lists id)))
(re-frame/reg-sub
  ::name
  (fn [[_ id] _]
    (re-frame/subscribe [::list id]))
  :-> :name)

(re-frame/reg-sub
  ::name-list
  :<- [::lists]
  (fn [lists]
    (reduce-kv (fn [coll id list]
                 (conj coll {:id id
                             :name (:name list)})) [] lists)))