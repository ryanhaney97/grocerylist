(ns grocerylist.subs.item
  (:require
    [re-frame.core :as re-frame]
    [grocerylist.subs :as subs]
    [grocerylist.subs.list :as subs.list]))

(re-frame/reg-sub
  ::item
  :<- [::subs.list/items]
  (fn [items [_ id]]
    (get items id)))

(defn item-id-signal-fn [[_ id] _]
  (re-frame/subscribe [::item id]))

(re-frame/reg-sub
  ::name
  item-id-signal-fn
  :-> :name)

(re-frame/reg-sub
  ::location
  item-id-signal-fn
  :-> :location)

(re-frame/reg-sub
  ::checked?
  item-id-signal-fn
  :-> :checked?)

(re-frame/reg-sub
  ::name.edited
  :<- [::subs/edits]
  (fn [edits [_ id]]
    (get-in edits [:items id :name])))

(re-frame/reg-sub
  ::name.editing?
  (fn [[_ id] _]
    (re-frame/subscribe [::name.edited id]))
  :-> some?)