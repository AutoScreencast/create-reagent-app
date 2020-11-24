(ns create-reagent-app-script.core
  (:require [clojure.string :as str]
            [clojure.tools.cli :refer [parse-opts]]
            [cljs-node-io.core :refer [slurp]]
            [fs :refer [existsSync mkdirSync appendFileSync]]
            [path]
            [create-reagent-app-script.contents :as contents]))

(def cli-options [;; Specification of --css flag requires an argument, such as `tailwindcss`, otherwise defaults to `basic`
                  ["-c" "--css CSS-Library" "CSS Library, for example: tailwindcss"
                   :default "basic"
                   :parse-fn #(str %)]
                  ["-h" "--help" "Usage Help"]])

(def argv-map (parse-opts (js->clj js/process.argv) cli-options))

;; Get imput command line arguments from Node process
;; ;; `npx create-react-app my-app --re-frame` will give us user-specified-args of [my-app --re-frame]
(def user-specified-args (subvec (:arguments argv-map) 2))

;; eg. "my-app"
(def user-project-name (str (first user-specified-args)))

;; Get CSS Library...
; (if (= (-> argv-map :options :css) "tailwindcss")
;   (println "OOOOOH! You want to use tailwindcss. Good stuff!")
;   (println "Falling back on basic CSS."))

;; __dirname: "/Users/username/.npm/_npx/ddddd..dd/node_modules/create-reagent-app/bin"
;; Remove the final "/bin" from the __dirname
;; Should be "/Users/username/.npm/_npx/ddddd..dd/node_modules/create-reagent-app"
(def BASE_FOLDER (path/join js/__dirname ".."))

;; Get version number
(def cra-version-number
  (-> BASE_FOLDER
      (str "/package.json")
      (slurp)
      (js/JSON.parse)
      (js->clj :keywordize-keys true)
      (:version)))

(when (:help (:options argv-map))
  (->> [""
        (str "================== USAGE HELP for CREATE REAGENT APP v" cra-version-number " ===================")
        ""
        "Usage:"
        "  `npx create-react-app <your-project-name> [options]`"
        ""
        "Options:"
        (:summary argv-map)
        ""
        "================================================================================"
        ""]
       (str/join \newline)
       (println))
  (js/process.exit 0))

;; Exit from process if user-specified-args are empty
(when (empty? user-specified-args)
  (->> [""
        "================= THERE IS A PROBLEM !!! PLEASE TRY AGAIN !!! =================="
        ""
        "You must provide a project name:"
        "  `npx create-reagent-app <your-project-name>`"
        ""
        "For example:"
        "  `npx create-reagent-app my-app`"
        ""
        "================================================================================"
        ""]
       (str/join \newline)
       (print))
  (js/process.exit 1))

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
(defn create-folders! []
  (maybe-create-folder! FILEPATH_USER_PROJECT_NAME_FOLDER)
  (maybe-create-folder! FILEPATH_PUBLIC_FOLDER)
  (maybe-create-folder! FILEPATH_PUBLIC_CSS_FOLDER)
  (maybe-create-folder! FILEPATH_SRC_FOLDER)
  (maybe-create-folder! FILEPATH_SRC_UPN_FOLDER)
  (maybe-create-folder! FILEPATH_SRC_UPN_APP_FOLDER))

;; Output files
(def FILEPATH_USER_PROJECT_NAME_FOLDER_SHADOW_CLJS_EDN (path/join FILEPATH_USER_PROJECT_NAME_FOLDER "shadow-cljs.edn"))
(def FILEPATH_USER_PROJECT_NAME_FOLDER_PACKAGE_JSON    (path/join FILEPATH_USER_PROJECT_NAME_FOLDER "package.json"))
(def FILEPATH_USER_PROJECT_NAME_FOLDER_DOT_GITIGNORE   (path/join FILEPATH_USER_PROJECT_NAME_FOLDER ".gitignore"))
(def FILEPATH_PUBLIC_FOLDER_INDEX_HTML                 (path/join FILEPATH_PUBLIC_FOLDER "index.html"))
(def FILEPATH_PUBLIC_CSS_FOLDER_STYLE_CSS              (path/join FILEPATH_PUBLIC_CSS_FOLDER "style.css"))
(def FILEPATH_SRC_UPN_APP_FOLDER_CORE_CLJS             (path/join FILEPATH_SRC_UPN_APP_FOLDER "core.cljs"))

;; Create files with content
(defn create-files-with-content! []
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
   (contents/gitignore-file-contents)))

;; --- Run ---

(defn print-following-steps! []
  (->> [""
        (str "========================== CREATE REAGENT APP v" cra-version-number " ==========================")
        ""
        (str "Your app `" user-project-name "` was successfully created. Now perform these 4 steps:")
        ""
        (str "1. Change directory into project folder:   `cd " user-project-name "`")
        "2. Install dependencies using npm or yarn: `npm install` or `yarn install`"
        "3. Run app with:                           `npm start`   or `yarn start`"
        "4. Open your browser at:                   `localhost:3000`"
        ""
        "================================================================================"
        ""]
       (str/join \newline)
       (println)))

(defn ^:export main []
  (create-folders!)
  (create-files-with-content!)
  (print-following-steps!))

(defn ^:dev/after-load reload! []
  (println "reload!"))
