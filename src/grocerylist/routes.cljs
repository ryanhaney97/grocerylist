(ns grocerylist.routes
  (:require
    [grocerylist.config :as config]
    [bidi.bidi :as bidi]
    [pushy.core :as pushy]
    [re-frame.core :as re-frame]))

(defmulti panels identity)
(defmethod panels :default [] [:div "No panel found for this route."])

(declare url-prefix)

(def routes
  ["/" {"" :lists
        "new" :new-list
        [:id ""] :list
        [:id "/add"] :add-item
        [:id "/locations"] :locations}])

(defn- unprefixed
  [path]
  (if (clojure.string/starts-with? path url-prefix)
    (subs path (count url-prefix))
    path))

(defn parse
  [url]
  (when-let [route (bidi/match-route routes (unprefixed url))]
    (if (get-in route [:route-params :id])
      (update-in route [:route-params :id] js/parseInt)
      route)))

(defn url-for
  [& args]
  (str "/#" (apply bidi/path-for (into [routes] args))))

(defn dispatch
  [route]
  (re-frame/dispatch [:grocerylist.events.route/set route]))

(defonce history
         (pushy/pushy dispatch parse))

(defn navigate!
  [handler]
  (pushy/set-token! history (apply url-for handler)))

(defn start!
  []
  (if config/debug?
    (def url-prefix "/#")
    (def url-prefix "/grocerylist/#"))
  (pushy/start! history))
