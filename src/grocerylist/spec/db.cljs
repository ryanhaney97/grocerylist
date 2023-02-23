(ns grocerylist.spec.db
  (:require
    [cljs.spec.alpha :as s]
    [bidi.bidi :as bidi]
    [grocerylist.spec.list :as list]
    [grocerylist.spec.common :as common]
    [grocerylist.routes :as routes]))

(s/def ::lists (s/map-of ::common/is-int ::list/list))
(s/def ::lists.next-id ::common/is-int)
(s/def ::sort-method #{:name :location :checked?})
(s/def ::sort-reversed? boolean?)
(def route-set
  (into #{}
        (conj
          (map :handler
               (bidi/route-seq routes/routes))
          :loading)))
(s/def ::route route-set)
(s/def ::current-list-id ::common/is-int)
(s/def ::db (s/keys :req-un [::lists ::lists.next-id ::sort-method ::sort-reversed? ::route ::current-list-id]))