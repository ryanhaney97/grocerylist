(ns grocerylist.views.util
  (:require
    [re-frame.core :as re-frame]
    [reagent.core :as r]
    [semantic-ui-reagent.core :as sui]
    [grocerylist.events.route :as events.route]))

(def <sub (comp deref re-frame.core/subscribe))
(def >evt (partial r/partial re-frame.core/dispatch))
(def >evt-now (partial r/partial re-frame.core/dispatch-sync))

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
(defn button-factory [text on-click]
  (fn [& _]
    (let [on-click-factory (callback-factory-factory on-click)]
      (fn [& args]
        [:button {:type "button"
                  :on-click (apply on-click-factory args)}
         text]))))

(defn nav-button [route text & {:as props}]
  (let [on-click (fn [] (re-frame/dispatch [::events.route/to route]))]
    (fn [_ _ & {:as props}]
      [sui/Button (merge {:on-click on-click} props)
       text])))

(defn form-text-input-factory [name value-sub update-event submit-event]
  (let [value (re-frame/subscribe [value-sub])
        on-change (fn [event]
                    (re-frame/dispatch-sync [update-event (.-value (.-target event))]))
        on-key-down (fn [event]
                      (if (= (.-key event) "Enter")
                        (re-frame/dispatch [submit-event])))]
    (fn [& _]
      [:input {:name name
               :type "text"
               :value @value
               :on-change on-change
               :on-key-down on-key-down}])))
(defn display-errors [& subscription-vector]
  (let [messages @(re-frame/subscribe (into [] subscription-vector))]
    (when messages
      [:div
       (map-indexed
         (fn [index message]
           [:div {:class "error"
                  :key index}
            message]) messages)])))