(ns grocerylist.events.forms.location
  (:require
    [re-frame.core :as re-frame]
    [grocerylist.events.util :refer [select-list]]
    [grocerylist.events.locations :as locations]))
(re-frame/reg-event-db
  ::update-name
  (fn [db [_ name]]
    (assoc-in db [:forms :location :name] name)))

(re-frame/reg-event-fx
  ::submit
  [select-list]
  (fn [{db :db}]
    (let [location (get-in db [:forms :location :name] "")]
      (if (or (= location "") (> (.indexOf (:locations db) location) -1))
        {}
        {:fx [[:dispatch [::locations/add location]]
              [:dispatch [::update-name ""]]]}))))