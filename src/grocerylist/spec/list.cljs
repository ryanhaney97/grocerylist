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

;Clojure Spec doesn't support cross-validating keys very well, and so the following is a workaround,
;in order to ensure each item has a location in the list of locations. This COULD be done with a single predicate
;checking from ::list, but if that is done it won't preserve WHERE the error occurred in explain-data.
;So, we first call "make-check-item-locations", which creates new "s/def"s wrapping around the location list,
;which is then used to validate each item.
;So DO NOT call any of the :check-location/* specs directly, and instead call ::items.locations if you need to check.
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