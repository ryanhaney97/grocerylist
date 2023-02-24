(ns grocerylist.views.list
  (:require
    [grocerylist.views.util :as u]
    [re-frame.core :as re-frame]
    [reagent.core :as r]
    [grocerylist.events.list :as events.list]
    [grocerylist.subs.list :as subs.list]
    [grocerylist.subs.locations :as subs.locations]
    [grocerylist.subs.errors :as errors]
    [semantic-ui-reagent.core :as sui]))

(defn list-name-display []
  (r/with-let [on-click #(re-frame/dispatch-sync [::events.list/edit-name-start])
               name (re-frame/subscribe [::subs.list/name])]
    [:div {:style {:display "flex"
                   :align-items "center"
                   :justify-content "center"
                   :margin-top "10px"}}
     [sui/Header {:size "huge"
                  :on-click on-click}
      @name]]))

(defn list-name-edit []
  (r/with-let [list-name (re-frame/subscribe [::subs.list/name.edited])
               on-name-change (fn [event] (re-frame/dispatch-sync [::events.list/edit-name (.-value (.-target event))]))
               on-enter (fn [event]
                          (if (= (.-key event) "Enter")
                            (.blur (.-target event))))
               on-blur #(re-frame/dispatch [::events.list/edit-name-submit])]
    [sui/Input {:style {:margin-top "10px"}
                :fluid true}
     [:input {:id "edit-list-name"
              :value @list-name
              :on-change on-name-change
              :on-key-down on-enter
              :on-blur on-blur
              :style {:text-align "center"
                      :padding 0}
              :class "ui huge header"}]]))

(defn list-name-errors []
  [u/display-errors ::errors/list-form])

(defn list-header []
  (let [editing? @(re-frame/subscribe [::subs.list/name.editing?])]
    (if editing?
      [:div
       [list-name-errors]
       [list-name-edit]]
      [list-name-display])))

(defn nav-buttons []
  [sui/ButtonGroup {:widths 3
                    :basic true
                    :style {:width "40%"}}
   [u/nav-button :add-item "Add"]
   [u/nav-button :lists "Lists"]
   [u/nav-button :locations "Locations"]])

(defn item-delete-button [id]
  (r/with-let [on-click (fn [id] (re-frame/dispatch [::events.list/delete-item id]))]
    [sui/Button {:on-click (r/partial on-click id)
                 :icon "delete"
                 :negative true
                 :size "mini"}]))

(defn item-checkbox [id]
  (r/with-let [on-checked (fn [id] (re-frame/dispatch [::events.list/check-item id]))]
    (let [checked? @(re-frame/subscribe [::subs.list/item-checked? id])]
      [:input {:type "checkbox"
               :checked checked?
               :on-change (r/partial on-checked id)}])))
(defn item-name-display [id]
  (r/with-let [on-click (fn [id] (re-frame/dispatch [::events.list/edit-item-name-start id]))]
    (let [item-name @(re-frame/subscribe [::subs.list/item-name id])]
      [sui/TableCell {:on-click (r/partial on-click id)
                      :selectable true
                      :style {:overflow-wrap "break-word"
                              :cursor "pointer"
                              :padding "0.78571429em 0.78571429em"}}
       item-name])))

(defn item-name-edit [id]
  (r/with-let [on-name-change (fn [id event] (re-frame/dispatch-sync [::events.list/edit-item-name id (.-value (.-target event))]))
               on-enter (fn [event]
                          (if (= (.-key event) "Enter")
                            (.blur (.-target event))))
               on-blur (fn [id] (re-frame/dispatch [::events.list/edit-item-name-submit id]))]
    (let [item-name @(re-frame/subscribe [::subs.list/item-name.edited id])]
      [sui/Input {:fluid true}
       [:input {:id (str "edit-item-name-" id)
                :value item-name
                :on-change (r/partial on-name-change id)
                :on-key-down on-enter
                :on-blur (r/partial on-blur id)}]])))

(defn item-name-errors []
  [u/display-errors ::errors/item-form :name])

(defn item-name [id]
  (let [editing? @(re-frame/subscribe [::subs.list/item-name.editing? id])]
    (if editing?
      [sui/TableCell {:selectable true
                      :style {:padding "0.78571429em 0.78571429em"}}
       [item-name-errors]
       [item-name-edit id]]
      [item-name-display id])))

(defn item-location [id]
  (r/with-let [on-location-change (fn [id _ props]
                                    (re-frame/dispatch [::events.list/update-item-location id (.-value props)]))
               locations (re-frame/subscribe [::subs.locations/options])
               on-cell-clicked (fn [id]
                                 (some-> js/document (.getElementById (str "edit-item-location-" id)) .click))]
    (let [location @(re-frame/subscribe [::subs.list/item-location id])]
      [sui/TableCell {:style {:overflow "visible"
                              :cursor "pointer"}
                      :on-click (r/partial on-cell-clicked id)
                      :selectable true}
       [sui/Select {:value location
                    :id (str "edit-item-location-" id)
                    :on-change (r/partial on-location-change id)
                    :options @locations
                    :style {:border "none"
                            :appearance "none"
                            :background "none"}
                    :icon nil}]])))

(defn draw-item [id]
  [sui/TableRow
   [sui/TableCell {:text-align "center"}
    [item-delete-button id]]
   [item-name id]
   [item-location id]
   [sui/TableCell {:text-align "center"}
    [item-checkbox id]]])

(defn column-header [category category-element & [props]]
  (r/with-let [sort-method (re-frame/subscribe [::subs.list/sort-method])
               sort-reversed? (re-frame/subscribe [::subs.list/sort-reversed?])
               on-click (fn [category] (re-frame/dispatch [::events.list/toggle-sort-method category]))]
    [sui/TableHeaderCell (merge {:scope "col"
                                 :on-click (r/partial on-click category)
                                 :sorted (when (= category @sort-method)
                                           (if @sort-reversed?
                                             "descending"
                                             "ascending"))} props)
     category-element]))

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
  (let [id-list @(re-frame/subscribe [::subs.list/sorted-ids])]
    [:<> (map (fn [id] ^{:key id} [draw-item id]) id-list)]))

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
  (let [item-count @(re-frame/subscribe [::subs.list/item-count])]
    (if (= item-count 0)
      [sui/Message {:compact true}
       [:p "There are currently no items in this list! Please add some using the \"Add\" button above."]]
      [item-table])))
(defn list-panel []
  [sui/Container {:text-align "center"}
   [nav-buttons]
   [list-header]
   [item-table-or-message]])