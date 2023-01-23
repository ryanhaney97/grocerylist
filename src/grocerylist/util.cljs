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

(defn callback-factory-factory
  "returns a function which will always return the `same-callback` every time
   it is called.
   `same-callback` is what actually calls your `callback` and, when it does,
   it supplies any necessary args, including those supplied at wrapper creation
   time and any supplied by the browser (a DOM event object?) at call time.
   NOTE: Copied from the re-frame docs: https://day8.github.io/re-frame/on-stable-dom-handlers/#the-technique"
  [the-real-callback]
  (let [*args1        (atom nil)
        same-callback (fn [& args2]
                        (apply the-real-callback (concat @*args1 args2)))]
    (fn callback-factory
      [& args1]
      (reset! *args1 args1)
      same-callback)))

(defn id->itemnum [list id]
  (first
    (filter (comp #{id} :id (partial nth list)) (range (count list)))))
