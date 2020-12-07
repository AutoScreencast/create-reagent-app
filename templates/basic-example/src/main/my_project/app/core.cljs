(ns *|USER-PROJECT-NAME|*.app.core
  (:require [reagent.core :as r]
            [reagent.dom :as rdom]
            [*|USER-PROJECT-NAME|*.app.views.header :refer [header]]
            [*|USER-PROJECT-NAME|*.app.views.description :refer [description]]
            [*|USER-PROJECT-NAME|*.app.views.aside :refer [aside]]
            [*|USER-PROJECT-NAME|*.app.views.counter :refer [counter] :rename {counter counter-component}]))

;; --- App State ---

(defonce counter (r/atom 0))  ;; Use `defonce` to preserve atom value across hot reloads

;; --- Utility Functions ---

(defn incrementer! [r]
  (swap! r inc))

(defn decrementer! [r]
  (swap! r dec))

;; --- App Component ---

(defn app []
  [:div.wrapper
   [header]
   [description]
   [aside]
   [counter-component {:counter (str @counter)
                       :inc-fn #(incrementer! counter)
                       :dec-fn #(decrementer! counter)}]])

;; --- Render App ---

(defn render []
  (rdom/render [app] (.getElementById js/document "root")))

;; `^:export` metadata prevents function name from being munged during `:advanced` release compilation
(defn ^:export main []
  (println "Initial render")
  (render))

;; `^:dev/after-load` metadata hook tells Shadow-CLJS to run this function after each hot reload
(defn ^:dev/after-load reload! []
  (println "Reload!")
  (render))
