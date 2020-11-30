(ns *|USER-PROJECT-NAME|*.test.core-test
  (:require [cljs.test :refer [deftest is testing]]
            [reagent.core :as r]
            [*|USER-PROJECT-NAME|*.app.core :refer [incrementer! decrementer!]]))

;; Basic unit tests of `incrementer!` and `decrementer!` utility functions

(deftest increment-atom
  (testing "increases the atom value by one"
    (let [r (r/atom 0)
          _ (incrementer! r)]
      (is (= 1 @r)))))

(deftest decrement-atom
  (testing "decreases the atom value by one"
    (let [r (r/atom 0)
          _ (decrementer! r)]
      (is (= -1 @r)))))
