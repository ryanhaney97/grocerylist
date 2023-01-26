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
  ::route-to
  (fn [db [_ path]]
    (assoc db :route path)))