(ns grocerylist.views.add-list
  (:require
    [re-frame.core :as re-frame]
    [semantic-ui-reagent.core :as sui]
    [grocerylist.views.util :as u]
    [grocerylist.events.forms.list :as events.forms.list]
    [grocerylist.subs.forms.list :as subs.forms.list]
    [grocerylist.subs.errors :as errors]))

(defn list-name-input []
  (let [on-name-change (fn [event] (re-frame/dispatch-sync [::events.forms.list/update-name (.-value (.-target event))]))
        on-submit #(re-frame/dispatch [::events.forms.list/submit])
        list-name (re-frame/subscribe [::subs.forms.list/name])]
    (fn []
      [sui/Input {:action true}
       [:input {:value @list-name
                :on-change on-name-change}]
       [sui/Button {:on-click on-submit
                    :primary true}
        "Add"]])))

;(defn list-name-input []
;  (u/form-text-input-factory
;    "name"
;    ::subs.forms.list/name
;    ::events.forms.list/update-name
;    ::events.forms.list/submit))

(def add-button
  (u/button-factory
    "Add"
    (fn []
      (re-frame/dispatch [::events.forms.list/submit]))))

(defn list-name-errors []
  [u/display-errors ::errors/list-form])

(defn header-text []
  [sui/Header {:as "h1"
               :text-align "center"}
   "Create a New List"])

(defn error-label [errors]
  (when errors
    [sui/Label {:pointing "above"
                :style {:white-space "pre-wrap"}
                :color "red"}
     (apply str (interpose "\n" errors))]))

(defn new-list-form []
  (let [errors @(re-frame/subscribe [::errors/list-form])]
    [sui/Form
     [sui/FormField {:error (boolean errors)}
      [:label "List Name: "]
      [list-name-input]
      [error-label errors]]]))

(defn add-list-panel []
  [sui/Container
   [header-text]
   [new-list-form]])