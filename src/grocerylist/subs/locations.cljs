(ns grocerylist.subs.locations
  (:require
    [re-frame.core :as re-frame]
    [grocerylist.subs.lists :as lists]))

(re-frame/reg-sub
  ::list
  :<- [::lists/current-list]
  :-> :locations)

(re-frame/reg-sub
  ::options
  :<- [::list]
  (fn [location-list]
    (map (fn [location]
           {:key  location
            :text location
            :value location}) location-list)))

(re-frame/reg-sub
  ::dragged
  :location.dragged)

(re-frame/reg-sub
  ::dragged?
  :<- [::dragged]
  (fn [dragnum [_ itemnum]]
    (= itemnum dragnum)))

(re-frame/reg-sub
  ::get
  :<- [::list]
  (fn [list [_ itemnum]]
    (get list itemnum)))

(re-frame/reg-sub
  ::count
  :<- [::list]
  :-> count)

(re-frame/reg-sub
  ::empty?
  :<- [::list]
  empty?)

(re-frame/reg-sub
  ::first
  :<- [::list]
  first)