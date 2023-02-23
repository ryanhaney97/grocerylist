(ns grocerylist.events.route
  (:require
    [re-frame.core :as re-frame]
    [grocerylist.fx :as fx]))

(defn set-route [{db :db} [_ route]]
  (let [current-list-id (get-in route [:route-params :id] (:current-list-id db))]
    (if (or (#{:lists :new-list :loading} (:handler route)) (contains? (:lists db) current-list-id))
      (let [new-db (assoc db
                     :route (:handler route)
                     :current-list-id current-list-id
                     :errors {}
                     :edits {})]
        (if (= current-list-id (:current-list-id db))
          {:db new-db}
          {:db new-db
           :fx [[:dispatch [:grocerylist.events.forms/reset]]]}))
      {:dispatch [::to :lists]})))
(re-frame/reg-event-fx
  ::set
  set-route)

(defn to [{db :db} [_ handler id]]
  {::fx/navigate [handler :id (if id
                                id
                                (:current-list-id db))]})
(re-frame/reg-event-fx
  ::to
  to)