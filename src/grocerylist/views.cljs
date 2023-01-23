(ns grocerylist.views
  (:require
    [re-frame.core :as re-frame]
    [grocerylist.subs :as subs]
    [grocerylist.events :as events]))

(defn draw-item [_ itemnum]
  (let [item @(re-frame/subscribe [::subs/listitem itemnum])]
    [:tr
     [:td
      [:button {:type "button"
                :on-click #(re-frame/dispatch [::events/delete-item itemnum])}
       "X"]]
     [:td (:name item "")]
     [:td (:location item "")]
     [:td
      [:input {:type "checkbox"
               :checked (:checked? item)
               :on-change #(re-frame/dispatch [::events/check-item itemnum])}]]]))

(defn draw-column-header [category category-name]
  (let [sort-method @(re-frame/subscribe [::subs/sort-method])
        sort-reversed? @(re-frame/subscribe [::subs/sort-reversed?])]
    [:th {:scope "col"
          :on-click #(re-frame/dispatch [::events/toggle-sort-method category])}
     (if (= category sort-method)
       (str category-name (if sort-reversed?
                            " \u25B2"
                            " \u25BC"))
       category-name)]))

(defn draw-table-header []
  [:thead
   [:tr
    [:th {:scope "col"} ""]
    [draw-column-header :name "Name"]
    [draw-column-header :location "Location"]
    [draw-column-header :checked? "Checked"]]])

(defn draw-item-list []
  [:table
   [draw-table-header]
   [:tbody
    (let [nlist @(re-frame/subscribe [::subs/nlist])]
      (map (fn [itemnum] [draw-item {:key itemnum} itemnum]) (range nlist)))]])

(defn draw-buttons []
  [:div
   [:button {:type "button"
             :on-click #(re-frame/dispatch [::events/route-to :additem])}
    "Add"]
   [:button {:type "button"
             :on-click #(re-frame/dispatch [::events/route-to :locations])}
    "Locations"]])

(defn draw-back-button []
  [:button {:type "button"
            :on-click #(re-frame/dispatch [::events/route-to :list])}
   "Back"])

(defn draw-list-name []
  (let [name @(re-frame/subscribe [::subs/listname])]
    [:h1 name]))

(defn main-list-panel []
  [:div
   [draw-list-name]
   [draw-buttons]
   [draw-item-list]])

(defn item-name-input []
  (let [currentname @(re-frame/subscribe [::subs/itemform.name])]
    [:div
     [:label {:for "name"} "Item Name: "]
     [:input {:name "name"
              :type "text"
              :value currentname
              :on-change (fn [event]
                           (re-frame/dispatch-sync [::events/itemform.update-name (.-value (.-target event))]))
              :on-key-down (fn [event]
                             (if (= (.-key event) "Enter")
                               (re-frame/dispatch [::events/itemform.add-item])))}]]))

(defn item-location-input-option [_ location-number]
  (let [location @(re-frame/subscribe [::subs/location-listitem location-number])]
    [:option {:value location}
     location]))

(defn item-location-input []
  (let [currentlocation @(re-frame/subscribe [::subs/itemform.location])
        locations-length @(re-frame/subscribe [::subs/nlocations])]
    [:div
     [:label {:for "location"} "Item Location: "]
     [:select {:value currentlocation
               :on-change (fn [event]
                            (re-frame/dispatch [::events/itemform.update-location (.-value (.-target event))]))}
      (map (fn [locationnum] [item-location-input-option {:key locationnum} locationnum]) (range locations-length))]]))

(defn add-item-panel []
  [:div
   [draw-back-button]
   [:h1 "Add New Item"]
   [item-name-input]
   [item-location-input]
   [:button {:type "button"
             :on-click #(re-frame/dispatch [::events/itemform.reset])}
    "Reset"]
   [:button {:type "button"
             :on-click #(re-frame/dispatch [::events/itemform.add-item])}
    "Add"]])

(defn draw-location-item [_ itemnum]
  (let [location @(re-frame/subscribe [::subs/location-listitem itemnum])
        hidden? @(re-frame/subscribe [::subs/location.dragged? itemnum])]
    ;(js/console.log (str "Draw Location " itemnum))
    [:tr {:draggable "true"
          :on-drag-start #(re-frame/dispatch [::events/locations.drag-start itemnum])
          :on-drag-end #(re-frame/dispatch [::events/locations.drag-end])
          :on-drag-enter #(re-frame/dispatch [::events/locations.drag-over itemnum])}
     [:td {:class (if hidden? "hidden" "")}
      location]]))

(defn draw-location-list []
  [:table
   [:tbody
    (let [nlist (re-frame/subscribe [::subs/nlocations])]
      (map (fn [itemnum] [draw-location-item {:key itemnum} itemnum]) (range @nlist)))]])

(defn draw-add-location-input []
  (let [currentname @(re-frame/subscribe [::subs/locationform.name])]
    [:input {:name "location"
             :type "text"
             :value currentname
             :on-change (fn [event]
                          (re-frame/dispatch-sync [::events/locationform.update-name (.-value (.-target event))]))
             :on-key-down (fn [event]
                            (if (= (.-key event) "Enter")
                              (re-frame/dispatch [::events/locationform.submit])))}]))

(defn draw-location-form []
  [:div
   [:label {:for "location"} "New Location: "]
   [draw-add-location-input]
   [:button {:type "button"
             :on-click #(re-frame/dispatch [::events/locationform.submit])}
    "Add"]])

(defn location-panel []
  [:div {:on-drag-over (fn [event]
                         (.preventDefault event))}
   [draw-back-button]
   [:h1 "Locations"]
   [draw-location-form]
   [draw-location-list]])

(defn main-router []
  (let [route @(re-frame/subscribe [::subs/route])]
    (condp = route
      :list [main-list-panel]
      :additem [add-item-panel]
      :locations [location-panel])))
