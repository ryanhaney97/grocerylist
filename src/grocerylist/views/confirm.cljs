(ns grocerylist.views.confirm
  (:require
    [reagent.core :as r]
    [grocerylist.views.util :refer [<sub]]
    [grocerylist.subs.confirm :as subs.confirm]
    [grocerylist.events.confirm :as events.confirm]
    [re-frame.core :as re-frame]
    [semantic-ui-reagent.core :as sui]))

(defn close-and-dispatch [event-vector]
  (re-frame/dispatch [::events.confirm/close])
  (when event-vector
    (re-frame/dispatch event-vector)))

(defn confirm-dialog-view []
  [sui/Confirm
   {:open true
    :content (<sub [::subs.confirm/message])
    :on-confirm (r/partial close-and-dispatch (<sub [::subs.confirm/on-confirm]))
    :on-cancel (r/partial close-and-dispatch (<sub [::subs.confirm/on-cancel]))}])

(defn confirm-dialog []
  (when (<sub [::subs.confirm/show?])
    [confirm-dialog-view]))