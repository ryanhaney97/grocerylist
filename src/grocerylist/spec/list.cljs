(ns grocerylist.spec.list
  (:require
    [cljs.spec.alpha :as s]
    [grocerylist.spec.item :as item]
    [grocerylist.spec.common :as common]))

(s/def ::name ::common/not-blank-string)
(s/def ::items (s/map-of int? ::item/item))
(s/def ::location ::common/not-blank-string)
(s/def ::locations (s/coll-of ::location :distinct true))
(s/def ::items.next-id int?)
(s/def ::list (s/keys :req-un [::name ::items ::locations ::items.next-id]))