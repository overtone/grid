(ns overtone.device.grid)

(defprotocol Grid
  "A generic representation of a grid of buttons which are also
  lights such as a monome or launchpad."
  (width [this]
    "Returns the number of elements of the grid width-wise. This value
    is not expected to change.")
  (height [this]
    "Returns the number of elements of the grid height-wise. This
    value is not expected to change.")
  (on-action [this key f]
    "Registers a callback fn, with the unique key to be called
     whenever one of the buttons is pressed or released. If a handler
     has already been registered with the specified key, replace the
     old handler with this new one.

     The handler fn should take three args: [action x y], where action
     is a keyword from the set #{:pressed :released} and x and y are
     the coords of the button being pressed.")
  (remove-handler [this key]
    "Removes the action handler associated with the specified
    key. Returns true if removal was successful, nil otherwise.")
  (remove-all-handlers [this]
    "Removes all action handlers asscociated with all keys. Returns
    the number of handlers removed, nil if no handlers were removed.")
  (handler-keys [this]
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
