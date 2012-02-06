(ns overtone.grid.monome
  (:require  [monome-serial.core :as monome-core]
             [monome-serial.led :as monome]
             [monome-serial.event-handlers :as handlers])
  (:use [overtone.grid]))

(extend-type Monome
  Grid
  (on-action [this f group name]
    (handlers/on-action monome f group name))
  (set-all-leds [this colour]
    (if (zero? colour)
      (monome/clear monome)
      (monome/all monome)))
  (led-on [this x y colour]
    (if (zero? colour)
      (monome/led-off monome x y)
      (monome/led-on monome x y)))
  (led-frame [this rows]
    ;; FIXME need to translate a large grid into multiple 8x8
    ;; grids and specify idx values
    ;; and also translate :on and :off values into
    ;; monome-serial friendly rows
    #_(apply monome/frame monome rows)
    nil))

