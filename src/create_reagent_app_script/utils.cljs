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

(defn capitalize-words-and-remove-hyphens
  "Capitalize every word in a string, and convert hyphens to space"
  [s]
  (let [capitalized-words (->> (str/split (str s) #"\b")
                               (map str/capitalize)
                               str/join)]
    (str/replace capitalized-words "-" " ")))

(defn name-starts-with-number?
  "Check to see if the first character of a string is a number"
  [s]
  (->> s
       str
       first
       js/parseInt
       js/Number.isNaN ; if false, then the string starts with a number
       not))