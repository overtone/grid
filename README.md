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
    (led-set grid 1 2 1) ; red
    (led-set grid 1 2 2) ; green
    (led-set grid 1 2 3) ; yellow
    (led-set grid 1 2 0) ; off
    (on-action grid (fn [event x y] (led-set grid x y (if (= :press event) 1 0))) nil nil)
    (meta-on-action grid (fn [event key] (meta-led-set grid key (if (= :press event) 2 0))))

## Contributors

* Philip Potter
* Gary Trakhman