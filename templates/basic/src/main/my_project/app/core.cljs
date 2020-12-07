(ns *|USER-PROJECT-NAME|*.app.core
  (:require [reagent.dom :as rdom]))

(defn app []
  [:h1 "Create Reagent App"])

(defn render []
  (rdom/render [app] (.getElementById js/document "root")))

(defn ^:export main []
  (render))

(defn ^:dev/after-load reload! []
  (render))
