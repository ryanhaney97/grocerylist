(ns grocerylist.events
  (:require
    [grocerylist.db :as db]
    [grocerylist.events.util :refer [reg-event-persistent-db select-list]]
    [grocerylist.events.forms]))

(reg-event-persistent-db
  ::initialize-db
  (fn [db _]
    (merge db/default-db db)))