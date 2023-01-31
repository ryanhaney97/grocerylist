(ns grocerylist.events.list
  (:require
    [re-frame.core :as re-frame]
    [grocerylist.util :as u]
    [grocerylist.events.util :refer [reg-event-persistent-db select-list]]))

(reg-event-persistent-db
  ::add-item
  [select-list]
  (fn [db [_ itemname itemlocation]]
    (let [id (:items.next-id db)]
      (-> db
          (assoc-in [:items id] {:name itemname
                                 :location itemlocation
                                 :checked? false
                                 :id id})
          (update :items.next-id inc)))))

(reg-event-persistent-db
  ::delete-item
  [select-list]
  (fn [db [_ id]]
    (update db :items dissoc id)))

(reg-event-persistent-db
  ::check-item
  [select-list]
  (fn [db [_ id]]
    (update-in db [:items id :checked?] not)))

(reg-event-persistent-db
  ::update-item-name
  [select-list]
  (fn [db [_ id new-name]]
    (assoc-in db [:items id :name] new-name)))

(reg-event-persistent-db
  ::update-item-location
  [select-list]
  (fn [db [_ id new-location]]
    (if (> (.indexOf (:locations db) new-location) -1)
      (assoc-in db [:items id :location] new-location)
      db)))

(re-frame/reg-event-db
  ::toggle-sort-method
  (fn [db [_ sort-method]]
    (if (contains? u/sorting-method-map sort-method)
      (if (= sort-method (:sort-method db))
        (update db :sort-reversed? not)
        (assoc db :sort-method sort-method
                  :sort-reversed? false))
      db)))