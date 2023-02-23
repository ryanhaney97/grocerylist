(ns grocerylist.events.confirm
  (:require
    [re-frame.core :as re-frame]
    [grocerylist.events.util :refer [select-list]]
    [grocerylist.events.locations :as events.locations]
    [grocerylist.events.lists :as events.lists]))

(defn close-dialog [db]
  (dissoc db :confirm))

(re-frame/reg-event-db
  ::close
  close-dialog)

(defn delete-location [{db :db} [_ itemnum]]
  (let [location (nth (:locations db) itemnum)
        location-counts (frequencies (map :location (vals (:items db))))
        num-references (get location-counts location 0)]
    (if (= num-references 0)
      {:fx [[:dispatch [::events.locations/delete itemnum]]]}
      (let [message (str "There are currently " num-references " items located in " location " that will be deleted as well. Do you still want to delete " location " ?")]
        {:db (assoc db :confirm
                       {:message message
                        :on-confirm [::events.locations/delete itemnum]})}))))
(re-frame/reg-event-fx
  ::delete-location
  [select-list]
  delete-location)

(defn delete-list [db [_ id]]
  (let [current-list-name (get-in db [:lists id :name])]
    (assoc db :confirm
              {:message    (str "This will PERMANENTLY delete the list named " current-list-name ", are you sure you still want to delete it?")
               :on-confirm [::events.lists/delete id]})))

(re-frame/reg-event-db
  ::delete-list
  delete-list)