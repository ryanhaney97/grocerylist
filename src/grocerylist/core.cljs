(ns grocerylist.core
  (:require
   [reagent.dom :as rdom]
   [re-frame.core :as re-frame]
   [pushy.core :as pushy]
   [grocerylist.events :as events]
   [grocerylist.views :as views]
   [grocerylist.config :as config]
   [grocerylist.routes :as routing]))


(defn dev-setup []
  (when config/debug?
    (println "dev mode")))

(defn ^:dev/after-load mount-root []
  (re-frame/clear-subscription-cache!)
  (let [root-el (.getElementById js/document "app")]
    (rdom/unmount-component-at-node root-el)
    (rdom/render [views/main-router] root-el)))

(defn init []
  (re-frame/dispatch-sync [::events/initialize-db])
  (routing/start!)
  (dev-setup)
  (mount-root)
  (if (not (routing/parse (pushy/get-token routing/history)))
    (re-frame/dispatch [:grocerylist.events.route/to :lists])))
