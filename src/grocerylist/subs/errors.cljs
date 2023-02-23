(ns grocerylist.subs.errors
  (:require
    [re-frame.core :as re-frame]
    [cljs.spec.alpha :as s]
    [grocerylist.spec.common :as spec.common]
    [grocerylist.spec.list :as spec.list]
    [grocerylist.spec.item :as spec.item]
    [grocerylist.spec.db :as spec.db]))

(re-frame/reg-sub
  ::errors
  :-> :errors)

(re-frame/reg-sub
  ::forms
  :<- [::errors]
  :-> :forms)

(re-frame/reg-sub
  ::item-form.data
  :<- [::forms]
  :-> :item)

(re-frame/reg-sub
  ::location-form.data
  :<- [::forms]
  :-> :location)

(re-frame/reg-sub
  ::list-form.data
  :<- [::forms]
  :-> :list)

(re-frame/reg-sub
  ::db.data
  :<- [::errors]
  :-> :db)

;(re-frame/reg-sub
;  ::via
;  :<- [::error]
;  :-> :via)
;
;(re-frame/reg-sub
;  ::in
;  :<- [::error]
;  :-> :in)
;
;(re-frame/reg-sub
;  ::path
;  :<- [::error]
;  :-> :path)

(def item-form-error-message-map
  {[:items 1 :name]
   {::spec.common/not-empty "Please enter a name."
    ::spec.common/not-blank-string "Name cannot be blank."}
   [:items 1 :location]
   {::spec.common/not-nil "No locations exist! Please create some first."}})

(def list-form-error-message-map
  {[:lists 1 :name]
   {::spec.common/not-empty "Please enter a name for the list."
    ::spec.common/not-blank-string "List name cannot be blank."}})

(def location-form-error-message-map
  {[:locations]
   {::spec.list/locations "This location already exists!"
    ::spec.common/not-empty "Please enter a name for the location first."
    ::spec.common/not-blank-string "Location name cannot be blank."}})

(def item-error-message-map
  {[]
   {::spec.item/item "Item is missing keywords!"}
   [:name]
   {::spec.common/not-empty "Item name is empty!"
    ::spec.common/not-blank-string "Item name is blank!"
    ::spec.common/is-string "Item name is not a string!"
    ::spec.common/not-nil "Item name is nil!"}
   [:location]
   {::spec.common/not-nil "Item Location is nil!"
    ::spec.common/is-string "Item Location is not a string!"
    ::spec.common/not-empty "Location name cannot be empty, but somehow is."
    ::spec.common/not-blank-string "Location name cannot be blank, but somehow is."
    :check-location/location "Item location is not in the list of locations!"}})

(def list-error-message-map
  (merge
    (update-keys item-error-message-map (fn [k] (concat [:items 1] k)))
    {[:items 0]
     {::spec.common/not-nil "Item id is nil!"
      ::spec.common/is-int "Item id is not a number!"}
     [:name]
     {::spec.common/not-nil "List name is nil!"
      ::spec.common/is-string "List name is not a string!"
      ::spec.common/not-empty "List name is empty!"
      ::spec.common/not-blank-string "List name is blank!"}
     [:locations]
     {::spec.list/locations "Location list contains duplicate locations!"
      ::spec.common/not-nil "Location is nil!"
      ::spec.common/is-string "Location is not a string!"
      ::spec.common/not-empty "Location name is empty!"
      ::spec.common/not-blank-string "Location name is blank!"}
     [:items.next-id]
     {::spec.common/not-nil "Next item id is nil!"
      ::spec.common/is-int "Next item id is not an integer!"}
     []
     {::spec.list/list "List is missing keywords!"}}))

(def db-error-message-map
  (merge
    (update-keys list-error-message-map (fn [k] (concat [:lists 1] k)))
    {[:lists 0]
     {::spec.common/not-nil "List id is nil!"
      ::spec.common/is-int "List id is not an integer!"}
     [:lists.next-id]
     {::spec.common/not-nil "Next list id is nil!"
      ::spec.common/is-int "Next list id is not an integer!"}
     [:sort-method]
     {::spec.db/sort-method "Unknown sort method selected!"}
     [:route]
     {::spec.db/route "Unknown route selected!"}
     [:current-list-id]
     {::spec.common/not-nil "Current list id is nil!"
      ::spec.common/is-int "Current list id is not an integer!"}
     []
     {::spec.db/db "Root database is missing keywords!"}}))

(defn get-message [message-map problem]
  (get-in message-map [(:path problem) (last (:via problem))]))
(defn get-item-form-messages [item-error-data [_ kind]]
  (when item-error-data
    (let [problems (::s/problems item-error-data)
          problems (if (= kind :all) problems (filter #(= kind (last (:path %1))) problems))
          errors (filter not-empty (map (partial get-message item-form-error-message-map) problems))]
      (if (empty? errors)
        nil
        errors))))

(re-frame/reg-sub
  ::item-form
  :<- [::item-form.data]
  get-item-form-messages)

(defn get-location-form-messages [location-error-data]
  (when location-error-data
    (let [problems (::s/problems location-error-data)
          errors (filter not-empty (map (partial get-message location-form-error-message-map) problems))]
      (if (empty? errors)
        nil
        errors))))
(re-frame/reg-sub
  ::location-form
  :<- [::location-form.data]
  get-location-form-messages)

(defn get-list-form-messages [list-error-data]
  (when list-error-data
    (let [problems (::s/problems list-error-data)
          errors (filter not-empty (map (partial get-message list-form-error-message-map) problems))]
      (if (empty? errors)
        nil
        errors))))
(re-frame/reg-sub
  ::list-form
  :<- [::list-form.data]
  get-list-form-messages)

(defn get-db-error-messages [error-data]
  (when error-data
    (let [problems (::s/problems error-data)]
      (concat
        (map #(if %1 %1 "An unknown error has occurred!")
           (map (partial get-message db-error-message-map) problems))
        (list "Full Message: " (str (s/explain-str (::s/spec error-data) (::s/value error-data))))))))

(re-frame/reg-sub
  ::db
  :<- [::db.data]
  get-db-error-messages)