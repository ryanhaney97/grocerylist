(ns grocerylist.events.route
  (:require
    [re-frame.core :as re-frame]
    [grocerylist.fx :as fx]))

(re-frame/reg-event-fx
  ::set
  (fn [{db :db} [_ route]]
    (let [current-list-id (get-in route [:route-params :id] (:current-list-id db))
          new-db (assoc db
                   :route (:handler route)
                   :current-list-id current-list-id)]
      (if (= (:current-list-id new-db) (:current-list-id db))
        {:db new-db}
        {:db new-db
         :fx [[:dispatch [:grocerylist.events.forms/reset]]]}))))

(re-frame/reg-event-fx
  ::to
  (fn [{db :db} [_ handler id]]
    {::fx/navigate [handler :id (if id
                                  id
                                  (:current-list-id db))]}))