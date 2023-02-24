(ns grocerylist.views.lists
  (:require
    [re-frame.core :as re-frame]
    [reagent.core :as r]
    [grocerylist.events.route :as events.route]
    [grocerylist.events.confirm :as events.confirm]
    [grocerylist.subs.lists :as subs.lists]
    [semantic-ui-reagent.core :as sui]))

(defn draw-list-name [id]
  (r/with-let [on-click (fn [id]
                          (re-frame/dispatch [::events.route/to :list id]))]
    (let [name @(re-frame/subscribe [::subs.lists/name id])]
      [sui/Header {:as "a"
                   :size "large"
                   :on-click (r/partial on-click id)}
       name])))

(defn draw-list-delete-button [id]
  (r/with-let [on-click (fn [id]
                          (re-frame/dispatch [::events.confirm/delete-list id]))]
    [sui/Button {:on-click (r/partial on-click id)
                 :icon "delete"
                 :negative true}]))

(defn draw-list-item [id]
  [:div {:style {:margin "1.5em"}}
   [draw-list-delete-button id]
   [draw-list-name id]])

(defn lists-list []
  (let [ids @(re-frame/subscribe [::subs.lists/id-list])]
    [:div
     (map (fn [id] ^{:key id} [draw-list-item id]) ids)]))

(defn header-text []
  (let [num-lists @(re-frame/subscribe [::subs.lists/count])]
    (if (= num-lists 0)
      [sui/Header {:as "h2"} "Please make a new list by clicking the button below."]
      [sui/Header {:as "h1"} "Select a list"])))

(defn new-list-button []
  (r/with-let [on-click #(re-frame/dispatch [::events.route/to :new-list])]
    [sui/Button {:primary true
                 :on-click on-click}
     "New"]))

(defn lists-panel []
  [sui/Container {:style {:display "flex"
                          :align-items "center"
                          :justify-content "center"
                          :flex-direction "column"}}
   [header-text]
   [lists-list]
   [new-list-button]])