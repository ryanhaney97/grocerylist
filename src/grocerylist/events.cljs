(ns grocerylist.events
  (:require
    [re-frame.core :as re-frame]
    [akiroz.re-frame.storage :as storage]
    [grocerylist.db :as db]
    [grocerylist.util :as u]))

(def persist-keys [:items :locations :next-id])
(defn reg-event-persistent-db [event-id handler]
  (re-frame/reg-event-fx
    event-id
    [(storage/persist-db-keys :grocerylist persist-keys)]
    (fn [{:keys [db]} event-vec]
      {:db (handler db event-vec)})))

(re-frame/reg-fx
  :confirm-dialog
  (fn [{:keys [message on-confirm on-deny]
        :or {message ""}}]
    (if (js/confirm message)
      (when on-confirm
        (on-confirm))
      (when on-deny
        (on-deny)))))

(re-frame/reg-event-fx
  ::confirm-delete-location
  (fn [{db :db} [_ itemnum]]
    (let [location (nth (:locations db) itemnum)
          location-counts (frequencies (map :location (vals (:items db))))
          num-references (get location-counts location 0)]
      (if (= num-references 0)
        {:fx [[:dispatch [::delete-location itemnum]]]}
        {:fx [[:confirm-dialog
               {:message (str "There are currently " num-references " items located in " location " that will be deleted as well. Do you still want to delete " location " ?")
                :on-confirm #(re-frame/dispatch [::delete-location itemnum])}]]}))))

(defn remove-items-with-location [items location]
  (into {}
        (remove (comp #{location} :location val) items)))
(reg-event-persistent-db
  ::delete-location
  (fn [db [_ itemnum]]
    (let [location (nth (:locations db) itemnum)]
      (js/console.log itemnum)
      (-> db
          (update :locations u/removenth itemnum)
          (update :items remove-items-with-location location)))))

(reg-event-persistent-db
  ::initialize-db
  (fn [db _]
    (u/deep-merge db/default-db db)))

(reg-event-persistent-db
  ::add-item
  (fn [db [_ itemname itemlocation]]
    (let [id (:next-id db)]
      (-> db
          (assoc-in [:items id] {:name itemname
                                 :location itemlocation
                                 :checked? false
                                 :id id})
          (update :next-id inc)))))

(reg-event-persistent-db
  ::delete-item
  (fn [db [_ id]]
    (update db :items dissoc id)))

(reg-event-persistent-db
  ::check-item
  (fn [db [_ id]]
    (update-in db [:items id :checked?] not)))

(reg-event-persistent-db
  ::update-item-name
  (fn [db [_ id new-name]]
    (assoc-in db [:items id :name] new-name)))

(reg-event-persistent-db
  ::update-item-location
  (fn [db [_ id new-location]]
    (if (> (.indexOf (:locations db) new-location) -1)
      (assoc-in db [:items id :location] new-location)
      db)))

(re-frame/reg-event-db
  ::toggle-sort-method
  (fn [db [_ sort-method]]
    (if (contains? u/sorting-method-map sort-method)
      (if (= sort-method (:sort-method db))
        (update db :sort-reversed? not)
        (assoc db :sort-method sort-method
                  :sort-reversed? false))
      db)))

(re-frame/reg-event-db
  ::itemform.update-name
  (fn [db [_ name]]
    (assoc-in db [:itemform :name] name)))

(re-frame/reg-event-db
  ::itemform.update-location
  (fn [db [_ location]]
    (assoc-in db [:itemform :location] location)))

(re-frame/reg-event-db
  ::locationform.update-name
  (fn [db [_ name]]
    (assoc-in db [:locationform :name] name)))

(reg-event-persistent-db
  ::locationform.submit
  (fn [db]
    (let [location (get-in db [:locationform :name])]
      (if (or (= location "") (> (.indexOf (:locations db) location) -1))
        (assoc-in db [:locationform :name] "")
        (-> db
            (update :locations conj location)
            (assoc-in [:locationform :name] ""))))))

(re-frame/reg-event-fx
  ::itemform.add-item
  (fn [{db :db}]
    (let [name (get-in db [:itemform :name] "")
          location (get-in db [:itemform :location] (first (:locations db)))]
      (if (and (not= name "") (> (.indexOf (:locations db) location) -1))
        {:fx [[:dispatch [::add-item name location]]
              [:dispatch [::itemform.update-name ""]]]}
        {}))))

(re-frame/reg-event-fx
  ::itemform.reset
  (fn [{db :db} _]
    {:fx [[:dispatch [::itemform.update-name ""]]
          [:dispatch [::itemform.update-location (first (:locations db))]]]}))

(re-frame/reg-event-db
  ::locations.drag-start
  (fn [db [_ itemnum]]
    (assoc db :location.dragged itemnum)))

(re-frame/reg-event-db
  ::locations.drag-end
  (fn [db _]
    (assoc db :location.dragged nil)))

(reg-event-persistent-db
  ::locations.drag-over
  (fn [db [_ itemnum]]
    (->
      db
      (update :locations u/swap (:location.dragged db) itemnum)
      (assoc :location.dragged itemnum))))

(re-frame/reg-event-db
  ::set-route
  (fn [db [_ path]]
    (assoc db :route path)))

(re-frame/reg-event-fx
  ::route-to
  (fn [_ [_ handler]]
    {:navigate handler}))