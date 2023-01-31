(ns grocerylist.views.lists
  (:require
    [re-frame.core :as re-frame]
    [grocerylist.events.route :as events.route]
    [grocerylist.subs.lists :as subs.lists]
    [grocerylist.views.util :as u]))

(defn draw-list-link [_ list]
  (let [on-click (fn [id] (re-frame/dispatch [::events.route/to :list id]))
        on-click-factory (u/callback-factory-factory on-click)]
    (fn [_ list]
      [:li
       [:button {:type "button"
                 :on-click (on-click-factory (:id list))}
        (:name list)]])))

(defn lists-list []
  (let [lists @(re-frame/subscribe [::subs.lists/name-list])]
    [:ul
     (map (fn [list] [draw-list-link {:key (:id list)} list]) lists)]))

(defn lists-panel []
  [:div
   [:h1 "Select a list"]
   [lists-list]
   [u/nav-button :new-list "New"]])