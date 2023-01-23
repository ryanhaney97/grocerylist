(ns cljs.user
  "Commonly used symbols for easy access in the ClojureScript REPL during
  development."
  (:require
    [cljs.repl :refer (Error->map apropos dir doc error->str ex-str ex-triage
                                  find-doc print-doc pst source)]
    [clojure.pprint :refer (pprint)]
    [clojure.string :as str]))

(defn rand-word [length]
  (let [letters "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
        numletters (count letters)]
    (apply str
           (take length
                 (repeatedly
                   #(get letters (rand-int numletters)))))))

(defn add-rand-item!
  ([minlen maxlen]
   (let [n (+ minlen (rand-int (- maxlen minlen)))
         location (get-in @re-frame.db/app-db [:locations (rand-int (count (:locations @re-frame.db/app-db)))])]
     (re-frame.core/dispatch [:grocerylist.events/add-item (rand-word n) location]))))

(defn add-rand-items!
  ([numitems minlen maxlen]
   (if (> numitems 0)
     (do
       (add-rand-item! minlen maxlen)
       (recur (dec numitems) minlen maxlen)))))