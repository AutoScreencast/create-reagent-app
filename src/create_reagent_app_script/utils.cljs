(ns create-reagent-app-script.utils
  (:require [clojure.string :as str]
            [fs :refer [existsSync mkdirSync appendFileSync copyFileSync]]))

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
