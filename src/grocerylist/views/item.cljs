(ns grocerylist.views.item
  (:require
    [grocerylist.views.util :refer [>evt <sub] :as u]
    [grocerylist.subs.item :as subs.item]
    [grocerylist.subs.locations :as subs.locations]
    [grocerylist.events.item :as events.item]
    [grocerylist.subs.errors :as errors]
    [re-frame.core :as re-frame]
    [reagent.core :as r]
    [semantic-ui-reagent.core :as sui]))

(defn item-delete-button [id]
  [sui/TableCell {:text-align "center"
                  :selectable true
                  :on-click (>evt [::events.item/delete id])}
   [sui/Icon {:name "delete"
              :color "red"
              :size "large"
              :fitted true}]])

(defn item-checkbox [id]
  [sui/TableCell {:text-align "center"
                  :selectable true
                  :on-click (>evt [::events.item/check id])}
   [:input {:type "checkbox"
            :on-change identity
            :checked (<sub [::subs.item/checked? id])}]])
(defn item-name-display [id]
  [sui/TableCell {:on-click (>evt [::events.item/edit-name-start id])
                  :selectable true
                  :style {:overflow-wrap "break-word"
                          :cursor "pointer"
                          :padding "0.78571429em 0.78571429em"}}
   (<sub [::subs.item/name id])])

(defn item-name-edit [id]
  (r/with-let [on-name-change (fn [id event] (re-frame/dispatch-sync [::events.item/edit-name id (.-value (.-target event))]))
               on-enter (fn [event]
                          (if (= (.-key event) "Enter")
                            (.blur (.-target event))))]
    [sui/Input {:fluid true}
     [:input {:id (str "edit-item-name-" id)
              :value (<sub [::subs.item/name.edited id])
              :on-change (r/partial on-name-change id)
              :on-key-down on-enter
              :on-blur (>evt [::events.item/edit-name-submit id])}]]))

(defn item-name-errors []
  [u/display-errors ::errors/item-form :name])

(defn item-name [id]
  (if (<sub [::subs.item/name.editing? id])
    [sui/TableCell {:selectable true
                    :style {:padding "0.78571429em 0.78571429em"}}
     [item-name-errors]
     [item-name-edit id]]
    [item-name-display id]))

(defn item-location [id]
  (r/with-let [on-location-change (fn [id _ props]
                                    (re-frame/dispatch [::events.item/update-location id (.-value props)]))
               on-cell-clicked (fn [id]
                                 (some-> js/document (.getElementById (str "edit-item-location-" id)) .click))]
    [sui/TableCell {:style {:overflow "visible"
                            :cursor "pointer"}
                    :on-click (r/partial on-cell-clicked id)
                    :selectable true}
     [sui/Select {:value (<sub [::subs.item/location id])
                  :id (str "edit-item-location-" id)
                  :on-change (r/partial on-location-change id)
                  :options (<sub [::subs.locations/options])
                  :style {:border "none"
                          :appearance "none"
                          :background "none"}
                  :icon nil}]]))

(defn draw-item [id]
  [sui/TableRow (if (<sub [::subs.item/checked? id])
                  {:style {:background "#D8D8D8"}}
                  {})
   [item-delete-button id]
   [item-name id]
   [item-location id]
   [item-checkbox id]
   ])