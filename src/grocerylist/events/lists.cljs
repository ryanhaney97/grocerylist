(ns grocerylist.events.lists
  (:require
    [grocerylist.events.util :refer [reg-event-persistent-db]]
    [grocerylist.fx :as fx]
    [re-frame.core :as re-frame]))

(defn new [db [_ name]]
  (let [new-id (:lists.next-id db)]
    (-> db
        (assoc-in [:lists new-id] {:name name
                                   :items {}
                                   :items.next-id 0
                                   :locations ["Unknown"]})
        (update :lists.next-id inc))))
(reg-event-persistent-db
  ::new
  new)

(defn delete [db [_ id]]
  (update db :lists dissoc id))
(reg-event-persistent-db
  ::delete
  delete)

(defn confirm-delete [{db :db} [_ id]]
  (let [current-list-name (get-in db [:lists id :name])]
    {:fx [[::fx/confirm-dialog
           {:message    (str "This will PERMANENTLY delete the list named " current-list-name ", are you sure you still want to delete it?")
            :on-confirm #(re-frame/dispatch [::delete id])}]]}))
(re-frame/reg-event-fx
  ::confirm-delete
  confirm-delete)