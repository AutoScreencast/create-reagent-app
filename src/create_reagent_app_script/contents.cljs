(ns create-reagent-app-script.contents)

;; We need to do this, since `npm publish` ignores the `.gitignore` when packaging.
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
