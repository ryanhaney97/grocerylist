(ns grocerylist.db)

(def default-db
  {:listname "Grocery List"
   :sort-method :checked?
   :sort-reversed? false
   :route :list
   :list []
   :itemform {:name ""}
   :locations ["Produce"
               "Aisle 1"
               "Aisle 2"
               "Aisle 3"
               "Aisle 4"]
   :locationform {:name ""}
   :location.dragged nil})
