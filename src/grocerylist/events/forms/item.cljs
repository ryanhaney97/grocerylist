(ns grocerylist.events.forms.item
  (:require
    [re-frame.core :as re-frame]
    [grocerylist.events.util :refer [select-list]]
    [grocerylist.events.list :as list]))

(defn update-name [db [_ name]]
  (assoc-in db [:forms :item :name] name))
(re-frame/reg-event-db
  ::update-name
  update-name)

(defn update-location [db [_ location]]
  (assoc-in db [:forms :item :location] location))
(re-frame/reg-event-db
  ::update-location
  update-location)

(defn submit [{db :db}]
  (let [name (get-in db [:forms :item :name] "")
        location (get-in db [:forms :item :location] (first (:locations db)))]
    (if (and (not= name "") (> (.indexOf (:locations db) location) -1))
      {:fx [[:dispatch [::list/add-item name location]]
            [:dispatch [::update-name ""]]]}
      {})))
(re-frame/reg-event-fx
  ::submit
  [select-list]
  submit)

(defn reset [{db :db} _]
  {:fx [[:dispatch [::update-name ""]]
        [:dispatch [::update-location (first (:locations db))]]]})
(re-frame/reg-event-fx
  ::reset
  [select-list]
  reset)