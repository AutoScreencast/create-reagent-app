(ns create-reagent-app-script.contents)

;; We need to do this, since `npm publish` ignores the `.gitignore` and `README.md` when packaging.
(defn gitignore-file-contents []
  "# See https://help.github.com/articles/ignoring-files/ for more about ignoring files.

# npm dependencies
/node_modules

# shadow-cljs
/.shadow-cljs
report.html

# generated js
/public/js
/out

# misc
.DS_Store
npm-debug.log*
yarn-debug.log*
yarn-error.log*
")

(defn readme-file-contents []
  "<img src=\"https://raw.githubusercontent.com/cljs/logo/master/cljs.svg\" height=\"120\">

# Create Reagent App

A simple way to bootstrap a ClojureScript (CLJS) web-app using:

- [Shadow-CLJS](http://shadow-cljs.org/) as the build tool / compiler

- [Reagent](https://github.com/reagent-project/reagent) (CLJS wrapper around [React](https://reactjs.org/)) for building your user interface

---
")


