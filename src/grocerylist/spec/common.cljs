(ns grocerylist.spec.common
  (:require
    [cljs.spec.alpha :as s]
    [clojure.string :as string]))

;A number of these are technically redundant (e.g. checking for nil before checking the type),
;but are present so that the exact error can be tracked more precisely.
(s/def ::not-nil some?)
(s/def ::is-string (s/and ::not-nil string?))
(s/def ::is-int (s/and ::not-nil int?))
(s/def ::not-empty not-empty)
(s/def ::not-blank-string (s/and ::is-string ::not-empty (complement string/blank?)))