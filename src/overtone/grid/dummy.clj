(ns overtone.grid.dummy
  (:use [overtone.grid]))

(defn make-dummy
  ([]
     (make-dummy 8 8))
  ([x y]
     (reify Grid
       (on-action [this f group name]
         nil)
       (dimensions [this]
         [x y])
       (clear-all-leds [this]
         nil)
       (illuminate-all-leds [this]
         nil)
       (led-on [this x y]
         nil)
       (led-off [this x y]
         nil)
       (led-frame [this idx & rows]
         nil)
       (is-connected? [this]
         true)
       (disconnect [this]
         nil))))
