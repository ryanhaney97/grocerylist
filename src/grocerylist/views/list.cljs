(ns grocerylist.views.list
  (:require
    [grocerylist.views.util :as u]
    [re-frame.core :as re-frame]
    [grocerylist.events.list :as events.list]
    [grocerylist.subs.list :as subs.list]
    [grocerylist.subs.locations :as subs.locations]
    [grocerylist.subs.errors :as errors]))

(defn list-name-display []
  (let [on-click #(re-frame/dispatch [::events.list/edit-name-start])
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

(defn item-checkbox [checked? id]
  (let [on-checked (fn [id] (re-frame/dispatch [::events.list/check-item id]))
        on-checked-factory (u/callback-factory-factory on-checked)]
    (fn [checked? id]
      [:input {:type "checkbox"
               :checked checked?
               :on-change (on-checked-factory id)}])))

(defn item-name [name id]
  (let [on-name-change (fn [id event] (re-frame/dispatch-sync [::events.list/update-item-name id (.-value (.-target event))]))
        name-change-factory (u/callback-factory-factory on-name-change)
        on-enter (fn [event]
                   (if (= (.-key event) "Enter")
                     (.blur (.-target event))))
        max-length (re-frame/subscribe [::subs.list/max-item-length])]
    (fn [name id]
      [:input {:type "text"
               :value name
               :on-change (name-change-factory id)
               :on-key-down on-enter
               :class "edit-name"
               :size @max-length}])))

(defn item-location [_ location id]
  (let [locations (re-frame/subscribe [::subs.locations/list])
        on-location-change (fn [id event]
                             (re-frame/dispatch [::events.list/update-item-location id (.-value (.-target event))]))
        location-change-factory (u/callback-factory-factory on-location-change)]
    (fn [_ location id]
      [:select {:value location
                :on-change (location-change-factory id)}
       (map
         (fn [location]
           [:option {:value location
                     :key location}
            location]) @locations)])))
(defn draw-item [_ id]
  (let [item @(re-frame/subscribe [::subs.list/item-by-id id])]
    [:tr
     [:td
      [item-delete-button id]]
     [:td
      [item-name (:name item "") id]]
     [:td
      [item-location {:key (:location item)} (:location item) id]]
     [:td
      [item-checkbox (:checked? item) id]]]))

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
  [:table {:class "item-table"}
   [table-header]
   [:tbody
    (let [id-list @(re-frame/subscribe [::subs.list/sorted-ids])]
      (map (fn [id] [draw-item {:key id} id]) id-list))]])

(defn item-list-or-message []
  (let [item-count (re-frame/subscribe [::subs.list/item-count])]
    (fn []
      (if (= @item-count 0)
        [:p "There are currently no items in this list! Please add some using the \"Add\" button above."]
        [item-list]))))
(defn list-panel []
  [:div
   [list-header]
   [nav-buttons]
   [item-list-or-message]])