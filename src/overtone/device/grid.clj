(ns overtone.device.grid)

(defprotocol Grid
  "A generic representation of a grid of buttons which are also
  lights such as a monome or launchpad."
  (on-action [this key f]
    "Registers a callback fn, with the unique key to be called
     whenever one of the buttons is pressed or released.  The fn
     should take three args: [action x y], where action is a keyword
     from the set #{:pressed :released} and x and y are the coords of
     the button being pressed.")
  (remove-action-handler [this key]
    "Removes the action handler associated with the specified key.")
  (action-handlers [this]
    "Returns a seq of keys for each registered handler.")
  (led-set [this x y colour]
    "Set the LED at position [x y] to the given colour. Color 0 is
    off, > 1 represents a specific palette colour. Subject to
    change.")
  (led-set-all [this colour]
    "Sets all LEDs to a single colour or off if 0.")
  (led-frame [this leds]
    "Update the entire field of LEDs. The rows arg is a map of coords
    to colour values, the same as in led-set.

     eg, if you have a grid 2 columns by 2 rows, you could update all
     of the LEDs like this:
       (led-frame grid {[0 0] 1 [1 0] 0
                        [0 1] 0 [1 1] 1})

     Any unspecified coords should default to 0."))
