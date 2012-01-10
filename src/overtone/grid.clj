(ns overtone.grid)

(defprotocol Grid
  "A generic representation of a grid of buttons which are also lights."
  (on-action [this f group name]
    "Registers a callback fn, to be called whenever one of the buttons is pressed or released.

     The fn should take three args: [type x y], where type is :press or :release, and x and y are the coords of the button.

     The group and name are currently only used by monome, but will be extended to launchpad soon.")
  (dimensions [this]
    "Returns the dimensions of this grid as a vector: [cols rows]")
  (clear-all-leds [this]
    "Turn all LEDs off.")
  (illuminate-all-leds [this]
    "Light up all LEDs.")
  (led-on [this x y]
    "Light up the LED at position [x y].")
  (led-off [this x y]
    "Turn off the LED at position [x y].")
  (led-frame [this rows]
    "Update the entire field of LEDs. The rows arg is a seq of seqs of either :off or :on values.

     eg, if you have a grid 4 columns by 2 rows, you could update all of the LEDs like this:
       (led-frame grid [[:on  :on  :off :off]
                        [:off :off :on  :on ]])")
  (is-connected? [this]
    "Returns true if this grid is currently connected to an underlying hardware device.")
  (disconnect [this]
    "Disconnects this grid from its hardware device."))

