(ns grocerylist.views.locations
  (:require
    [grocerylist.views.util :as u]
    [grocerylist.subs.locations :as subs.locations]
    [grocerylist.subs.forms.location :as subs.forms.location]
    [grocerylist.subs.errors :as errors]
    [grocerylist.events.locations :as events.locations]
    [grocerylist.events.forms.location :as events.forms.location]
    [re-frame.core :as re-frame]))

(def add-button
  (u/button-factory
    "Add"
    (fn []
      (re-frame/dispatch [::events.forms.location/submit]))))

(defn location-error []
  [u/display-errors ::errors/location-form])

(defn location-text-input []
  (u/form-text-input-factory
    "location"
    ::subs.forms.location/name
    ::events.forms.location/update-name
    ::events.forms.location/submit))
(defn location-form []
  [:div
   [:label {:for "location"} "New Location: "]
   [location-text-input]
   [add-button]
   [location-error]])

(def location-delete-button
  (u/button-factory
    "X"
    (fn [id]
      (re-frame/dispatch-sync [::events.locations/confirm-delete id]))))

(defn location-item [_ location index]
  (let [on-drag-start (fn [index] (re-frame/dispatch [::events.locations/drag-start index]))
        drag-start-factory (u/callback-factory-factory on-drag-start)
        on-drag-end #(re-frame/dispatch [::events.locations/drag-end])
        on-drag-enter (fn [index] (re-frame/dispatch [::events.locations/drag-over index]))
        drag-enter-factory (u/callback-factory-factory on-drag-enter)]
    (fn [_ location index]
      (let [hidden? @(re-frame/subscribe [::subs.locations/dragged? index])]
        [:tr
         [:td
          [location-delete-button index]]
         [:td {:draggable "true"
               :on-drag-start (drag-start-factory index)
               :on-drag-end on-drag-end
               :on-drag-enter (drag-enter-factory index)
               :class (if hidden? "hidden" "")}
          location]]))))

(defn location-list []
  [:table
   [:tbody
    (let [list (re-frame/subscribe [::subs.locations/list])]
      (map-indexed (fn [index location] [location-item {:key location} location index]) @list))]])
(defn location-panel []
  [:div {:on-drag-over (fn [event]
                         (.preventDefault event))}
   [u/nav-button :list "Back"]
   [:h1 "Locations"]
   [location-form]
   [location-list]])