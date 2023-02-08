(ns grocerylist.spec.db
  (:require
    [cljs.spec.alpha :as s]
    [grocerylist.spec.list :as list]
    [grocerylist.spec.common :as common]))

(s/def ::lists (s/map-of ::common/is-int ::list/list))
(s/def ::lists.next-id ::common/is-int)
(s/def ::sort-method #{:name :location :checked?})
(s/def ::sort-reversed? boolean?)
(s/def ::route #{:lists :new-list :list :add-item :locations})
(s/def ::current-list-id ::common/is-int)
(s/def ::db (s/keys :req-un [::lists ::lists.next-id ::sort-method ::sort-reversed? ::route ::current-list-id]))