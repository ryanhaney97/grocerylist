(ns grocerylist.events.forms.list
  (:require
    [re-frame.core :as re-frame]
    [grocerylist.events.lists :as lists]
    [grocerylist.events.route :as route]))

(defn submit [{db :db}]
  (let [list-name (get-in db [:forms :list :name] "")
        new-list-id (:lists.next-id db)]
    (if (= list-name "")
      {}
      {:fx [[:dispatch [::lists/new list-name]]
            [:dispatch [::update-name ""]]
            [:dispatch [::route/to :list new-list-id]]]})))
(re-frame/reg-event-fx
  ::submit
  submit)

(defn update-name [db [_ new-name]]
  (assoc-in db [:forms :list :name] new-name))
(re-frame/reg-event-db
  ::update-name
  update-name)