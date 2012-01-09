(ns overtone.grid.test.dummy
  (:require [overtone.grid :as grid])
  (:use [overtone.grid.dummy] :reload)
  (:use [midje.sweet]))

(fact "A dummy grid is 8x8 by default"
  (let [grid (make-dummy)]
    (grid/dimensions grid) => [8 8]))

(fact "A dummy grid can have different dimensions specified"
  (let [grid (make-dummy 16 32)]
    (grid/dimensions grid) => [16 32]))