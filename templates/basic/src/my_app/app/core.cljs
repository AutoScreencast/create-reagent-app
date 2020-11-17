(ns my-app.app.core
  (:require [reagent.core :as r]
            [reagent.dom :as rdom]))

;; --- App State ---

(defonce counter (r/atom 0))  ;; Use `defonce` to preserve atom value across hot reloads

;; --- Component Views ---

(defn header []
  [:div {:style {:display "flex" :align-items "center"}}
   [:img {:src "https://raw.githubusercontent.com/cljs/logo/master/cljs.svg" :width 120}]
   [:h1 {:style {:padding "1rem" :font-weight "bold"}} "Create Reagent App"]])

(defn description []
  [:p {:style {:padding-top "2rem"}}
   "Bootstrap a ClojureScript frontend app that uses "
   [:a {:href "https://github.com/reagent-project/reagent" :target "_blank"} "Reagent"]
   " for your user interface and "
   [:a {:href "http://shadow-cljs.org/" :target "_blank"} "Shadow-CLJS"]
   " as its build tool / compiler."])

(defn aside []
  [:aside {:style {:padding-top "2rem" :font-weight "lighter" :line-height "1.4rem"}}
   "Note that the CLJS image on this page is in the " [:code "/public/img"] " folder, "
   "and the CSS stylesheet is in the " [:code "/public/css"] " folder. "
   "Feel free to edit the CSS stylesheet in " [:code "/public"] ". Only the "
   [:code "/public/js"] " folder is generated by the compiler, and should "
   [:span {:style {:font-weight "bolder"}} "not"] " be edited directly."])

(defn count-component [value]
  [:div.counter  ;; .counter is shorthand for {:class "counter"}
   [:div {:style {:display "flex" :justify-content "center"}}
    [:button.dec {:on-click #(swap! value dec)} "− 1"]
    [:span.val (str @value)]
    [:button.inc {:on-click #(swap! value inc)} "+ 1"]]])

(defn app []
  [:div.wrapper
   [header]
   [description]
   [aside]
   [count-component counter]])

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