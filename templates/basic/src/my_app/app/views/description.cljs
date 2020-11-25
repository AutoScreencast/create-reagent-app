(ns *|USER-PROJECT-NAME|*.app.views.description)

(def reagent-github-url "https://github.com/reagent-project/reagent")

(def shadow-cljs-website-url "http://shadow-cljs.org/")

(defn description []
  [:p {:style {:padding-top "2rem"}}
   "Bootstrap a ClojureScript frontend app that uses "
   [:a {:href reagent-github-url :target "_blank"} "Reagent"]
   " for your user interface and "
   [:a {:href shadow-cljs-website-url :target "_blank"} "Shadow-CLJS"]
   " as its build tool / compiler."])
