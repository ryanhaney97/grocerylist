(ns grocerylist.subs
  (:require
    [re-frame.core :as re-frame]))

(re-frame/reg-sub
  ::route
  :-> :route)

(re-frame/reg-sub
  ::forms
  :-> :forms)