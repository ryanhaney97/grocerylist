(ns grocerylist.events.locations
  (:require
    [re-frame.core :as re-frame]
    [grocerylist.util :as u]
    [grocerylist.fx :as fx]
    [grocerylist.events.util :refer [select-list reg-event-persistent-db]]))

(reg-event-persistent-db
  ::add
  [select-list]
  (fn [db [_ location]]
    (update db :locations conj location)))

(defn remove-items-with-location [items location]
  (into {}
        (remove (comp #{location} :location val) items)))

(reg-event-persistent-db
  ::delete
  [select-list]
  (fn [db [_ itemnum]]
    (let [location (nth (:locations db) itemnum)]
      (-> db
          (update :locations u/removenth itemnum)
          (update :items remove-items-with-location location)))))

(re-frame/reg-event-fx
  ::confirm-delete
  [select-list]
  (fn [{db :db} [_ itemnum]]
    (let [location (nth (:locations db) itemnum)
          location-counts (frequencies (map :location (vals (:items db))))
          num-references (get location-counts location 0)]
      (if (= num-references 0)
        {:fx [[:dispatch [::delete itemnum]]]}
        {:fx [[::fx/confirm-dialog
               {:message    (str "There are currently " num-references " items located in " location " that will be deleted as well. Do you still want to delete " location " ?")
                :on-confirm #(re-frame/dispatch [::delete itemnum])}]]}))))

(re-frame/reg-event-db
  ::drag-start
  (fn [db [_ itemnum]]
    (assoc db :location.dragged itemnum)))

(re-frame/reg-event-db
  ::drag-end
  (fn [db _]
    (assoc db :location.dragged nil)))

(reg-event-persistent-db
  ::drag-over
  (fn [db [_ itemnum]]
    (->
      db
      (update-in [:lists (:current-list-id db) :locations] u/swap (:location.dragged db) itemnum)
      (assoc :location.dragged itemnum))))