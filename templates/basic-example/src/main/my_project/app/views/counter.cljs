(ns *|USER-PROJECT-NAME|*.app.views.counter)

(defn counter [{:keys [counter inc-fn dec-fn]}]
  [:div.counter  ;; `.counter` is shorthand for `{:class "counter"}`
   [:div {:style {:display "flex" :justify-content "center"}}
    [:button.dec {:on-click dec-fn} "− 1"]
    [:span.val counter]
    [:button.inc {:on-click inc-fn} "+ 1"]]])
