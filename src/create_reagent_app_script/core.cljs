(ns create-reagent-app-script.core
  (:require [clojure.string :as str]
            [clojure.tools.cli :refer [parse-opts]]
            [cljs-node-io.core :refer [slurp]]
            [fs :refer [existsSync mkdirSync appendFileSync copyFileSync]]
            [path]
            [create-reagent-app-script.contents :as contents]))

(def cli-options [["-c" "--css CSS-LIBRARY" "CSS library, for example: tailwindcss"
                   :default "basic"
                   :parse-fn #(str %)]
                  ["-p" "--package-manager PACKAGE-MANAGER" "Package Manager, for example: yarn. Defaults to: npm."
                   :default "npm"
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

;; Get the user's preferred package manager (npm or yarn):
(def pm (-> argv-map :options :package-manager))

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

;; Constant origin filepaths (excluding the `.gitignore` file, which is excluded during `npm publish`)

(def FILEPATH_BASIC_TEMPLATE_SHADOW_CLJS_EDN                       (str BASE_FOLDER "/templates/basic/shadow-cljs.edn"))
(def FILEPATH_BASIC_TEMPLATE_PACKAGE_JSON                          (str BASE_FOLDER "/templates/basic/package.json"))
(def FILEPATH_BASIC_TEMPLATE_PUBLIC_INDEX_HTML                     (str BASE_FOLDER "/templates/basic/public/index.html"))
(def FILEPATH_BASIC_TEMPLATE_PUBLIC_FAVICON_ICO                    (str BASE_FOLDER "/templates/basic/public/favicon.ico"))
(def FILEPATH_BASIC_TEMPLATE_PUBLIC_LOGO_192_PNG                   (str BASE_FOLDER "/templates/basic/public/cljs_logo_192.png"))
(def FILEPATH_BASIC_TEMPLATE_PUBLIC_LOGO_512_PNG                   (str BASE_FOLDER "/templates/basic/public/cljs_logo_512.png"))
(def FILEPATH_BASIC_TEMPLATE_PUBLIC_MANIFEST_JSON                  (str BASE_FOLDER "/templates/basic/public/manifest.json"))
(def FILEPATH_BASIC_TEMPLATE_PUBLIC_ROBOTS_TXT                     (str BASE_FOLDER "/templates/basic/public/robots.txt"))
(def FILEPATH_BASIC_TEMPLATE_PUBLIC_CSS_STYLE_CSS                  (str BASE_FOLDER "/templates/basic/public/css/style.css"))
(def FILEPATH_BASIC_TEMPLATE_PUBLIC_IMG_CLJS_SVG                   (str BASE_FOLDER "/templates/basic/public/img/cljs.svg"))
(def FILEPATH_BASIC_TEMPLATE_SRC_MY_APP_APP_CORE_CLJS              (str BASE_FOLDER "/templates/basic/src/my_app/app/core.cljs"))
(def FILEPATH_BASIC_TEMPLATE_SRC_MY_APP_APP_VIEWS_ASIDE_CLJS       (str BASE_FOLDER "/templates/basic/src/my_app/app/views/aside.cljs"))
(def FILEPATH_BASIC_TEMPLATE_SRC_MY_APP_APP_VIEWS_COUNTER_CLJS     (str BASE_FOLDER "/templates/basic/src/my_app/app/views/counter.cljs"))
(def FILEPATH_BASIC_TEMPLATE_SRC_MY_APP_APP_VIEWS_DESCRIPTION_CLJS (str BASE_FOLDER "/templates/basic/src/my_app/app/views/description.cljs"))
(def FILEPATH_BASIC_TEMPLATE_SRC_MY_APP_APP_VIEWS_HEADER_CLJS      (str BASE_FOLDER "/templates/basic/src/my_app/app/views/header.cljs"))

;; Utility Functions

(defn maybe-create-folder! [folder-name]
  (if (not (existsSync folder-name))
    (mkdirSync folder-name)
    (js/console.error (str "The folder " folder-name " already exists."))))

(defn replace-hyphens-with-underscores [folder-name]
  (str/replace folder-name \- \_))

(defn append-contents-to-file! [filepath contents]
  (appendFileSync filepath contents))

(defn copy-file [origin destination]
  (copyFileSync origin destination))

;; --- Linear sequence of steps ---

;; Output folders
(def FILEPATH_USER_PROJECT_NAME_FOLDER (path/resolve user-project-name))
(def FILEPATH_PUBLIC_FOLDER            (path/join FILEPATH_USER_PROJECT_NAME_FOLDER "public"))
(def FILEPATH_PUBLIC_CSS_FOLDER        (path/join FILEPATH_PUBLIC_FOLDER "css"))
(def FILEPATH_PUBLIC_IMG_FOLDER        (path/join FILEPATH_PUBLIC_FOLDER "img"))
(def FILEPATH_SRC_FOLDER               (path/join FILEPATH_USER_PROJECT_NAME_FOLDER "src"))
(def FILEPATH_SRC_UPN_FOLDER           (path/join FILEPATH_SRC_FOLDER (replace-hyphens-with-underscores user-project-name)))
(def FILEPATH_SRC_UPN_APP_FOLDER       (path/join FILEPATH_SRC_UPN_FOLDER "app"))
(def FILEPATH_SRC_UPN_APP_VIEWS_FOLDER (path/join FILEPATH_SRC_UPN_APP_FOLDER "views"))

;; Create folders
(defn create-folders! []
  (maybe-create-folder! FILEPATH_USER_PROJECT_NAME_FOLDER)
  (maybe-create-folder! FILEPATH_PUBLIC_FOLDER)
  (maybe-create-folder! FILEPATH_PUBLIC_CSS_FOLDER)
  (maybe-create-folder! FILEPATH_PUBLIC_IMG_FOLDER)
  (maybe-create-folder! FILEPATH_SRC_FOLDER)
  (maybe-create-folder! FILEPATH_SRC_UPN_FOLDER)
  (maybe-create-folder! FILEPATH_SRC_UPN_APP_FOLDER)
  (maybe-create-folder! FILEPATH_SRC_UPN_APP_VIEWS_FOLDER))

;; Output files
(def FILEPATH_USER_PROJECT_NAME_FOLDER_SHADOW_CLJS_EDN  (path/join FILEPATH_USER_PROJECT_NAME_FOLDER "shadow-cljs.edn"))
(def FILEPATH_USER_PROJECT_NAME_FOLDER_PACKAGE_JSON     (path/join FILEPATH_USER_PROJECT_NAME_FOLDER "package.json"))
(def FILEPATH_USER_PROJECT_NAME_FOLDER_DOT_GITIGNORE    (path/join FILEPATH_USER_PROJECT_NAME_FOLDER ".gitignore"))
(def FILEPATH_PUBLIC_FOLDER_INDEX_HTML                  (path/join FILEPATH_PUBLIC_FOLDER "index.html"))
(def FILEPATH_PUBLIC_FOLDER_FAVICON_ICO                 (path/join FILEPATH_PUBLIC_FOLDER "favicon.ico"))
(def FILEPATH_PUBLIC_FOLDER_LOGO_192_PNG                (path/join FILEPATH_PUBLIC_FOLDER "cljs_logo_192.png"))
(def FILEPATH_PUBLIC_FOLDER_LOGO_512_PNG                (path/join FILEPATH_PUBLIC_FOLDER "cljs_logo_512.png"))
(def FILEPATH_PUBLIC_FOLDER_MANIFEST_JSON               (path/join FILEPATH_PUBLIC_FOLDER "manifest.json"))
(def FILEPATH_PUBLIC_FOLDER_ROBOTS_TXT                  (path/join FILEPATH_PUBLIC_FOLDER "robots.txt"))
(def FILEPATH_PUBLIC_CSS_FOLDER_STYLE_CSS               (path/join FILEPATH_PUBLIC_CSS_FOLDER "style.css"))
(def FILEPATH_PUBLIC_IMG_FOLDER_CLJS_SVG                (path/join FILEPATH_PUBLIC_IMG_FOLDER "cljs.svg"))
(def FILEPATH_SRC_UPN_APP_FOLDER_CORE_CLJS              (path/join FILEPATH_SRC_UPN_APP_FOLDER "core.cljs"))
(def FILEPATH_SRC_UPN_APP_VIEWS_FOLDER_ASIDE_CLJS       (path/join FILEPATH_SRC_UPN_APP_VIEWS_FOLDER "aside.cljs"))
(def FILEPATH_SRC_UPN_APP_VIEWS_FOLDER_COUNTER_CLJS     (path/join FILEPATH_SRC_UPN_APP_VIEWS_FOLDER "counter.cljs"))
(def FILEPATH_SRC_UPN_APP_VIEWS_FOLDER_DESCRIPTION_CLJS (path/join FILEPATH_SRC_UPN_APP_VIEWS_FOLDER "description.cljs"))
(def FILEPATH_SRC_UPN_APP_VIEWS_FOLDER_HEADER_CLJS      (path/join FILEPATH_SRC_UPN_APP_VIEWS_FOLDER "header.cljs"))

;; Create or copy files with content
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

  (copy-file FILEPATH_BASIC_TEMPLATE_PUBLIC_IMG_CLJS_SVG FILEPATH_PUBLIC_IMG_FOLDER_CLJS_SVG)
  (copy-file FILEPATH_BASIC_TEMPLATE_PUBLIC_FAVICON_ICO FILEPATH_PUBLIC_FOLDER_FAVICON_ICO)
  (copy-file FILEPATH_BASIC_TEMPLATE_PUBLIC_LOGO_192_PNG FILEPATH_PUBLIC_FOLDER_LOGO_192_PNG)
  (copy-file FILEPATH_BASIC_TEMPLATE_PUBLIC_LOGO_512_PNG FILEPATH_PUBLIC_FOLDER_LOGO_512_PNG)
  (copy-file FILEPATH_BASIC_TEMPLATE_PUBLIC_MANIFEST_JSON FILEPATH_PUBLIC_FOLDER_MANIFEST_JSON)
  (copy-file FILEPATH_BASIC_TEMPLATE_PUBLIC_ROBOTS_TXT FILEPATH_PUBLIC_FOLDER_ROBOTS_TXT)

  (append-contents-to-file!
   FILEPATH_PUBLIC_CSS_FOLDER_STYLE_CSS
   (slurp FILEPATH_BASIC_TEMPLATE_PUBLIC_CSS_STYLE_CSS))

  (append-contents-to-file!
   FILEPATH_SRC_UPN_APP_FOLDER_CORE_CLJS
   (str/replace (slurp FILEPATH_BASIC_TEMPLATE_SRC_MY_APP_APP_CORE_CLJS) "*|USER-PROJECT-NAME|*" user-project-name))

  (append-contents-to-file!
   FILEPATH_SRC_UPN_APP_VIEWS_FOLDER_ASIDE_CLJS
   (str/replace (slurp FILEPATH_BASIC_TEMPLATE_SRC_MY_APP_APP_VIEWS_ASIDE_CLJS) "*|USER-PROJECT-NAME|*" user-project-name))

  (append-contents-to-file!
   FILEPATH_SRC_UPN_APP_VIEWS_FOLDER_COUNTER_CLJS
   (str/replace (slurp FILEPATH_BASIC_TEMPLATE_SRC_MY_APP_APP_VIEWS_COUNTER_CLJS) "*|USER-PROJECT-NAME|*" user-project-name))

  (append-contents-to-file!
   FILEPATH_SRC_UPN_APP_VIEWS_FOLDER_DESCRIPTION_CLJS
   (str/replace (slurp FILEPATH_BASIC_TEMPLATE_SRC_MY_APP_APP_VIEWS_DESCRIPTION_CLJS) "*|USER-PROJECT-NAME|*" user-project-name))

  (append-contents-to-file!
   FILEPATH_SRC_UPN_APP_VIEWS_FOLDER_HEADER_CLJS
   (str/replace (slurp FILEPATH_BASIC_TEMPLATE_SRC_MY_APP_APP_VIEWS_HEADER_CLJS) "*|USER-PROJECT-NAME|*" user-project-name))

  ;; Create `.gitignore` file and its contents without slurping
  (append-contents-to-file!
   FILEPATH_USER_PROJECT_NAME_FOLDER_DOT_GITIGNORE
   (contents/gitignore-file-contents)))

;; --- Run ---

(defn print-following-steps! []
  (->> [""
        (str "========================== CREATE REAGENT APP v" cra-version-number " ==========================")
        ""
        (str
         "Your app `" user-project-name "` was successfully created. Now perform these 4 steps:")
        ""
        (str
         "1. Change directory into project folder: `cd " user-project-name "`")
        (str
         "2. Install dependencies:                 `" pm " install`")
        (str
         "3. Run app:                              `" pm " start`")
        (str
         "4. Open your browser at:                 `localhost:3000`")
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
