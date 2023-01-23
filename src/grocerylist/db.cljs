(ns grocerylist.db)

(def default-db
  {:listname "Grocery List"
   :sort-method :checked?
   :sort-reversed? false
   :route :list
   :items {}
   :next-id 0
   :itemform {:name ""}
   :locations ["Produce"
               "Aisle 1"
               "Aisle 2"
               "Aisle 3"
               "Aisle 4"]
   :locationform {:name ""}
   :location.dragged nil})
