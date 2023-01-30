(ns grocerylist.db)

(def default-db
  {:lists {0 {:listname "Grocery List"
              :items {}
              :items.next-id 0
              :locations ["Produce"
                          "Aisle 1"
                          "Aisle 2"
                          "Aisle 3"
                          "Aisle 4"]}}

   :sort-method :checked?
   :sort-reversed? false
   :route :lists
   :current-list-id 0
   :lists.next-id 1
   :itemform {:name ""}
   :locationform {:name ""}
   :listform {:name ""}
   :location.dragged nil})
