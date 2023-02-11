(ns grocerylist.subs.list
  (:require
    [grocerylist.util :as u]
    [re-frame.core :as re-frame]
    [grocerylist.subs.lists :as lists]
    [grocerylist.subs.locations :as locations]))

(re-frame/reg-sub
  ::name
  :<- [::lists/current-list]
  :-> :name)

(re-frame/reg-sub
  ::items
  :<- [::lists/current-list]
  :-> :items)

(re-frame/reg-sub
  ::list
  :<- [::items]
  :-> vals)

(re-frame/reg-sub
  ::sort-method
  :-> :sort-method)

(re-frame/reg-sub
  ::sort-reversed?
  :-> :sort-reversed?)

(re-frame/reg-sub
  ::sorted
  :<- [::locations/list]
  :<- [::list]
  :<- [::sort-method]
  :<- [::sort-reversed?]
  (fn [[location-list list sort-method sort-reversed?]]
    ((get u/sorting-method-map sort-method) location-list list sort-reversed?)))

(re-frame/reg-sub
  ::sorted-ids
  :<- [::sorted]
  (fn [sorted-list]
    (map :id sorted-list)))

(re-frame/reg-sub
  ::item-by-id
  :<- [::items]
  (fn [items [_ id]]
    (get items id)))

(re-frame/reg-sub
  ::item-name-lengths
  :<- [::items]
  (fn [items]
    (map (comp count :name) (vals items))))

(re-frame/reg-sub
  ::max-item-length
  :<- [::item-name-lengths]
  (fn [lengths]
    (apply max lengths)))

(re-frame/reg-sub
  ::item-count
  :<- [::items]
  :-> count)

(re-frame/reg-sub
  ::name.edited
  (fn [db]
    (get-in db [:edit :list :name])))

(re-frame/reg-sub
  ::name.editing?
  :<- [::name.edited]
  some?)