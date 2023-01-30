(ns grocerylist.routes
  (:require
    [bidi.bidi :as bidi]
    [pushy.core :as pushy]
    [re-frame.core :as re-frame]
    [grocerylist.events :as events]))

(defmulti panels identity)
(defmethod panels :default [] [:div "No panel found for this route."])

(def routes
  (atom
    ["/" {"" :lists
          "new" :newlist
          [:id ""] :list
          [:id "/add"] :additem
          [:id "/locations"] :locations}]))

(defn parse
  [url]
  (when-let [route (bidi/match-route @routes url)]
    (if (get-in route [:route-params :id])
      (update-in route [:route-params :id] js/parseInt)
      route)))

(defn url-for
  [& args]
  (apply bidi/path-for (into [@routes] args)))

(defn dispatch
  [route]
  (re-frame/dispatch [::events/set-route route]))

(defonce history
         (pushy/pushy dispatch parse))

(defn navigate!
  [handler]
  (pushy/set-token! history (apply url-for handler)))

(defn start!
  []
  (pushy/start! history))

(re-frame/reg-fx
  :navigate
  (fn [handler]
    (navigate! handler)))
