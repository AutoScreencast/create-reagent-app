(ns create-reagent-app-script.core
  (:require [clojure.string :as str]
            [fs :refer [existsSync mkdirSync appendFileSync]]
            [create-reagent-app-script.contents :as contents]))

(def arguments (js->clj js/process.argv))

;; `npx create-react-app my-app --re-frame` will give us
;; options of [my-app --re-frame]
(def options (subvec arguments 2))

(def user-project-name (str (first options)))

(defn maybe-create-folder! [folder-name]
  (if (not (existsSync folder-name))
    (mkdirSync folder-name)
    (js/console.error (str "The folder " folder-name " already exists."))))

(defn replace-hyphens-with-underscores [folder-name]
  (str/replace folder-name \- \_))

(defn append-contents-to-file! [filepath contents]
  (appendFileSync filepath contents))

;; --- Linear sequence of steps ---

;; Create `my-app` folder.
(maybe-create-folder! user-project-name)

;; Create `shadow-cljs.edn` file and its contents
(append-contents-to-file!
 (str "./" user-project-name "/shadow-cljs.edn")
 (contents/shadow-cljs-edn-file-contents user-project-name))

;; Create `package.json` file and its contents
(append-contents-to-file!
 (str "./" user-project-name "/package.json")
 (contents/package-json-file-contents user-project-name))

;; Create `.gitignore` file and its contents
(append-contents-to-file!
 (str "./" user-project-name "/.gitignore")
 (contents/gitignore-file-contents))

;; Create `public` folder
(maybe-create-folder! (str user-project-name "/public"))

;; Create `index.html` file
(append-contents-to-file!
 (str "./" user-project-name "/public/index.html")
 (contents/index-html-contents))

;; Create `css` folder under `public`
(maybe-create-folder! (str user-project-name "/public/css"))

;; Create `style.css` file
(append-contents-to-file!
 (str "./" user-project-name "/public/css/style.css")
 (contents/style-css-contents))

;; Create `src` folder
(maybe-create-folder! (str user-project-name "/src"))

;; Create `user-project-name` folder, with underscores instead of hyphens, if any
(maybe-create-folder! (str user-project-name
                           "/src/"
                           (replace-hyphens-with-underscores user-project-name)))

;; Create `app` folder
(maybe-create-folder! (str user-project-name
                           "/src/"
                           (replace-hyphens-with-underscores user-project-name)
                           "/app"))

;; Create `core.cljs` file
(append-contents-to-file!
 (str "./" user-project-name
      "/src/"
      (replace-hyphens-with-underscores user-project-name)
      "/app/core.cljs")
 (contents/core-cljs-contents user-project-name))

;; --- Run ---

(defn output! []
  (println "====================================== CREATE REAGENT APP ======================================")
  (println (str "Your app `" user-project-name "` was successfully created. Please do the following 4 steps:"))
  (println (str "1. Please change into the project folder: `cd " user-project-name "`"))
  (println "2. Then, install the package dependencies using npm or yarn: `npm install` or `yarn install`")
  (println "3. Run your app with `npm start` or `yarn start`")
  (println "4. Open your app in the browser at `localhost:3000`")
  (println "================================================================================================"))

(defn ^:export main []
  (output!))

(defn ^:dev/after-load reload! []
  (println "reload!"))
