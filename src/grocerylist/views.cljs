(ns grocerylist.views
  (:require
    [re-frame.core :as re-frame]
    [grocerylist.subs :as subs]
    [grocerylist.views.list :refer [list-panel]]
    [grocerylist.views.lists :refer [lists-panel]]
    [grocerylist.views.add-list :refer [add-list-panel]]
    [grocerylist.views.add-item :refer [add-item-panel]]
    [grocerylist.views.locations :refer [location-panel]]
    [grocerylist.routes :as routes]))

(defmethod routes/panels :new-list [] [add-list-panel])
(defmethod routes/panels :lists [] [lists-panel])
(defmethod routes/panels :list [] [list-panel])
(defmethod routes/panels :add-item [] [add-item-panel])
(defmethod routes/panels :locations [] [location-panel])

(defn main-router []
  (let [route @(re-frame/subscribe [::subs/route])]
    (routes/panels route)))
