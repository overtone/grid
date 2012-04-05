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

## Usage

    (def grid (make-launchpad)) ;; from overtone.device.launchpad
    (led-set grid 1 2 1) ; red
    (led-set grid 1 2 2) ; green
    (led-set grid 1 2 3) ; yellow
    (led-set grid 1 2 0) ; off
    (on-action grid (fn [event x y] (led-set grid x y (if (= :press event) 1 0))))

## Contributors

* Philip Potter
* Gary Trakhman
* Sam Aaron
* Fronx
