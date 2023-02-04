(ns grocerylist.events.forms.item
  (:require
    [cljs.spec.alpha :as s]
    [re-frame.core :as re-frame]
    [grocerylist.events.util :refer [select-list]]
    [grocerylist.events.list :as list]
    [grocerylist.spec.item :as spec.item]))

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

(defn location-in-locations? [db item]
  (> (.indexOf (:locations db) (:location item)) -1))
(defn submit [{db :db}]
  (let [item-name (get-in db [:forms :item :name] "")
        location (get-in db [:forms :item :location] (first (:locations db)))
        item {:name item-name
              :location location
              :checked? false}
        validation-spec (s/and ::spec.item/item (partial location-in-locations? db))]
    (if (s/valid? validation-spec item)
      {:fx [[:dispatch [::list/add-item (:name item) (:location item)]]
            [:dispatch [::update-name ""]]]}
      (do
        (s/explain validation-spec item)
        {}))))
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