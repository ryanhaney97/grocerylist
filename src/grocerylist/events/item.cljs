(ns grocerylist.events.item
  (:require
    [re-frame.core :as re-frame]
    [cljs.spec.alpha :as s]
    [grocerylist.spec.list :as spec.list]
    [grocerylist.fx :as fx]
    [grocerylist.events.util :refer [select-list reg-event-persistent-db]]))

(defn delete [db [_ id]]
  (update db :items dissoc id))
(reg-event-persistent-db
  ::delete
  [select-list]
  delete)

(defn update-name [db [_ id new-name]]
  (assoc-in db [:items id :name] new-name))
(reg-event-persistent-db
  ::update-name
  [select-list]
  update-name)

(defn update-location [db [_ id new-location]]
  (if (> (.indexOf (:locations db) new-location) -1)
    (assoc-in db [:items id :location] new-location)
    db))
(reg-event-persistent-db
  ::update-location
  [select-list]
  update-location)

(defn check [db [_ id]]
  (update-in db [:items id :checked?] not))
(reg-event-persistent-db
  ::check
  [select-list]
  check)

(defn edit-name-start [{db :db} [_ id]]
  {:db (assoc-in db [:edits :items id :name] (get-in db [:items id :name]))
   :fx [[::fx/focus-element (str "edit-item-name-" id)]]})

(re-frame/reg-event-fx
  ::edit-name-start
  [select-list]
  edit-name-start)

(defn edit-name [db [_ id new-name]]
  (assoc-in db [:edits :items id :name] new-name))

(re-frame/reg-event-db
  ::edit-name
  edit-name)

(defn edit-name-submit [{db :db} [_ id]]
  (let [new-name (get-in db [:edits :items id :name])
        list (get-in db [:lists (:current-list-id db)])
        new-list (assoc-in list [:items id :name] new-name)]
    (if (s/valid? ::spec.list/list new-list)
      {:db (-> db
               (update-in [:edits :items] dissoc id)
               (update-in [:errors :forms] dissoc :item))
       :fx [[:dispatch [::update-name id new-name]]]}
      {:db (assoc-in db [:errors :forms :item] (s/explain-data ::spec.list/list new-list))
       :fx [[::fx/focus-element (str "edit-item-name-" id)]]})))

(re-frame/reg-event-fx
  ::edit-name-submit
  edit-name-submit)