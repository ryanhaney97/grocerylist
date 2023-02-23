(ns grocerylist.views.list
  (:require
    [grocerylist.views.util :as u]
    [re-frame.core :as re-frame]
    [grocerylist.events.list :as events.list]
    [grocerylist.subs.list :as subs.list]
    [grocerylist.subs.locations :as subs.locations]
    [grocerylist.subs.errors :as errors]
    [semantic-ui-reagent.core :as sui]))

(defn list-name-display []
  (let [on-click #(re-frame/dispatch-sync [::events.list/edit-name-start])
        name (re-frame/subscribe [::subs.list/name])]
    (fn []
      [:div {:style {:display "flex"
                     :align-items "center"
                     :justify-content "center"
                     :margin-top "10px"}}
       [sui/Header {:size "huge"
                    :on-click on-click}
        @name]])))

(defn list-name-edit []
  (let [list-name (re-frame/subscribe [::subs.list/name.edited])
        on-name-change (fn [event] (re-frame/dispatch-sync [::events.list/edit-name (.-value (.-target event))]))
        on-enter (fn [event]
                   (if (= (.-key event) "Enter")
                     (.blur (.-target event))))
        on-blur #(re-frame/dispatch [::events.list/edit-name-submit])]
    (fn []
      [sui/Input {:style {:margin-top "10px"}
                  :fluid true}
       [:input {:id "edit-list-name"
                :value @list-name
                :on-change on-name-change
                :on-key-down on-enter
                :on-blur on-blur
                :style {:text-align "center"
                        :padding 0}
                :class "ui huge header"}]])))

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

;(def item-delete-button
;  (u/button-factory
;    "X"
;    (fn [id]
;      (re-frame/dispatch [::events.list/delete-item id]))))

(defn item-delete-button [id]
  (let [on-click (fn [id] (re-frame/dispatch [::events.list/delete-item id]))
        on-click-factory (u/callback-factory-factory on-click)]
    (fn [id]
      [sui/Button {:on-click (on-click-factory id)
                   :icon "delete"
                   :negative true
                   :size "mini"}])))

(defn item-checkbox [id]
  (let [on-checked (fn [id] (re-frame/dispatch [::events.list/check-item id]))
        on-checked-factory (u/callback-factory-factory on-checked)]
    (fn [id]
      (let [checked? @(re-frame/subscribe [::subs.list/item-checked? id])]
        [:input {:type "checkbox"
                 :checked checked?
                 :on-change (on-checked-factory id)}]))))
(defn item-name-display [id]
  (let [on-click (fn [id] (re-frame/dispatch [::events.list/edit-item-name-start id]))
        on-click-factory (u/callback-factory-factory on-click)]
    (fn [id]
      (let [item-name @(re-frame/subscribe [::subs.list/item-name id])]
        [sui/TableCell {:on-click (on-click-factory id)
                        :selectable true
                        :style {:overflow-wrap "break-word"
                                :cursor "pointer"
                                :padding "0.78571429em 0.78571429em"}}
         item-name]))))

(defn item-name-edit [id]
  (let [on-name-change (fn [id event] (re-frame/dispatch-sync [::events.list/edit-item-name id (.-value (.-target event))]))
        on-name-change-factory (u/callback-factory-factory on-name-change)
        on-enter (fn [event]
                   (if (= (.-key event) "Enter")
                     (.blur (.-target event))))
        on-blur (fn [id] (re-frame/dispatch [::events.list/edit-item-name-submit id]))
        on-blur-factory (u/callback-factory-factory on-blur)
        ;max-length (re-frame/subscribe [::subs.list/max-item-length])
        ]
    (fn [id]
      (let [item-name @(re-frame/subscribe [::subs.list/item-name.edited id])]
        [sui/Input {:fluid true}
         [:input {:id (str "edit-item-name-" id)
                  :value item-name
                  :on-change (on-name-change-factory id)
                  :on-key-down on-enter
                  :on-blur (on-blur-factory id)}]]))))

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
  (let [on-location-change (fn [id _ props]
                             (re-frame/dispatch [::events.list/update-item-location id (.-value props)]))
        location-change-factory (u/callback-factory-factory on-location-change)
        locations (re-frame/subscribe [::subs.locations/options])
        on-cell-clicked (fn [id]
                          (some-> js/document (.getElementById (str "edit-item-location-" id)) .click))
        on-cell-clicked-factory (u/callback-factory-factory on-cell-clicked)]
    (fn [id]
      (let [location @(re-frame/subscribe [::subs.list/item-location id])]
        [sui/TableCell {:style {:overflow "visible"
                                :cursor "pointer"}
                        :on-click (on-cell-clicked-factory id)
                        :selectable true}
         [sui/Select {:value location
                      :id (str "edit-item-location-" id)
                      :on-change (location-change-factory id)
                      :options @locations
                      :style {:border "none"
                              :appearance "none"
                              :background "none"}
                      :icon nil}]]))))

(defn draw-item [id]
  [sui/TableRow
   [sui/TableCell {:text-align "center"}
    [item-delete-button id]]
   [item-name id]
   [item-location id]
   [sui/TableCell {:text-align "center"}
    [item-checkbox id]]])

(defn column-header [category category-element & [props]]
  (let [sort-method (re-frame/subscribe [::subs.list/sort-method])
        sort-reversed? (re-frame/subscribe [::subs.list/sort-reversed?])
        on-click (fn [category] (re-frame/dispatch [::events.list/toggle-sort-method category]))
        on-click-factory (u/callback-factory-factory on-click)]
    (fn [category category-element & [props]]
      [sui/TableHeaderCell (merge {:scope "col"
                                   :on-click (on-click-factory category)
                                   :sorted (when (= category @sort-method)
                                             (if @sort-reversed?
                                               "descending"
                                               "ascending"))} props)
       category-element])))

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
  (let [id-list (re-frame/subscribe [::subs.list/sorted-ids])]
    (fn []
      [:<> (map (fn [id] ^{:key id} [draw-item id]) @id-list)])))

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
  (let [item-count (re-frame/subscribe [::subs.list/item-count])]
    (fn []
      (if (= @item-count 0)
        [sui/Message {:compact true}
         [:p "There are currently no items in this list! Please add some using the \"Add\" button above."]]
        [item-table]))))
(defn list-panel []
  [sui/Container {:text-align "center"}
   [nav-buttons]
   [list-header]
   [item-table-or-message]])