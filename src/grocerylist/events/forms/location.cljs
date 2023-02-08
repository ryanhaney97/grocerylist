(ns grocerylist.events.forms.location
  (:require
    [re-frame.core :as re-frame]
    [cljs.spec.alpha :as s]
    [grocerylist.spec.list :as spec.list]
    [grocerylist.events.util :refer [select-list]]
    [grocerylist.events.locations :as locations]))

(defn update-name [db [_ name]]
  (assoc-in db [:forms :location :name] name))
(re-frame/reg-event-db
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