(ns grocerylist.events.forms.list
  (:require
    [re-frame.core :as re-frame]
    [cljs.spec.alpha :as s]
    [grocerylist.events.lists :as lists]
    [grocerylist.events.route :as route]
    [grocerylist.spec.db :as spec.db]))

(defn submit [{db :db}]
  (let [list-name (get-in db [:forms :list :name] "")
        new-list-id (:lists.next-id db)
        new-db (lists/new db [nil list-name])]
    (if (s/valid? ::spec.db/db new-db)
      {:fx [[:dispatch [::lists/new list-name]]
            [:dispatch [::update-name ""]]
            [:dispatch [::route/to :list new-list-id]]]
       :db (assoc-in db [:errors :forms :list] nil)}
      {:db (assoc-in db [:errors :forms :list] (s/explain-data ::spec.db/db new-db))})))
(re-frame/reg-event-fx
  ::submit
  submit)

(defn update-name [db [_ new-name]]
  (assoc-in db [:forms :list :name] new-name))
(re-frame/reg-event-db
  ::update-name
  update-name)