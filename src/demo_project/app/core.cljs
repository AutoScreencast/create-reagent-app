(ns demo-project.app.core
  (:require [reagent.dom :as rdom]))

;; --- Component Views ---

(defn app []
  [:<>
   [:h1 {:style {:padding "2rem"}} "Create Reagent App"]
   [:p "Bootstrap a ClojureScript frontend app that uses Reagent for the UI and Shadow-CLJS as its build tool."]])

;; --- Render App ---

(defn render []
  (rdom/render [app] (.getElementById js/document "root")))

(defn ^:export main []
  (println "Initial render of demo-project.app.core")
  (render))

(defn ^:dev/after-load reload! []
  (println "Reloaded demo-project.app.core")
  (render))
