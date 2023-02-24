(ns grocerylist.views.locations
  (:require
    [grocerylist.views.util :refer [<sub >evt] :as u]
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
  (r/with-let [on-change (fn [event]
                           (re-frame/dispatch-sync [::events.forms.location/update-name (.-value (.-target event))]))]
    [sui/Input {:name "location-name"
                :action true}
     [:input {:value (<sub [::subs.forms.location/name])
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
  (let [errors (<sub [::errors/location-form])]
    [sui/FormField {:error (boolean errors)}
     [:label {:for "location-name"} "New Location: "]
     [location-text-input]
     [error-label errors]]))

(defn location-form []
  [sui/Form {:on-submit (>evt [::events.forms.location/submit])}
   [location-name-field]])

(defn location-delete-button [id]
  [sui/Button {:on-click (>evt [::events.confirm/delete-location id])
               :negative true
               :icon "delete"}])

(defn location-item [location index]
  [sui/TableRow
   [sui/TableCell {:collapsing true}
    [location-delete-button index]]
   [sui/TableCell {:draggable "true"
                   :selectable true
                   :on-drag-start (>evt [::events.locations/drag-start index])
                   :on-drag-end (>evt [::events.locations/drag-end])
                   :on-drag-enter (>evt [::events.locations/drag-over index])}
    [:span {:style {:opacity (if (<sub [::subs.locations/dragged? index])
                               0.0
                               100.0)
                    :padding-left "1em"}}
     location]]])

(defn location-list []
  [sui/Table {:celled true
              :striped true}
   [sui/TableBody
    (map-indexed (fn [index location] ^{:key location} [location-item location index]) (<sub [::subs.locations/list]))]])

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