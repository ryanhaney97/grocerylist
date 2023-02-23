(ns grocerylist.views
  (:require
    [re-frame.core :as re-frame]
    [semantic-ui-reagent.core :as sui]
    [grocerylist.subs :as subs]
    [grocerylist.subs.errors :as errors]
    [grocerylist.views.list :refer [list-panel]]
    [grocerylist.views.lists :refer [lists-panel]]
    [grocerylist.views.add-list :refer [add-list-panel]]
    [grocerylist.views.add-item :refer [add-item-panel]]
    [grocerylist.views.locations :refer [location-panel]]
    [grocerylist.routes :as routes]))

(defn error-message []
  (let [messages @(re-frame/subscribe [::errors/db])]
    (when messages
      [sui/Message {:error true
                    :style {:white-space "pre-wrap"}}
       (apply str "INTERNAL ERROR, PLEASE FORWARD THE FOLLOWING TO THE DEVELOPER:\n"
              (interpose "\n" messages))])))

(defn loading-panel []
  [sui/Container {:style {:display "flex"
                          :justify-content "center"
                          :align-items "center"}}
   [sui/Loader {:active true}]])

(defmethod routes/panels :new-list [] [add-list-panel])
(defmethod routes/panels :lists [] [lists-panel])
(defmethod routes/panels :list [] [list-panel])
(defmethod routes/panels :add-item [] [add-item-panel])
(defmethod routes/panels :locations [] [location-panel])
(defmethod routes/panels :loading [] [loading-panel])

(defn main-router []
  (let [route @(re-frame/subscribe [::subs/route])]
    [:div
     [error-message]
     (routes/panels route)]))
