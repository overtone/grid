(ns overtone.grid)

(defprotocol Grid
  "A generic representation of a grid of buttons which are also lights."
  (on-action [this f group name]
    "Registers a callback fn, to be called whenever one of the buttons is pressed or released.

     The fn should take three args: [type x y], where type is :press or :release, and x and y are the coords of the button.

     The group and name are currently only used by monome, but will be extended to launchpad soon.")
  (dimensions [this]
    "Returns the dimensions of this grid as a vector: [cols rows]")
  (led-set [this x y colour]
    "Set the LED at position [x y] to the given colour. Currently colour
    is an index value into a palette, but this may change.")
  (set-all-leds [this colour]
    "Sets all LEDs to a single colour.")
  (led-frame [this leds]
    "Update the entire field of LEDs. The rows arg is a map of coords to colour values, the same as in led-set.

     eg, if you have a grid 2 columns by 2 rows, you could update all of the LEDs like this:
       (led-frame grid {[0 0] 1 [1 0] 0
                        [0 1] 0 [1 1] 1})

     Any unspecified coords will default to 0."))

