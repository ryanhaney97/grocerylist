(ns grocerylist.spec.common
  (:require
    [cljs.spec.alpha :as s]
    [clojure.string :as string]))

(s/def ::not-nil some?)
(s/def ::is-string (s/and ::not-nil string?))
(s/def ::is-int (s/and ::not-nil int?))
(s/def ::not-empty not-empty)
(s/def ::not-blank-string (s/and ::is-string ::not-empty (complement string/blank?)))