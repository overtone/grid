(ns overtone.grid)

(defprotocol Grid
  (on-action [this f group name])
  (dimensions [this])
  (clear-all-leds [this])
  (illuminate-all-leds [this])
  (led-on [this x y])
  (led-off [this x y])
  (led-frame [this rows])
  (is-connected? [this])
  (disconnect [this]))

