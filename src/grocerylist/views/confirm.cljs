(ns grocerylist.views.confirm
  (:require
    [grocerylist.subs.confirm :as subs.confirm]
    [grocerylist.events.confirm :as events.confirm]
    [re-frame.core :as re-frame]
    [semantic-ui-reagent.core :as sui]))

(defn close-and-dispatch [event-vector]
  (re-frame/dispatch [::events.confirm/close])
  (when event-vector
    (re-frame/dispatch event-vector)))

(defn confirm-dialog-view [message confirm-event cancel-event]
  [sui/Confirm
   {:open true
    :content message
    :on-confirm (partial close-and-dispatch confirm-event)
    :on-cancel (partial close-and-dispatch cancel-event)}])

(defn confirm-dialog []
  (let [show? @(re-frame/subscribe [::subs.confirm/show?])]
    (when show?
      (let [message @(re-frame/subscribe [::subs.confirm/message])
            confirm-event @(re-frame/subscribe [::subs.confirm/on-confirm])
            cancel-event @(re-frame/subscribe [::subs.confirm/on-cancel])]
        [confirm-dialog-view message confirm-event cancel-event]))))