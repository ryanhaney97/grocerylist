(ns grocerylist.views.add-list
  (:require
    [re-frame.core :as re-frame]
    [reagent.core :as r]
    [semantic-ui-reagent.core :as sui]
    [grocerylist.views.util :refer [<sub >evt]]
    [grocerylist.events.forms.list :as events.forms.list]
    [grocerylist.subs.forms.list :as subs.forms.list]
    [grocerylist.subs.errors :as errors]))

(defn list-name-input []
  (r/with-let [on-name-change (fn [event] (re-frame/dispatch-sync [::events.forms.list/update-name (.-value (.-target event))]))]
    [sui/Input {:action true}
     [:input {:value (<sub [::subs.forms.list/name])
              :on-change on-name-change}]
     [sui/Button {:on-click (>evt [::events.forms.list/submit])
                  :primary true}
      "Add"]]))

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
  (let [errors (<sub [::errors/list-form])]
    [sui/Form
     [sui/FormField {:error (boolean errors)}
      [:label "List Name: "]
      [list-name-input]
      [error-label errors]]]))

(defn add-list-panel []
  [sui/Container
   [header-text]
   [new-list-form]])