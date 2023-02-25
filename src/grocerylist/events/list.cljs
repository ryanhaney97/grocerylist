(ns grocerylist.events.list
  (:require
    [cljs.spec.alpha :as s]
    [re-frame.core :as re-frame]
    [grocerylist.util :as u]
    [grocerylist.spec.db :as spec.db]
    [grocerylist.events.util :refer [reg-event-persistent-db select-list]]
    [grocerylist.fx :as fx]))

(defn add-item [db [_ item-name item-location]]
  (let [list-id (:current-list-id db)
        id (get-in db [:lists list-id :items.next-id])]
    (-> db
        (assoc-in [:lists list-id :items id] {:name item-name
                                              :location item-location
                                              :checked? false
                                              :id id})
        (update-in [:lists list-id :items.next-id] inc))))
(reg-event-persistent-db
  ::add-item
  add-item)

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

(defn update-name [db [_ new-name]]
  (assoc db :name new-name))
(reg-event-persistent-db
  ::update-name
  [select-list]
  update-name)

(defn edit-name-start [{db :db}]
  {:db (assoc-in db [:edits :list :name] (:name db))
   :fx [[::fx/focus-element "edit-list-name"]]})

(re-frame/reg-event-fx
  ::edit-name-start
  [select-list]
  edit-name-start)

(defn edit-name [db [_ new-name]]
  (assoc-in db [:edits :list :name] new-name))

(re-frame/reg-event-db
  ::edit-name
  edit-name)

(defn edit-name-submit [{db :db}]
  (let [new-name (get-in db [:edits :list :name])
        new-db (assoc-in db [:lists (:current-list-id db) :name] new-name)]
    (if (s/valid? ::spec.db/db new-db)
      {:db (-> db
               (update-in [:edits :list] dissoc :name)
               (update-in [:errors :forms] dissoc :list))
       :fx [[:dispatch [::update-name new-name]]]}
      {:db (assoc-in db [:errors :forms :list] (s/explain-data ::spec.db/db new-db))
       :fx [[::fx/focus-element "edit-list-name"]]})))

(re-frame/reg-event-fx
  ::edit-name-submit
  edit-name-submit)