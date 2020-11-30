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
;; We want the subpath: "/Users/username/.npm/_npx/ddddd..dd/node_modules/create-reagent-app"
(def orig_base_dir (path/join js/__dirname ".."))

;; Get version number
(def cra-version-number
  (-> orig_base_dir
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

(def dest_upn_dir (path/resolve user-project-name))

(def u_p_n (replace-hyphens-with-underscores user-project-name))

;; Create folders

(defn create-folders! []
  (maybe-create-folder! dest_upn_dir)
  (maybe-create-folder! (path/join dest_upn_dir "public"))
  (maybe-create-folder! (path/join dest_upn_dir "public" "css"))
  (maybe-create-folder! (path/join dest_upn_dir "public" "img"))
  (maybe-create-folder! (path/join dest_upn_dir "src"))
  (maybe-create-folder! (path/join dest_upn_dir "src" u_p_n))
  (maybe-create-folder! (path/join dest_upn_dir "src" u_p_n "app"))
  (maybe-create-folder! (path/join dest_upn_dir "src" u_p_n "app" "views"))
  (maybe-create-folder! (path/join dest_upn_dir "src" u_p_n "test")))

;; Origin filepath

(def basic_template_dir  (path/join orig_base_dir "templates" "basic"))

;; Create or copy files with content

(defn create-files-with-content! []
  (append-contents-to-file! (path/join dest_upn_dir "shadow-cljs.edn")
                            (str/replace (slurp (path/join basic_template_dir "shadow-cljs.edn")) "*|USER-PROJECT-NAME|*" user-project-name))

  (append-contents-to-file! (path/join dest_upn_dir "package.json")
                            (str/replace (slurp (path/join basic_template_dir "package.json")) "*|USER-PROJECT-NAME|*" user-project-name))

  (append-contents-to-file! (path/join dest_upn_dir "public" "index.html")
                            (slurp (path/join basic_template_dir "public" "index.html")))

  (copy-file (path/join basic_template_dir "public" "img" "cljs.svg")
             (path/join dest_upn_dir       "public" "img" "cljs.svg"))

  (copy-file (path/join basic_template_dir "public" "favicon.ico")
             (path/join dest_upn_dir       "public" "favicon.ico"))

  (copy-file (path/join basic_template_dir "public" "cljs_logo_192.png")
             (path/join dest_upn_dir       "public" "cljs_logo_192.png"))

  (copy-file (path/join basic_template_dir "public" "cljs_logo_512.png")
             (path/join dest_upn_dir       "public" "cljs_logo_512.png"))

  (copy-file (path/join basic_template_dir "public" "manifest.json")
             (path/join dest_upn_dir       "public" "manifest.json"))

  (copy-file (path/join basic_template_dir "public" "robots.txt")
             (path/join dest_upn_dir       "public" "robots.txt"))

  (append-contents-to-file! (path/join dest_upn_dir              "public" "css" "style.css")
                            (slurp (path/join basic_template_dir "public" "css" "style.css")))

  (append-contents-to-file! (path/join dest_upn_dir                           "src" u_p_n    "app" "core.cljs")
                            (str/replace (slurp (path/join basic_template_dir "src" "my_app" "app" "core.cljs"))
                                         "*|USER-PROJECT-NAME|*" user-project-name))

  (append-contents-to-file! (path/join dest_upn_dir                           "src" u_p_n    "app" "views" "aside.cljs")
                            (str/replace (slurp (path/join basic_template_dir "src" "my_app" "app" "views" "aside.cljs"))
                                         "*|USER-PROJECT-NAME|*" user-project-name))

  (append-contents-to-file! (path/join dest_upn_dir                           "src" u_p_n    "app" "views" "counter.cljs")
                            (str/replace (slurp (path/join basic_template_dir "src" "my_app" "app" "views" "counter.cljs"))
                                         "*|USER-PROJECT-NAME|*" user-project-name))

  (append-contents-to-file! (path/join dest_upn_dir                           "src" u_p_n    "app" "views" "description.cljs")
                            (str/replace (slurp (path/join basic_template_dir "src" "my_app" "app" "views" "description.cljs"))
                                         "*|USER-PROJECT-NAME|*" user-project-name))

  (append-contents-to-file! (path/join dest_upn_dir                           "src" u_p_n    "app" "views" "header.cljs")
                            (str/replace (slurp (path/join basic_template_dir "src" "my_app" "app" "views" "header.cljs"))
                                         "*|USER-PROJECT-NAME|*" user-project-name))

  (append-contents-to-file! (path/join dest_upn_dir                           "src" u_p_n    "test" "core_test.cljs")
                            (str/replace (slurp (path/join basic_template_dir "src" "my_app" "test" "core_test.cljs"))
                                         "*|USER-PROJECT-NAME|*" user-project-name))

  ;; Create `.gitignore` file and its contents without slurping, since it is excluded during `npm publish`
  (append-contents-to-file! (path/join dest_upn_dir ".gitignore")
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
