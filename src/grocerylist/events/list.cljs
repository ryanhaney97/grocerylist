(ns grocerylist.events.list
  (:require
    [cljs.spec.alpha :as s]
    [re-frame.core :as re-frame]
    [grocerylist.util :as u]
    [grocerylist.events.util :refer [reg-event-persistent-db select-list]]))

(defn add-item [db [_ item-name item-location]]
  (let [id (:items.next-id db)]
    (-> db
        (assoc-in [:items id] {:name item-name
                               :location item-location
                               :checked? false
                               :id id})
        (update :items.next-id inc))))
(reg-event-persistent-db
  ::add-item
  [select-list]
  add-item)

(defn delete-item [db [_ id]]
  (update db :items dissoc id))
(reg-event-persistent-db
  ::delete-item
  [select-list]
  delete-item)

(defn check-item [db [_ id]]
  (update-in db [:items id :checked?] not))
(reg-event-persistent-db
  ::check-item
  [select-list]
  check-item)

(defn update-item-name [db [_ id new-name]]
  (assoc-in db [:items id :name] new-name))
(reg-event-persistent-db
  ::update-item-name
  [select-list]
  update-item-name)

(defn update-item-location [db [_ id new-location]]
  (if (> (.indexOf (:locations db) new-location) -1)
    (assoc-in db [:items id :location] new-location)
    db))
(reg-event-persistent-db
  ::update-item-location
  [select-list]
  update-item-location)

(defn toggle-sort-method [db [_ sort-method]]
  (if (contains? u/sorting-method-map sort-method)
    (if (= sort-method (:sort-method db))
      (update db :sort-reversed? not)
      (assoc db :sort-method sort-method
                :sort-reversed? false))
    db))
(re-frame/reg-event-db
  ::toggle-sort-method
  toggle-sort-method)

(defn update-list-name [db [_ new-name]]
  (assoc db :name new-name))
(reg-event-persistent-db
  ::update-list-name
  [select-list]
  update-list-name)