(ns grocerylist.events.locations
  (:require
    [re-frame.core :as re-frame]
    [grocerylist.util :as u]
    [grocerylist.events.util :refer [select-list reg-event-persistent-db]]))

(defn add [db [_ location]]
  (update-in db [:lists (:current-list-id db) :locations] conj location))
(reg-event-persistent-db
  ::add
  add)

(defn remove-items-with-location [items location]
  (into {}
        (remove (comp #{location} :location val) items)))

(defn delete [db [_ itemnum]]
  (let [location (nth (:locations db) itemnum)]
    (-> db
        (update :locations u/removenth itemnum)
        (update :items remove-items-with-location location)
        (#(if (= (get-in %1 [:forms :item :location]) location)
           (update-in %1 [:forms :item] dissoc :location)
           %1)))))
(reg-event-persistent-db
  ::delete
  [select-list]
  delete)

(defn drag-start [db [_ itemnum]]
  (assoc db :location.dragged itemnum))
(re-frame/reg-event-db
  ::drag-start
  drag-start)

(defn drag-end [db _]
  (assoc db :location.dragged nil))
(re-frame/reg-event-db
  ::drag-end
  drag-end)

(defn drag-over [db [_ itemnum]]
  (->
    db
    (update-in [:lists (:current-list-id db) :locations] u/swap (:location.dragged db) itemnum)
    (assoc :location.dragged itemnum)))
(reg-event-persistent-db
  ::drag-over
  drag-over)