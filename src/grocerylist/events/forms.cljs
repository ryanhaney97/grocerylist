(ns grocerylist.events.forms
  (:require
    [re-frame.core :as re-frame]
    [grocerylist.events.forms.item :as forms.item]
    [grocerylist.events.forms.location :as forms.location]
    [grocerylist.events.forms.list :as forms.list]))

(re-frame/reg-event-fx
  ::reset
  (fn []
    {:fx [[:dispatch [::forms.item/reset]]
          [:dispatch [::forms.location/update-name ""]]
          [:dispatch [::forms.list/update-name ""]]]}))