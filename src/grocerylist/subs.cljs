(ns grocerylist.subs
  (:require
   [re-frame.core :as re-frame]
   [grocerylist.util :as u]))

(re-frame/reg-sub
  ::route
  (fn [db]
    (:route db)))

(re-frame/reg-sub
 ::listname
 (fn [db]
   (:listname db)))

(re-frame/reg-sub
  ::list
  (fn [db]
    (:list db)))

(re-frame/reg-sub
  ::location-list
  (fn [db]
    (:locations db)))

(re-frame/reg-sub
  ::itemform.name
  (fn [db]
    (get-in db [:itemform :name] "")))

(re-frame/reg-sub
  ::itemform.location
  (fn [db]
    (get-in db [:itemform :location] "")))

(re-frame/reg-sub
  ::listitem
  :<- [::list]
  (fn [list [_ itemnum]]
    (get list itemnum)))

(re-frame/reg-sub
  ::location.dragged
  :location.dragged)

(re-frame/reg-sub
  ::location.dragged?
  :<- [::location.dragged]
  (fn [dragnum [_ itemnum]]
    (= itemnum dragnum)))

(re-frame/reg-sub
  ::location-listitem
  :<- [::location-list]
  (fn [list [_ itemnum]]
      (get list itemnum)))

(re-frame/reg-sub
  ::nlocations
  :<- [::location-list]
  :-> count)

(re-frame/reg-sub
  ::nlist
  :<- [::list]
  :-> count)

(re-frame/reg-sub
  ::locationform.name
  (fn [db]
    (get-in db [:locationform :name])))

(re-frame/reg-sub
  ::sort-method
  :-> :sort-method)

(re-frame/reg-sub
  ::sort-reversed?
  :-> :sort-reversed?)

(re-frame/reg-sub
  ::sorted-list
  :<- [::location-list]
  :<- [::list]
  :<- [::sort-method]
  :<- [::sort-reversed?]
  (fn [[location-list list sort-method sort-reversed?]]
    (into [] ((get u/sorting-method-map sort-method) location-list (mapv #(assoc %1 :id %2) list (range)) sort-reversed?))))

(re-frame/reg-sub
  ::sorted-list-item
  :<- [::sorted-list]
  (fn [sorted-list [_ itemnum]]
    (get sorted-list itemnum)))