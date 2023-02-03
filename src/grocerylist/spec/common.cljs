(ns grocerylist.spec.common
  (:require
    [cljs.spec.alpha :as s]
    [clojure.string :as string]))

(s/def ::not-blank-string (s/and string? (complement empty?) (complement string/blank?)))