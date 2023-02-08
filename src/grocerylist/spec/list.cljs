(ns grocerylist.spec.list
  (:require
    [cljs.spec.alpha :as s]
    [grocerylist.spec.item :as item]
    [grocerylist.spec.common :as common]))

(s/def ::name ::common/not-blank-string)
(s/def ::items (s/map-of ::common/is-int ::item/item))
(s/def ::location ::common/not-blank-string)
(s/def ::locations (s/coll-of ::location :distinct true))
(s/def ::items.next-id ::common/is-int)
(defn make-check-item-locations [{:keys [locations]}]
  (s/def :check-location/location (into #{} locations))
  (s/def :check-location/item (s/keys :req-un [:check-location/location]))
  (s/def :check-location/items (s/map-of ::common/is-int :check-location/item))
  true)
(s/def ::items.locations
  (s/and
    make-check-item-locations
    (s/keys :req-un [:check-location/items])))
(s/def ::list (s/merge
                (s/keys :req-un [::name ::items ::locations ::items.next-id])
                ::items.locations))