(ns grocerylist.views.locations
  (:require
    [grocerylist.views.util :as u]
    [grocerylist.subs.locations :as subs.locations]
    [grocerylist.subs.forms.location :as subs.forms.location]
    [grocerylist.subs.errors :as errors]
    [grocerylist.events.locations :as events.locations]
    [grocerylist.events.forms.location :as events.forms.location]
    [semantic-ui-reagent.core :as sui]
    [re-frame.core :as re-frame]
    [reagent.core :as reagent]))

;(defn location-text-input []
;  (u/form-text-input-factory
;    "location"
;    ::subs.forms.location/name
;    ::events.forms.location/update-name
;    ::events.forms.location/submit))

(defn location-text-input []
  (let [location-name (re-frame/subscribe [::subs.forms.location/name])
        on-change (fn [event] (re-frame/dispatch-sync [::events.forms.location/update-name (.-value (.-target event))]))]
    (fn []
      [sui/Input {:name "location-name"
                  :action true}
       [:input {:value @location-name
                :on-change on-change}]
       [sui/Button {:primary true
                    :type "submit"}
        "Add"]])))

(defn error-label [errors]
  (when errors
    [sui/Label {:pointing "above"
                :style {:white-space "pre-wrap"}
                :color "red"}
     (apply str (interpose "\n" errors))]))

(defn location-name-field []
  (let [errors @(re-frame/subscribe [::errors/location-form])]
    [sui/FormField {:error (boolean errors)}
     [:label {:for "location-name"} "New Location: "]
     [location-text-input]
     [error-label errors]]))

(defn location-form []
  (let [on-submit #(re-frame/dispatch [::events.forms.location/submit])]
    (fn []
      [sui/Form {:on-submit on-submit}
       [location-name-field]])))

(defn location-delete-button [id]
  (let [on-click (fn [id] (re-frame/dispatch-sync [::events.locations/confirm-delete id]))
        on-click-factory (u/callback-factory-factory on-click)]
    (fn [id]
      [sui/Button {:on-click (on-click-factory id)
                   :negative true
                   :icon "delete"}])))

(defn location-item [_ location index]
  (let [on-drag-start (fn [index] (re-frame/dispatch [::events.locations/drag-start index]))
        drag-start-factory (u/callback-factory-factory on-drag-start)
        on-drag-end #(re-frame/dispatch [::events.locations/drag-end])
        on-drag-enter (fn [index] (re-frame/dispatch [::events.locations/drag-over index]))
        drag-enter-factory (u/callback-factory-factory on-drag-enter)]
    (fn [_ location index]
      (let [dragged? @(re-frame/subscribe [::subs.locations/dragged? index])]
        [sui/TableRow
         [sui/TableCell {:collapsing true}
          [location-delete-button index]]
         [sui/TableCell {:draggable "true"
                         :selectable true
                         :on-drag-start (drag-start-factory index)
                         :on-drag-end on-drag-end
                         :on-drag-enter (drag-enter-factory index)}
          [:span {:style {:opacity (if dragged?
                                     0.0
                                     100.0)
                          :padding-left "1em"}}
           location]]]))))

(defn location-list []
  [sui/Table {:celled true}
   [sui/TableBody
    (let [list (re-frame/subscribe [::subs.locations/list])]
      (map-indexed (fn [index location] [location-item {:key location} location index]) @list))]])
(defn location-panel []
  (let [prevent-default (fn [event]
                          (.preventDefault event))]
    (reagent/create-class
      {:component-did-mount
       (fn []
         (.addEventListener js/document "dragover" prevent-default))
       :component-will-unmount
       (fn []
         (.removeEventListener js/document "dragover" prevent-default))
       :reagent-render
       (fn []
         [sui/Container
          [u/nav-button :list "Back"]
          [sui/Header {:as "h1"
                       :text-align "center"}
           "Locations"]
          [location-form]
          [location-list]])})))