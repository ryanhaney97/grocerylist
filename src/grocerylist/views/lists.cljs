(ns grocerylist.views.lists
  (:require
    [re-frame.core :as re-frame]
    [grocerylist.events.route :as events.route]
    [grocerylist.events.lists :as events.lists]
    [grocerylist.subs.lists :as subs.lists]
    [grocerylist.views.util :as u]))

(defn list-button [list-name]
  (u/button-factory
    list-name
    (fn [id]
      (re-frame/dispatch [::events.route/to :list id]))))

(def list-delete-button
  (u/button-factory
    "X"
    (fn [id]
      (re-frame/dispatch-sync [::events.lists/confirm-delete id]))))

(defn draw-list-link [_ list]
  [:li
   [list-delete-button (:id list)]
   [(list-button (:name list)) (:id list)]])

(defn lists-list []
  (let [lists @(re-frame/subscribe [::subs.lists/name-list])]
    [:ul
     (map (fn [list] [draw-list-link {:key (:id list)} list]) lists)]))

(defn header-text []
  (let [num-lists (re-frame/subscribe [::subs.lists/count])]
    (fn []
      (if (= @num-lists 0)
        [:h2 "Please make a new list by clicking the button below."]
        [:h1 "Select a list"]))))

(defn lists-panel []
  [:div
   [header-text]
   [lists-list]
   [u/nav-button :new-list "New"]])