(ns overtone.grid.dummy
  (:use [overtone.grid]))

(defn make-dummy
  ([]
     (make-dummy 8 8))
  ([x y]
     (reify Grid
       ;; might be nice to be able to manually trigger button events
       ;; for testing purposes rather than just ignoring on-action
       (on-action [this f group name]
         nil)
       (dimensions [this]
         [x y])
       (set-all-leds [this colour]
         nil)
       (led-set [this x y colour]
         nil)
       (led-frame [this rows]
         nil)
       (is-connected? [this]
         true)
       (disconnect [this]
         nil))))
