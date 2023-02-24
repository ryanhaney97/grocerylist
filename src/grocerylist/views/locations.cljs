(ns grocerylist.views.locations
  (:require
    [grocerylist.views.util :as u]
    [grocerylist.subs.locations :as subs.locations]
    [grocerylist.subs.forms.location :as subs.forms.location]
    [grocerylist.subs.errors :as errors]
    [grocerylist.events.locations :as events.locations]
    [grocerylist.events.forms.location :as events.forms.location]
    [grocerylist.events.confirm :as events.confirm]
    [semantic-ui-reagent.core :as sui]
    [re-frame.core :as re-frame]
    [reagent.core :as r]))

(defn location-text-input []
  (r/with-let [location-name (re-frame/subscribe [::subs.forms.location/name])
               on-change (fn [event] (re-frame/dispatch-sync [::events.forms.location/update-name (.-value (.-target event))]))]
    [sui/Input {:name "location-name"
                :action true}
     [:input {:value @location-name
              :on-change on-change}]
     [sui/Button {:primary true
                  :type "submit"}
      "Add"]]))

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
  (r/with-let [on-submit #(re-frame/dispatch [::events.forms.location/submit])]
    [sui/Form {:on-submit on-submit}
     [location-name-field]]))

(defn location-delete-button [id]
  (r/with-let [on-click (fn [id] (re-frame/dispatch [::events.confirm/delete-location id]))]
    [sui/Button {:on-click (r/partial on-click id)
                 :negative true
                 :icon "delete"}]))

(defn location-item [_ location index]
  (r/with-let [on-drag-start (fn [index] (re-frame/dispatch [::events.locations/drag-start index]))
               on-drag-end #(re-frame/dispatch [::events.locations/drag-end])
               on-drag-enter (fn [index] (re-frame/dispatch [::events.locations/drag-over index]))]
    (let [dragged? @(re-frame/subscribe [::subs.locations/dragged? index])]
      [sui/TableRow
       [sui/TableCell {:collapsing true}
        [location-delete-button index]]
       [sui/TableCell {:draggable "true"
                       :selectable true
                       :on-drag-start (r/partial on-drag-start index)
                       :on-drag-end on-drag-end
                       :on-drag-enter (r/partial on-drag-enter index)}
        [:span {:style {:opacity (if dragged?
                                   0.0
                                   100.0)
                        :padding-left "1em"}}
         location]]])))

(defn location-list []
  [sui/Table {:celled true
              :striped true}
   [sui/TableBody
    (let [list (re-frame/subscribe [::subs.locations/list])]
      (map-indexed (fn [index location] [location-item {:key location} location index]) @list))]])

(defn location-panel []
  (r/with-let [prevent-default (fn [event]
                                 (.preventDefault event))
               _ (.addEventListener js/document "dragover" prevent-default)]
    [sui/Container
     [u/nav-button :list "Back"]
     [sui/Header {:as "h1"
                  :text-align "center"}
      "Locations"]
     [location-form]
     [location-list]]
    (finally
      (.removeEventListener js/document "dragover" prevent-default))))