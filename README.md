    +---+---+---+---+---+---+---+---+
    |   |   |   |   |   |   |   |   |
    +---+---+---+---+---+---+---+---+
    |   |   |   |   |   |   |   |   |
    +---+---+---+---+---+---+---+---+
    |   |   | G |   |   |   |   |   |
    +---+---+---+---+---+---+---+---+
    |   |   |   | R |   |   |   |   |
    +---+---+---+---+---+---+---+---+
    |   |   |   |   | I |   |   |   |
    +---+---+---+---+---+---+---+---+
    |   |   |   |   |   | D |   |   |
    +---+---+---+---+---+---+---+---+
    |   |   |   |   |   |   |   |   |
    +---+---+---+---+---+---+---+---+
    |   |   |   |   |   |   |   |   |
    +---+---+---+---+---+---+---+---+


An abstraction for button-grid based controllers such as the monome.

## Optional dependencies

* [monome-serial](http://github/samaaron/monome-serial) for monome support
* [midi-clj](https://github.com/overtone/midi-clj) for launchpad support

## Usage

    (def grid (make-launchpad))
    (led-on grid 1 2)
    (on-press grid (fn [x y s] (my-instrument x y)))

## Contributors

* Philip Potter
* Gary Trakhman