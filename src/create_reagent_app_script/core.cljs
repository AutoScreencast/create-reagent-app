(ns create-reagent-app-script.core
  (:require [clojure.string :as str]
            [clojure.tools.cli :refer [parse-opts]]
            [cljs-node-io.core :refer [slurp]]
            [path]
            [create-reagent-app-script.utils :refer [maybe-create-folder!
                                                     replace-hyphens-with-underscores
                                                     append-contents-to-file!
                                                     copy-file]]
            [create-reagent-app-script.contents :as contents]))

(def cli-options [; ["-c" "--css CSS-LIBRARY" "CSS library, for example: tailwindcss"
                  ;  :default "basic"
                  ;  :parse-fn #(str %)]
                  ["-p" "--package-manager PACKAGE-MANAGER" "Package manager, for example: yarn. Defaults to: npm."
                   :default "npm"
                   :parse-fn #(str %)]
                  ["-x" "--example" "Version of template with example code and comments"]
                  ["-h" "--help" "Usage help"]])

(def argv-map (parse-opts (js->clj js/process.argv) cli-options))

; (println "argv-map" argv-map)

;; Get imput command line arguments from Node process
(def user-specified-args (subvec (:arguments argv-map) 2))

;; eg. "my-project"
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
        "  `npx create-reagent-app my-project`"
        ""
        "================================================================================"
        ""]
       (str/join \newline)
       (print))
  (js/process.exit 1))

;; --- Linear sequence of steps ---

;; Output folders

(def dest_upn_dir (path/resolve user-project-name))

(def u_p_n (replace-hyphens-with-underscores user-project-name))

;; Create project

