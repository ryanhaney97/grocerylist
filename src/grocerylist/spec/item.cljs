(ns grocerylist.spec.item
  (:require
    [cljs.spec.alpha :as s]
    [grocerylist.spec.common :as common]))

(s/def ::name ::common/not-blank-string)
(s/def ::location ::common/not-blank-string)

(s/def ::checked? boolean?)

(s/def ::item (s/keys :req-un [::name ::location ::checked?]))