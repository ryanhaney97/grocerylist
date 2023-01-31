(ns grocerylist.events.lists
  (:require
    [grocerylist.events.util :refer [reg-event-persistent-db]]))

(reg-event-persistent-db
  ::new
  (fn [db [_ name]]
    (let [new-id (:lists.next-id db)]
      (-> db
          (assoc-in [:lists new-id] {:name name
                                     :items {}
                                     :items.next-id 0
                                     :locations []})
          (update :lists.next-id inc)))))