(ns grocerylist.views.list
  (:require
    [grocerylist.views.util :refer [<sub >evt >evt-now] :as u]
    [re-frame.core :as re-frame]
    [reagent.core :as r]
    [grocerylist.events.list :as events.list]
    [grocerylist.subs.list :as subs.list]
    [grocerylist.subs.errors :as errors]
    [grocerylist.views.item :refer [draw-item]]
    [semantic-ui-reagent.core :as sui]))

(defn list-name-display []
  [:div {:style {:display "flex"
                 :align-items "center"
                 :justify-content "center"
                 :margin-top "10px"}}
   [sui/Header {:size "huge"
                :on-click (>evt-now [::events.list/edit-name-start])}
    (<sub [::subs.list/name])]])

(defn list-name-edit []
  (r/with-let [on-name-change (fn [event] (re-frame/dispatch-sync [::events.list/edit-name (.-value (.-target event))]))
               on-enter (fn [event]
                          (if (= (.-key event) "Enter")
                            (.blur (.-target event))))]
    [sui/Input {:style {:margin-top "10px"}
                :fluid true}
     [:input {:id "edit-list-name"
              :value (<sub [::subs.list/name.edited])
              :on-change on-name-change
              :on-key-down on-enter
              :on-blur (>evt [::events.list/edit-name-submit])
              :style {:text-align "center"
                      :padding 0}
              :class "ui huge header"}]]))

(defn list-name-errors []
  [u/display-errors ::errors/list-form])

(defn list-header []
  (if (<sub [::subs.list/name.editing?])
    [:div
     [list-name-errors]
     [list-name-edit]]
    [list-name-display]))

(defn nav-buttons []
  [sui/ButtonGroup {:widths 3
                    :basic true
                    :style {:width "40%"}}
   [u/nav-button :add-item "Add"]
   [u/nav-button :lists "Lists"]
   [u/nav-button :locations "Locations"]])

(defn column-header [category category-element & [props]]
  [sui/TableHeaderCell (merge {:scope "col"
                               :on-click (>evt [::events.list/toggle-sort-method category])
                               :sorted (when (= category (<sub [::subs.list/sort-method]))
                                         (if (<sub [::subs.list/sort-reversed?])
                                           "descending"
                                           "ascending"))} props)
   category-element])

(defn table-header []
  [sui/TableHeader
   [sui/TableRow
    [sui/TableHeaderCell {:scope "col"
                          :width 1} ""]
    [column-header :name "Name" {:width 7}]
    [column-header :location "Location" {:width 7}]
    [column-header :checked? [sui/Icon {:name "check square outline"}]
     {:width 1
      :text-align "center"}]]])

(defn item-list []
  [:<> (map (fn [id] ^{:key id} [draw-item id]) (<sub [::subs.list/sorted-ids]))])

(defn item-table []
  [sui/Table {:class "item-table"
              :sortable true
              :striped true
              :celled true
              :fixed true}
   [table-header]
   [sui/TableBody
    [item-list]]])

(defn item-table-or-message []
  (if (= (<sub [::subs.list/count]) 0)
    [sui/Message {:compact true}
     [:p "There are currently no items in this list! Please add some using the \"Add\" button above."]]
    [item-table]))
(defn list-panel []
  [sui/Container {:text-align "center"}
   [nav-buttons]
   [list-header]
   [item-table-or-message]])