(defn create-project! [template]
  (let [template_dir (case template
                       "basic-example" (path/join orig_base_dir "templates" "basic-example")
                       "basic"         (path/join orig_base_dir "templates" "basic"))]
  ; Create folders
    (maybe-create-folder! dest_upn_dir)
    (maybe-create-folder! (path/join dest_upn_dir "public"))
    (maybe-create-folder! (path/join dest_upn_dir "public" "css"))
    (when (= template "basic-example")
      (maybe-create-folder! (path/join dest_upn_dir "public" "img")))

    (maybe-create-folder! (path/join dest_upn_dir "src"))
    (maybe-create-folder! (path/join dest_upn_dir "src" "main"))
    (maybe-create-folder! (path/join dest_upn_dir "src" "main" u_p_n))
    (maybe-create-folder! (path/join dest_upn_dir "src" "main" u_p_n "app"))
    (when (= template "basic-example")
      (maybe-create-folder! (path/join dest_upn_dir "src" "main" u_p_n "app" "views")))

    (maybe-create-folder! (path/join dest_upn_dir "src" "test"))
    (maybe-create-folder! (path/join dest_upn_dir "src" "test" u_p_n))
    (maybe-create-folder! (path/join dest_upn_dir "src" "test" u_p_n "app"))
    (when (= template "basic-example")
      (maybe-create-folder! (path/join dest_upn_dir "src" "test" u_p_n "app" "views")))

  ;; Create or copy files with content
    (append-contents-to-file! (path/join dest_upn_dir "shadow-cljs.edn")
                              (str/replace (slurp (path/join template_dir "shadow-cljs.edn")) "*|USER-PROJECT-NAME|*" user-project-name))

    (append-contents-to-file! (path/join dest_upn_dir "package.json")
                              (str/replace (slurp (path/join template_dir "package.json")) "*|USER-PROJECT-NAME|*" user-project-name))

    (append-contents-to-file! (path/join dest_upn_dir "public" "index.html")
                              (slurp (path/join template_dir "public" "index.html")))

    (when (= template "basic-example")
      (copy-file (path/join template_dir "public" "img" "cljs.svg")
                 (path/join dest_upn_dir "public" "img" "cljs.svg")))

    (copy-file (path/join template_dir "public" "favicon.ico")
               (path/join dest_upn_dir "public" "favicon.ico"))

    (copy-file (path/join template_dir "public" "cljs_logo_192.png")
               (path/join dest_upn_dir "public" "cljs_logo_192.png"))

    (copy-file (path/join template_dir "public" "cljs_logo_512.png")
               (path/join dest_upn_dir "public" "cljs_logo_512.png"))

    (copy-file (path/join template_dir "public" "manifest.json")
               (path/join dest_upn_dir "public" "manifest.json"))

    (copy-file (path/join template_dir "public" "robots.txt")
               (path/join dest_upn_dir "public" "robots.txt"))

    (append-contents-to-file! (path/join dest_upn_dir        "public" "css" "style.css")
                              (slurp (path/join template_dir "public" "css" "style.css")))

    (append-contents-to-file! (path/join dest_upn_dir                     "src" "main" u_p_n        "app" "core.cljs")
                              (str/replace (slurp (path/join template_dir "src" "main" "my_project" "app" "core.cljs"))
                                           "*|USER-PROJECT-NAME|*" user-project-name))

    (when (= template "basic-example")
      (append-contents-to-file! (path/join dest_upn_dir                     "src" "main" u_p_n        "app" "views" "aside.cljs")
                                (str/replace (slurp (path/join template_dir "src" "main" "my_project" "app" "views" "aside.cljs"))
                                             "*|USER-PROJECT-NAME|*" user-project-name))

      (append-contents-to-file! (path/join dest_upn_dir                     "src" "main" u_p_n        "app" "views" "counter.cljs")
                                (str/replace (slurp (path/join template_dir "src" "main" "my_project" "app" "views" "counter.cljs"))
                                             "*|USER-PROJECT-NAME|*" user-project-name))

      (append-contents-to-file! (path/join dest_upn_dir                     "src" "main" u_p_n        "app" "views" "description.cljs")
                                (str/replace (slurp (path/join template_dir "src" "main" "my_project" "app" "views" "description.cljs"))
                                             "*|USER-PROJECT-NAME|*" user-project-name))

      (append-contents-to-file! (path/join dest_upn_dir                     "src" "main" u_p_n        "app" "views" "header.cljs")
                                (str/replace (slurp (path/join template_dir "src" "main" "my_project" "app" "views" "header.cljs"))
                                             "*|USER-PROJECT-NAME|*" user-project-name)))

    (append-contents-to-file! (path/join dest_upn_dir                     "src" "test" u_p_n        "app" "core_test.cljs")
                              (str/replace (slurp (path/join template_dir "src" "test" "my_project" "app" "core_test.cljs"))
                                           "*|USER-PROJECT-NAME|*" user-project-name))

    (when (= template "basic-example")
      (append-contents-to-file! (path/join dest_upn_dir                     "src" "test" u_p_n        "app" "views" "aside_test.cljs")
                                (str/replace (slurp (path/join template_dir "src" "test" "my_project" "app" "views" "aside_test.cljs"))
                                             "*|USER-PROJECT-NAME|*" user-project-name))

      (append-contents-to-file! (path/join dest_upn_dir                     "src" "test" u_p_n        "app" "views" "counter_test.cljs")
                                (str/replace (slurp (path/join template_dir "src" "test" "my_project" "app" "views" "counter_test.cljs"))
                                             "*|USER-PROJECT-NAME|*" user-project-name))

      (append-contents-to-file! (path/join dest_upn_dir                     "src" "test" u_p_n        "app" "views" "description_test.cljs")
                                (str/replace (slurp (path/join template_dir "src" "test" "my_project" "app" "views" "description_test.cljs"))
                                             "*|USER-PROJECT-NAME|*" user-project-name))

      (append-contents-to-file! (path/join dest_upn_dir                     "src" "test" u_p_n        "app" "views" "header_test.cljs")
                                (str/replace (slurp (path/join template_dir "src" "test" "my_project" "app" "views" "header_test.cljs"))
                                             "*|USER-PROJECT-NAME|*" user-project-name)))

    ;; Create `.gitignore` and `README.md` files and their contents without slurping, since they are excluded during `npm publish`
    (append-contents-to-file! (path/join dest_upn_dir ".gitignore")
                              (contents/gitignore-file-contents))

    (append-contents-to-file! (path/join dest_upn_dir "README.md")
                              (contents/readme-file-contents))))


;; --- Run ---

(defn print-following-steps! [cra-version-number user-project-name package-manager]
  (->> [""
        (str "========================== CREATE REAGENT APP v" cra-version-number " ==========================")
        ""
        (str
         "Your app `" user-project-name "` was successfully created. Now perform these 4 steps:")
        ""
        (str
         "1. Change directory into project folder: `cd " user-project-name "`")
        (str
         "2. Install dependencies:                 `" package-manager " install`")
        (str
         "3. Run app:                              `" package-manager " start`")
        (str
         "4. Open your browser at:                 `localhost:3000`")
        ""
        "================================================================================"
        ""]
       (str/join \newline)
       (println)))

(defn ^:export main []
  (if (:example (:options argv-map))
    (create-project! "basic-example")
    (create-project! "basic"))
  (print-following-steps! cra-version-number user-project-name pm))

(defn ^:dev/after-load reload! []
  (println "reload!"))
