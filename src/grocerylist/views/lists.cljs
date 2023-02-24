(ns grocerylist.views.lists
  (:require
    [grocerylist.events.route :as events.route]
    [grocerylist.events.confirm :as events.confirm]
    [grocerylist.subs.lists :as subs.lists]
    [grocerylist.views.util :refer [>evt <sub]]
    [semantic-ui-reagent.core :as sui]))

(defn draw-list-name [id]
  [sui/Header {:as "a"
               :size "large"
               :on-click (>evt [::events.route/to :list id])}
   (<sub [::subs.lists/name id])])

(defn draw-list-delete-button [id]
  [sui/Button {:on-click (>evt [::events.confirm/delete-list id])
               :icon "delete"
               :negative true}])

(defn draw-list-item [id]
  [:div {:style {:margin "1.5em"}}
   [draw-list-delete-button id]
   [draw-list-name id]])

(defn lists-list []
  [:div
   (map (fn [id] ^{:key id} [draw-list-item id]) (<sub [::subs.lists/id-list]))])

(defn header-text []
  (if (= (<sub [::subs.lists/count]) 0)
    [sui/Header {:as "h2"} "Please make a new list by clicking the button below."]
    [sui/Header {:as "h1"} "Select a list"]))

(defn new-list-button []
  [sui/Button {:primary true
               :on-click (>evt [::events.route/to :new-list])}
   "New"])

(defn lists-panel []
  [sui/Container {:style {:display "flex"
                          :align-items "center"
                          :justify-content "center"
                          :flex-direction "column"}}
   [header-text]
   [lists-list]
   [new-list-button]])