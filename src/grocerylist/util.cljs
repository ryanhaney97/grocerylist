(ns grocerylist.util)

(defn removenth [coll index]
  (into []
        (concat
          (subvec coll 0 index)
          (subvec coll (inc index)))))

(defn make-index-map [coll]
  (apply
    assoc {} (interleave coll (range))))

(defn swap [coll a b]
  (assoc coll a (get coll b) b (get coll a)))

(defn sort-by-name
  ([item-coll reversed?]
   (sort-by :name (if reversed? > <) item-coll))
  ([item-coll]
   (sort-by-name item-coll false)))

(defn sort-by-location
  ([locations item-coll reversed?]
   (let [locations (if reversed? (rseq locations) locations)
         grouped-items (group-by :location item-coll)]
     (apply concat (map (fn [location]
                          (sort-by-name (get grouped-items location []))) locations))))
  ([locations item-coll]
   (sort-by-location locations item-coll false)))

(defn sort-by-checked
  ([locations item-coll reversed?]
   (let [grouped-items (group-by :checked? item-coll)]
    (concat (sort-by-location locations (get grouped-items reversed? [])) (sort-by-location locations (get grouped-items (not reversed?) [])))))
  ([locations item-coll]
   (sort-by-checked locations item-coll false)))

(def sorting-method-map
  {:name (fn [_ & args]
           (apply sort-by-name args))
   :location sort-by-location
   :checked? sort-by-checked})