(ns create-reagent-app-script.contents)

(defn shadow-cljs-edn-file-contents [user-project-name]
  (str "{:source-paths [\"src\"]
  :dependencies [[reagent \"0.10.0\"]]
  :dev-http {3000 \"public\"}
  :builds {:app {:target :browser
                :output-dir \"public/js\"
                :asset-path \"/js\"
                :modules {:main {:init-fn " user-project-name ".app.core/main}}}}}
"))

;; `version` looks like this: "0.1.4" (as a quoted string)
(defn package-json-file-contents [user-project-name version]
  (str "{
  \"name\": \"create-reagent-app\",
  \"description\": \"Bootstrap a ClojureScript frontend app that uses Reagent for the user interface and Shadow-CLJS as its build tool / compiler.\",
  \"keywords\": [
    \"ClojureScript\",
    \"CLJS\",
    \"Reagent\",
    \"React\",
    \"Shadow-CLJS\",
    \"SPA\",
    \"Frontend\",
    \"Webapp\"
  ],
  \"license\": \"MIT\",
  \"homepage\": \"https://github.com/AutoScreencast/create-reagent-app#readme\",
  \"version\": " \"version \" ",
  \"private\": false,
  \"bin\": {
    \"create-reagent-app\": \"bin/index.js\"
  },
  \"scripts\": {
    \"execute\": \"node bin/index.js " user-project-name \" ",
    \"start\": \"shadow-cljs watch script\",
    \"build\": \"shadow-cljs release script\",
    \"report\": \"shadow-cljs run shadow.cljs.build-report script report.html\",
    \"debug-build\": \"shadow-cljs release script --debug\",
    \"dev-build\": \"shadow-cljs compile script\",
    \"repl\": \"shadow-cljs cljs-repl script\",
    \"node-repl\": \"shadow-cljs node-repl\",
    \"clojure-repl\": \"shadow-cljs clj-repl\",
    \"clean\": \"rimraf public/js\",
    \"nuke\": \"rimraf public/js .shadow-cljs node_modules yarn.lock package-lock.json\"
  },
  \"dependencies\": {
    \"fs-extra\": \"^9.0.1\",
    \"rimraf\": \"^3.0.2\",
    \"shadow-cljs\": \"2.11.7\",
    \"source-map-support\": \"^0.5.19\"
  }
}
"))

(defn gitignore-file-contents []
  "# See https://help.github.com/articles/ignoring-files/ for more about ignoring files.

# npm dependencies
/node_modules

# shadow-cljs
/.shadow-cljs
report.html

# generated js
/public/js

# misc
.DS_Store
npm-debug.log*
yarn-debug.log*
yarn-error.log*
")

(defn index-html-contents []
  "<!doctype html>
<html lang=\"en\">
  <head>
    <meta charset=\"utf-8\" />
		<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\" />
		<link rel=\"stylesheet\" href=\"/css/style.css\" type=\"text/css\" />
    <title>Create Reagent App</title>
  </head>
  <body>
		<noscript>You need to enable JavaScript to run this app.</noscript>
    <div id=\"root\"></div>
    <script src=\"/js/main.js\"></script>
  </body>
</html>
")

(defn style-css-contents []
  "/* Edit these styles */

html {
  box-sizing: border-box;
  font-size: 16px;
}

* {
  box-sizing: inherit;
}

body,
h1,
h2,
h3,
h4,
h5,
h6,
p,
ol,
ul {
  margin: 0;
  padding: 0;
  font-weight: normal;
}

body {
  font-family: -apple-system, BlinkMacSystemFont, \"Helvetica Neue\", sans-serif;
}

.wrapper {
  padding: 2rem;
}

.counter {
  padding: 3rem 0;
  display: flex;
  justify-content: center;
}

button {
  border: none;
  border-radius: 1rem;
  color: white;
  text-align: center;
  padding: 2rem 3rem;
  text-decoration: none;
  font-size: 2rem;
  cursor: pointer;
}

button:focus {
  outline: thin dotted;
}

.inc {
  background-color: rgba(0, 140, 186, 1); /* Blue */
}

.inc:hover {
  background-color: rgba(0, 140, 186, 0.8);
}

.dec {
  background-color: rgba(244, 67, 54, 1); /* Red */
}

.dec:hover {
  background-color: rgba(244, 67, 54, 0.8);
}

.val {
  font-size: 6rem;
  font-weight: lighter;
  text-align: center;
  width: 12rem;
  height: 8rem;
}
")

(defn core-cljs-contents [user-project-name]
  (str "(ns " user-project-name ".app.core
  (:require [reagent.core :as r]
            [reagent.dom :as rdom]))

;; --- App State ---

(defonce counter (r/atom 0))  ;; Use `defonce` to preserve atom value across hot reloads

;; --- Component Views ---

(defn header []
  [:div {:style {:display \"flex\" :align-items \"center\"}}
   [:img {:src \"https://raw.githubusercontent.com/cljs/logo/master/cljs.svg\" :width 120}]
   [:h1 {:style {:padding \"1rem\" :font-weight \"bold\"}} \"Create Reagent App\"]])

(defn description []
  [:p {:style {:padding-top \"2rem\"}}
   \"Bootstrap a ClojureScript frontend app that uses \"
   [:a {:href \"https://github.com/reagent-project/reagent\" :target \"_blank\"} \"Reagent\"]
   \" for your user interface and \"
   [:a {:href \"http://shadow-cljs.org/\" :target \"_blank\"} \"Shadow-CLJS\"]
   \" as its build tool / compiler.\"])

(defn aside []
  [:aside {:style {:padding-top \"2rem\" :font-weight \"lighter\" :line-height \"1.4rem\"}}
   \"Note that the CLJS image on this page is in the \" [:code \"/public/img\"] \" folder, \"
   \"and the CSS stylesheet is in the \" [:code \"/public/css\"] \" folder. \"
   \"Feel free to edit the CSS stylesheet in \" [:code \"/public\"] \". Only the \"
   [:code \"/public/js\"] \" folder is generated by the compiler, and should \"
   [:span {:style {:font-weight \"bolder\"}} \"not\"] \" be edited directly.\"])

(defn count-component [value]
  [:div.counter  ;; .counter is shorthand for {:class \"counter\"}
   [:div {:style {:display \"flex\" :justify-content \"center\"}}
    [:button.dec {:on-click #(swap! value dec)} \"− 1\"]
    [:span.val (str @value)]
    [:button.inc {:on-click #(swap! value inc)} \"+ 1\"]]])

(defn app []
  [:div.wrapper
   [header]
   [description]
   [aside]
   [count-component counter]])

;; --- Render App ---

(defn render []
  (rdom/render [app] (.getElementById js/document \"root\")))

;; `^:export` metadata prevents function name from being munged during `:advanced` release compilation
(defn ^:export main []
  (println \"Initial render\")
  (render))

;; `^:dev/after-load` metadata hook tells Shadow-CLJS to run this function after each hot reload
(defn ^:dev/after-load reload! []
  (println \"Reload!\")
  (render))
"))