(ns grocerylist.views
  (:require
    [re-frame.core :as re-frame]
    [grocerylist.subs :as subs]
    [grocerylist.subs.errors :as errors]
    [grocerylist.views.list :refer [list-panel]]
    [grocerylist.views.lists :refer [lists-panel]]
    [grocerylist.views.add-list :refer [add-list-panel]]
    [grocerylist.views.add-item :refer [add-item-panel]]
    [grocerylist.views.locations :refer [location-panel]]
    [grocerylist.routes :as routes]))

(defn error-panel []
  (let [messages @(re-frame/subscribe [::errors/db])]
    (when messages
      [:div {:class "error"}
       [:div {:class "error"}
        "INTERNAL ERROR, PLEASE FORWARD THE FOLLOWING TO THE DEVELOPER: "]
       (map-indexed (fn [index message] [:div {:class "error"
                                               :key index}
                                         message]) messages)])))

(defmethod routes/panels :new-list [] [add-list-panel])
(defmethod routes/panels :lists [] [lists-panel])
(defmethod routes/panels :list [] [list-panel])
(defmethod routes/panels :add-item [] [add-item-panel])
(defmethod routes/panels :locations [] [location-panel])

(defn main-router []
  (let [route @(re-frame/subscribe [::subs/route])]
    [:div
     [error-panel]
     (routes/panels route)]))
