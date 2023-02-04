(ns grocerylist.events
  (:require
    [cljs.spec.alpha :as s]
    [grocerylist.spec.db :as spec.db]
    [grocerylist.db :as db]
    [grocerylist.events.util :refer [reg-event-persistent-db select-list]]
    [grocerylist.events.forms]))

(defn initialize-db [db]
  (merge db/default-db db))
(s/fdef initialize-db
        :args (s/cat :db map?)
        :ret (s/and map? ::spec.db/db))
(reg-event-persistent-db
  ::initialize-db
  initialize-db)