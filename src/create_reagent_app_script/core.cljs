(ns create-reagent-app-script.core
  (:require [clojure.string :as str]
            [cljs-node-io.core :refer [slurp]]
            [fs :refer [existsSync mkdirSync appendFileSync]]
            [path]
            [create-reagent-app-script.contents :as contents]))


;; Get imput command line arguments from Node process
(def arguments (js->clj js/process.argv))

;; `npx create-react-app my-app --re-frame` will give us options of [my-app --re-frame]
(def options (subvec arguments 2))

;; eg. "my-app"
(def user-project-name (str (first options)))

;; __dirname: "/Users/username/.npm/_npx/ddddd..dd/node_modules/create-reagent-app/bin"
;; Remove the final "/bin" from the __dirname
;; Should be "/Users/username/.npm/_npx/ddddd..dd/node_modules/create-reagent-app"
(def BASE_FOLDER (path/join js/__dirname ".."))

;; Constant filepaths (excluding the `.gitignore` file, which is excluded during `npm publish`)

(def FILEPATH_BASIC_TEMPLATE_SHADOW_CLJS_EDN          (str BASE_FOLDER "/templates/basic/shadow-cljs.edn"))
(def FILEPATH_BASIC_TEMPLATE_PACKAGE_JSON             (str BASE_FOLDER "/templates/basic/package.json"))
(def FILEPATH_BASIC_TEMPLATE_PUBLIC_INDEX_HTML        (str BASE_FOLDER "/templates/basic/public/index.html"))
(def FILEPATH_BASIC_TEMPLATE_PUBLIC_CSS_STYLE_CSS     (str BASE_FOLDER "/templates/basic/public/css/style.css"))
(def FILEPATH_BASIC_TEMPLATE_SRC_MY_APP_APP_CORE_CLJS (str BASE_FOLDER "/templates/basic/src/my_app/app/core.cljs"))

;; Utility Functions

(defn maybe-create-folder! [folder-name]
  (if (not (existsSync folder-name))
    (mkdirSync folder-name)
    (js/console.error (str "The folder " folder-name " already exists."))))

(defn replace-hyphens-with-underscores [folder-name]
  (str/replace folder-name \- \_))

(defn append-contents-to-file! [filepath contents]
  (appendFileSync filepath contents))

;; --- Linear sequence of steps ---

;; Output folders
(def FILEPATH_USER_PROJECT_NAME_FOLDER (path/resolve user-project-name))
(def FILEPATH_PUBLIC_FOLDER            (path/join FILEPATH_USER_PROJECT_NAME_FOLDER "public"))
(def FILEPATH_PUBLIC_CSS_FOLDER        (path/join FILEPATH_PUBLIC_FOLDER "css"))
(def FILEPATH_SRC_FOLDER               (path/join FILEPATH_USER_PROJECT_NAME_FOLDER "src"))
(def FILEPATH_SRC_UPN_FOLDER           (path/join FILEPATH_SRC_FOLDER (replace-hyphens-with-underscores user-project-name)))
(def FILEPATH_SRC_UPN_APP_FOLDER       (path/join FILEPATH_SRC_UPN_FOLDER "app"))

;; Create folders
(maybe-create-folder! FILEPATH_USER_PROJECT_NAME_FOLDER)
(maybe-create-folder! FILEPATH_PUBLIC_FOLDER)
(maybe-create-folder! FILEPATH_PUBLIC_CSS_FOLDER)
(maybe-create-folder! FILEPATH_SRC_FOLDER)
(maybe-create-folder! FILEPATH_SRC_UPN_FOLDER)
(maybe-create-folder! FILEPATH_SRC_UPN_APP_FOLDER)

;; Output files
(def FILEPATH_USER_PROJECT_NAME_FOLDER_SHADOW_CLJS_EDN (path/join FILEPATH_USER_PROJECT_NAME_FOLDER "shadow-cljs.edn"))
(def FILEPATH_USER_PROJECT_NAME_FOLDER_PACKAGE_JSON    (path/join FILEPATH_USER_PROJECT_NAME_FOLDER "package.json"))
(def FILEPATH_USER_PROJECT_NAME_FOLDER_DOT_GITIGNORE   (path/join FILEPATH_USER_PROJECT_NAME_FOLDER ".gitignore"))
(def FILEPATH_PUBLIC_FOLDER_INDEX_HTML                 (path/join FILEPATH_PUBLIC_FOLDER "index.html"))
(def FILEPATH_PUBLIC_CSS_FOLDER_STYLE_CSS              (path/join FILEPATH_PUBLIC_CSS_FOLDER "style.css"))
(def FILEPATH_SRC_UPN_APP_FOLDER_CORE_CLJS             (path/join FILEPATH_SRC_UPN_APP_FOLDER "core.cljs"))

;; Create files with content
(append-contents-to-file!
 FILEPATH_USER_PROJECT_NAME_FOLDER_SHADOW_CLJS_EDN
 (str/replace (slurp FILEPATH_BASIC_TEMPLATE_SHADOW_CLJS_EDN) "*|USER-PROJECT-NAME|*" user-project-name))

(append-contents-to-file!
 FILEPATH_USER_PROJECT_NAME_FOLDER_PACKAGE_JSON
 (str/replace (slurp FILEPATH_BASIC_TEMPLATE_PACKAGE_JSON) "*|USER-PROJECT-NAME|*" user-project-name))

(append-contents-to-file!
 FILEPATH_PUBLIC_FOLDER_INDEX_HTML
 (slurp FILEPATH_BASIC_TEMPLATE_PUBLIC_INDEX_HTML))

(append-contents-to-file!
 FILEPATH_PUBLIC_CSS_FOLDER_STYLE_CSS
 (slurp FILEPATH_BASIC_TEMPLATE_PUBLIC_CSS_STYLE_CSS))

(append-contents-to-file!
 FILEPATH_SRC_UPN_APP_FOLDER_CORE_CLJS
 (str/replace (slurp FILEPATH_BASIC_TEMPLATE_SRC_MY_APP_APP_CORE_CLJS) "*|USER-PROJECT-NAME|*" user-project-name))

;; Create `.gitignore` file and its contents without slurping
(append-contents-to-file!
 FILEPATH_USER_PROJECT_NAME_FOLDER_DOT_GITIGNORE
 (contents/gitignore-file-contents))

;; --- Run ---

;; FIXME: Not making any allowances for anything going wrong...
(defn output! []
  (println "")
  (println "=============================== CREATE REAGENT APP v0.0.33 ===============================")
  (println (str "Your app `" user-project-name "` was successfully created. Now perform these 4 steps:"))
  (println (str "1. Change directory into project folder: `cd " user-project-name "`"))
  (println "2. Install dependencies using npm or yarn: `npm install` or `yarn install`")
  (println "3. Run app with `npm start` or `yarn start`")
  (println "4. Open your browser at `localhost:3000`")
  (println "==========================================================================================")
  (println ""))

(defn ^:export main []
  (output!))

(defn ^:dev/after-load reload! []
  (println "reload!"))
