(ns grocerylist.subs.confirm
  (:require
    [re-frame.core :as re-frame]))

(re-frame/reg-sub
  ::confirm
  :-> :confirm)

(re-frame/reg-sub
  ::show?
  :<- [::confirm]
  :-> some?)

(re-frame/reg-sub
  ::on-confirm
  :<- [::confirm]
  (fn [confirm-entity]
    (:on-confirm confirm-entity)))

(re-frame/reg-sub
  ::on-cancel
  :<- [::confirm]
  (fn [confirm-entity]
    (:on-cancel confirm-entity)))

(re-frame/reg-sub
  ::message
  :<- [::confirm]
  (fn [confirm-entity]
    (:message confirm-entity
      "No message was set! This shouldn't happen, so please submit a bug report if you see this message.")))