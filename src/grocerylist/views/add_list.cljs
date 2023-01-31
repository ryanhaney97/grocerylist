(ns grocerylist.views.add-list
  (:require
    [re-frame.core :as re-frame]
    [grocerylist.views.util :as u]
    [grocerylist.events.forms.list :as events.forms.list]
    [grocerylist.subs.forms.list :as subs.forms.list]))

(defn list-name-input []
  (u/form-text-input-factory
    "name"
    ::subs.forms.list/name
    ::events.forms.list/update-name
    ::events.forms.list/submit))

(def add-button
  (u/button-factory
    "Add"
    (fn []
      (re-frame/dispatch [::events.forms.list/submit]))))
(defn add-list-panel []
  [:div
   [:h1 "Create a New List"]
   [:label {:for "name"} "List Name: "]
   [list-name-input]
   [add-button]])