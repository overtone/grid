(ns overtone.grid.test.launchpad
  (:use [overtone.grid.launchpad] :reload)
  (:use [midje.sweet]))

(fact "MIDI note 0 corresponds to coordinate [0 0], top left"
  (midi-note->coords 0) => [0 0])

(fact "MIDI note 119 corresponds to coordinate [7 7], bottom right"
  (midi-note->coords 119) => [7 7])

(facts "The x coordinate is given by the midi note mod 16"
  (fact (first (midi-note->coords 1)) => 1)
  (fact (first (midi-note->coords 2)) => 2)
  (fact (first (midi-note->coords 7)) => 7)
  (fact (first (midi-note->coords 16)) => 0)
  (fact (first (midi-note->coords 23)) => 7)
  (fact (first (midi-note->coords 35)) => 3))

(facts "The y coordinate is given by the midi note divided by 16 (ignoring remainder)"
  (fact (second (midi-note->coords 0)) => 0)
  (fact (second (midi-note->coords 7)) => 0)
  (fact (second (midi-note->coords 16)) => 1)
  (fact (second (midi-note->coords 23)) => 1)
  (fact (second (midi-note->coords 32)) => 2)
  (fact (second (midi-note->coords 112)) => 7))

(fact "Coord [0 0] corresponds to midi note 0"
  (coords->midi-note 0 0) => 0)

(fact "Coord [7 0] corresponds to midi note 7"
  (coords->midi-note 7 0) => 7)

(facts "Coord [x y] corresponds to midi note (+ (* 16 y) x)"
  (fact (coords->midi-note 1 4) => 65)
  (fact (coords->midi-note 5 2) => 37))