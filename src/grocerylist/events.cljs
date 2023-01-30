(ns grocerylist.events
  (:require
    [re-frame.core :as re-frame]
    [akiroz.re-frame.storage :as storage]
    [grocerylist.db :as db]
    [grocerylist.util :as u]))

(def persist-keys [:lists :lists.next-id])
(defn reg-event-persistent-db
  ([event-id interceptors handler]
   (re-frame/reg-event-fx
     event-id
     (into [(storage/persist-db-keys :grocerylist persist-keys)] interceptors)
     (fn [{:keys [db]} event-vec]
       {:db (handler db event-vec)})))
  ([event-id handler]
   (reg-event-persistent-db event-id [] handler)))

(def select-list
  (re-frame/->interceptor
    :id :select-list
    :before (fn [context]
              (let [db (get-in context [:coeffects :db])
                    current-list (get-in db [:lists (:current-list-id db)])]
                (update-in context [:coeffects :db] merge current-list)))
    :after (fn [context]
             (if-let [db (get-in context [:effects :db])]
               (let [current-list-id (get-in context [:coeffects :db :current-list-id])
                     list-keys (keys (get-in db [:lists current-list-id]))
                     updated-list (select-keys db list-keys)]
                 (-> context
                     (assoc-in [:effects :db :lists current-list-id] updated-list)
                     (update-in [:effects :db] (fn [db] (apply dissoc db list-keys)))))
               context))))

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
  [select-list]
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
  [select-list]
  (fn [db [_ itemnum]]
    (let [location (nth (:locations db) itemnum)]
      (-> db
          (update :locations u/removenth itemnum)
          (update :items remove-items-with-location location)))))

(reg-event-persistent-db
  ::initialize-db
  (fn [db _]
    (u/deep-merge db/default-db db)))

(reg-event-persistent-db
  ::add-item
  [select-list]
  (fn [db [_ itemname itemlocation]]
    (let [id (:items.next-id db)]
      (-> db
          (assoc-in [:items id] {:name itemname
                                 :location itemlocation
                                 :checked? false
                                 :id id})
          (update :items.next-id inc)))))

(reg-event-persistent-db
  ::delete-item
  [select-list]
  (fn [db [_ id]]
    (update db :items dissoc id)))

(reg-event-persistent-db
  ::check-item
  [select-list]
  (fn [db [_ id]]
    (update-in db [:items id :checked?] not)))

(reg-event-persistent-db
  ::update-item-name
  [select-list]
  (fn [db [_ id new-name]]
    (assoc-in db [:items id :name] new-name)))

(reg-event-persistent-db
  ::update-item-location
  [select-list]
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
  [select-list]
  (fn [db]
    (let [location (get-in db [:locationform :name])]
      (if (or (= location "") (> (.indexOf (:locations db) location) -1))
        (assoc-in db [:locationform :name] "")
        (-> db
            (update :locations conj location)
            (assoc-in [:locationform :name] ""))))))

(re-frame/reg-event-fx
  ::itemform.add-item
  [select-list]
  (fn [{db :db}]
    (let [name (get-in db [:itemform :name] "")
          location (get-in db [:itemform :location] (first (:locations db)))]
      (if (and (not= name "") (> (.indexOf (:locations db) location) -1))
        {:fx [[:dispatch [::add-item name location]]
              [:dispatch [::itemform.update-name ""]]]}
        {}))))

(re-frame/reg-event-fx
  ::itemform.reset
  [select-list]
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
      (update-in [:lists (:current-list-id db) :locations] u/swap (:location.dragged db) itemnum)
      (assoc :location.dragged itemnum))))

(re-frame/reg-event-db
  ::set-route
  (fn [db [_ route]]
    (assoc db
      :route (:handler route)
      :current-list-id (get-in route [:route-params :id] (:current-list-id db)))))

(re-frame/reg-event-fx
  ::route-to
  (fn [{db :db} [_ handler id]]
    {:navigate [handler :id (if id
                              id
                              (:current-list-id db))]}))

(reg-event-persistent-db
  ::new-list
  (fn [db [_ listname]]
    (let [new-id (:lists.next-id db)]
      (-> db
          (assoc-in [:lists new-id] {:listname listname
                                     :items {}
                                     :items.next-id 0
                                     :locations []})
          (update :lists.next-id inc)))))