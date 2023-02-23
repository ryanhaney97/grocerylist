(ns grocerylist.events.forms.location
  (:require
    [re-frame.core :as re-frame]
    [cljs.spec.alpha :as s]
    [grocerylist.spec.list :as spec.list]
    [grocerylist.events.util :refer [select-list]]
    [grocerylist.events.locations :as locations]))

(defn validate [db]
  (let [new-location (get-in db [:forms :location :name] "")
        new-db (locations/add db [nil new-location])
        new-list (get-in new-db [:lists (:current-list-id new-db)])]
    (when (not (s/valid? ::spec.list/list new-list))
      (s/explain-data ::spec.list/list new-list))))

(defn revalidate [db]
  (assoc-in db [:errors :forms :location] (validate db)))

(re-frame/reg-event-db
  ::revalidate
  revalidate)

(defn update-name [{db :db} [_ name]]
  (let [result {:db (assoc-in db [:forms :location :name] name)}]
    (if (get-in db [:errors :forms :location])
      (assoc result
        :fx [[:dispatch [::revalidate]]])
      result)))
(re-frame/reg-event-fx
  ::update-name
  update-name)

(defn submit [{db :db}]
  (let [location (get-in db [:forms :location :name] "")
        list (get-in db [:lists (:current-list-id db)] {})
        new-list (update list :locations conj location)]
    (if (s/valid? ::spec.list/list new-list)
      {:fx [[:dispatch [::locations/add location]]
            [:dispatch [::update-name ""]]]
       :db (assoc-in db [:errors :forms :location] nil)}
      {:db (assoc-in db [:errors :forms :location] (s/explain-data ::spec.list/list new-list))})))
(re-frame/reg-event-fx
  ::submit
  [select-list]
  submit)