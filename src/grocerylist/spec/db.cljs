(ns grocerylist.spec.db
  (:require
    [cljs.spec.alpha :as s]
    [grocerylist.spec.list :as list]))

(s/def ::lists (s/map-of int? ::list/list))
(s/def ::lists.next-id int?)
(s/def ::sort-method #{:name :location :checked?})
(s/def ::sort-reversed? boolean?)
(s/def ::route #{:lists :new-list :list :add-item :locations})
(s/def ::current-list-id int?)
(s/def ::db (s/keys :req-un [::lists ::lists.next-id ::sort-method ::sort-reversed? ::route ::current-list-id]))