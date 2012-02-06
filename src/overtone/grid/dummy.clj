(ns overtone.grid.dummy
  (:use [overtone.grid]))

(defrecord DummyGrid []
  Grid
  ;; might be nice to be able to manually trigger button events
  ;; for testing purposes rather than just ignoring on-action
  (on-action [this f group name]
    nil)
  (set-all-leds [this colour]
    nil)
  (led-set [this x y colour]
    nil)
  (led-frame [this rows]
    nil))

(defn make-dummy []
  (DummyGrid.))
