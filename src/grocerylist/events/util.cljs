(ns grocerylist.events.util
  (:require
    [akiroz.re-frame.storage :as storage]
    [re-frame.core :as re-frame]
    [cljs.spec.alpha :as s]
    [grocerylist.spec.db :as spec.db]))

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

(defn verify-db-sub [db _]
  (if (s/valid? ::spec.db/db db)
    (assoc-in db [:errors :db] nil)
    (assoc-in db [:errors :db] (s/explain-data ::spec.db/db db))))
(def verify-db (re-frame/enrich verify-db-sub))
(re-frame/reg-global-interceptor verify-db)