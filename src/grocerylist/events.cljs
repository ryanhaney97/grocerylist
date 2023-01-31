(ns grocerylist.events
  (:require
    [grocerylist.db :as db]
    [grocerylist.util :as u]
    [grocerylist.events.util :refer [reg-event-persistent-db select-list]]
    [grocerylist.events.forms]))

(reg-event-persistent-db
  ::initialize-db
  (fn [db _]
    (u/deep-merge db/default-db db)))