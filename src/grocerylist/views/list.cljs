(ns grocerylist.views.list
  (:require
    [grocerylist.views.util :as u]
    [re-frame.core :as re-frame]
    [grocerylist.events.list :as events.list]
    [grocerylist.subs.list :as subs.list]
    [grocerylist.subs.locations :as subs.locations]
    [grocerylist.subs.errors :as errors]))

(defn list-name-display []
  (let [on-click #(re-frame/dispatch-sync [::events.list/edit-name-start])
        name (re-frame/subscribe [::subs.list/name])]
    (fn []
      [:div {:class "h1"
             :on-click on-click}
       @name])))

(defn list-name-edit []
  (let [list-name (re-frame/subscribe [::subs.list/name.edited])
        on-name-change (fn [event] (re-frame/dispatch-sync [::events.list/edit-name (.-value (.-target event))]))
        on-enter (fn [event]
                   (if (= (.-key event) "Enter")
                     (.blur (.-target event))))
        on-blur #(re-frame/dispatch [::events.list/edit-name-submit])]
    (fn []
      [:input {:id "edit-list-name"
               :type "text"
               :value @list-name
               :on-change on-name-change
               :on-key-down on-enter
               :on-blur on-blur
               :class "h1 edit-name"
               :size (count @list-name)}])))

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
  [:div
   [u/nav-button :add-item "Add"]
   [u/nav-button :lists "Lists"]
   [u/nav-button :locations "Locations"]])

(def item-delete-button
  (u/button-factory
    "X"
    (fn [id]
      (re-frame/dispatch [::events.list/delete-item id]))))

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
        [:div {:on-click (on-click-factory id)}
         item-name]))))
(defn item-name-edit [id]
  (let [on-name-change (fn [id event] (re-frame/dispatch-sync [::events.list/edit-item-name id (.-value (.-target event))]))
        on-name-change-factory (u/callback-factory-factory on-name-change)
        on-enter (fn [event]
                   (if (= (.-key event) "Enter")
                     (.blur (.-target event))))
        on-blur (fn [id] (re-frame/dispatch [::events.list/edit-item-name-submit id]))
        on-blur-factory (u/callback-factory-factory on-blur)
        max-length (re-frame/subscribe [::subs.list/max-item-length])]
    (fn [id]
      (let [item-name @(re-frame/subscribe [::subs.list/item-name.edited id])]
        [:input {:id (str "edit-item-name-" id)
                 :type "text"
                 :value item-name
                 :on-change (on-name-change-factory id)
                 :on-key-down on-enter
                 :on-blur (on-blur-factory id)
                 :class "edit-name"
                 :size 1}]))))

(defn item-name-errors []
  [u/display-errors ::errors/item-form :name])

(defn item-name-sizing [id]
  (let [item-name @(re-frame/subscribe [::subs.list/item-name.edited id])]
    [:div {:class "input-sizer"}
     item-name]))
(defn item-name [id]
  (let [editing? @(re-frame/subscribe [::subs.list/item-name.editing? id])]
    (if editing?
      [:div {:class "input-container"}
       [item-name-sizing id]
       [item-name-errors]
       [item-name-edit id]]
      [item-name-display id])))

(defn location-select-option [location]
  [:option {:value location}
   location])

(defn location-select-options []
  (let [locations (re-frame/subscribe [::subs.locations/list])]
    (fn []
      [:<> (map (fn [location] ^{:key location} [location-select-option location]) @locations)])))

(defn item-location [id]
  (let [on-location-change (fn [id event]
                             (re-frame/dispatch [::events.list/update-item-location id (.-value (.-target event))]))
        location-change-factory (u/callback-factory-factory on-location-change)]
    (fn [id]
      (let [location @(re-frame/subscribe [::subs.list/item-location id])]
        [:select {:value location
                  :on-change (location-change-factory id)}
         [location-select-options]]))))
(defn draw-item [id]
  [:tr
   [:td
    [item-delete-button id]]
   [:td
    [item-name id]]
   [:td
    [item-location id]]
   [:td
    [item-checkbox id]]])

(defn column-header [category category-name]
  (let [sort-method (re-frame/subscribe [::subs.list/sort-method])
        sort-reversed? (re-frame/subscribe [::subs.list/sort-reversed?])
        on-click #(re-frame/dispatch [::events.list/toggle-sort-method category])]
    (fn []
      [:th {:scope "col"
            :on-click on-click}
       (if (= category @sort-method)
         (str category-name (if @sort-reversed?
                              " \u25B2"
                              " \u25BC"))
         category-name)])))

(defn table-header []
  [:thead
   [:tr
    [:th {:scope "col"} ""]
    [column-header :name "Name"]
    [column-header :location "Location"]
    [column-header :checked? "Checked"]]])

(defn item-list []
  (let [id-list (re-frame/subscribe [::subs.list/sorted-ids])]
    (fn []
      [:<> (map (fn [id] ^{:key id} [draw-item id]) @id-list)])))

(defn item-table []
  [:table {:class "item-table"}
   [table-header]
   [:tbody
    [item-list]]])

(defn item-table-or-message []
  (let [item-count (re-frame/subscribe [::subs.list/item-count])]
    (fn []
      (if (= @item-count 0)
        [:p "There are currently no items in this list! Please add some using the \"Add\" button above."]
        [item-table]))))
(defn list-panel []
  [:div
   [list-header]
   [nav-buttons]
   [item-table-or-message]])