(ns grocerylist.events.locations
  (:require
    [re-frame.core :as re-frame]
    [grocerylist.util :as u]
    [grocerylist.fx :as fx]
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

(defn confirm-delete [{db :db} [_ itemnum]]
  (let [location (nth (:locations db) itemnum)
        location-counts (frequencies (map :location (vals (:items db))))
        num-references (get location-counts location 0)]
    (if (= num-references 0)
      {:fx [[:dispatch [::delete itemnum]]]}
      {:fx [[::fx/confirm-dialog
             {:message    (str "There are currently " num-references " items located in " location " that will be deleted as well. Do you still want to delete " location " ?")
              :on-confirm #(re-frame/dispatch [::delete itemnum])}]]})))
(re-frame/reg-event-fx
  ::confirm-delete
  [select-list]
  confirm-delete)

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