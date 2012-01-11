(ns overtone.grid.test.launchpad
  (:use [overtone.grid.launchpad] :reload)
  (:use [midje.sweet])
  (:import (javax.sound.midi ShortMessage)))

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

(defn two-byte-message [expected]
  {:pre [(isa? (class expected) ShortMessage)]}
   (chatty-checker [actual]
       (and (isa? (class actual) ShortMessage)
            (= (.getCommand actual) (.getCommand expected))
            (= (.getChannel actual) (.getChannel expected))
            (= (.getData1 actual) (.getData1 expected))
            (= (.getData2 actual) (.getData2 expected)))))

(fact "make-ShortMessage can create valid note-on messages, and defaults to channel 0"
  (make-ShortMessage :note-on 4 6) => (two-byte-message (doto (ShortMessage.) (.setMessage 0x90 0 4 6))))

(fact "make-ShortMessage can create valid note-on messages on different channels"
  (make-ShortMessage 2 :note-on 4 6) => (two-byte-message (doto (ShortMessage.) (.setMessage 0x90 2 4 6))))

(let [handler (midi-handler (fn [event x y] [event x y]))]
  (fact "Midi handler forwards regular note-on messages and translates coordinates"
    (handler {:cmd ShortMessage/NOTE_ON :vel 127 :note 0   } -1) => [:press   0 0]
    (handler {:cmd ShortMessage/NOTE_ON :vel 0   :note 0x30} -1) => [:release 0 3]
    (handler {:cmd ShortMessage/NOTE_ON :vel 127 :note 0x37} -1) => [:press 7 3]
    (handler {:cmd ShortMessage/NOTE_ON :vel 127 :note 0x77} -1) => [:press 7 7])

  (fact "Midi handler ignores note-on messages with notes it doesn't understand"
    (handler {:cmd ShortMessage/NOTE_ON :vel 127 :note 0x08} -1) => nil
    (handler {:cmd ShortMessage/NOTE_ON :vel 127 :note 0x19} -1) => nil
    (handler {:cmd ShortMessage/NOTE_ON :vel 127 :note 0xff} -1) => nil)

  (fact "Midi handler ignores control-change messages"
    (handler {:cmd ShortMessage/CONTROL_CHANGE :vel 127 :note 0x7f} -1) => nil
    (handler {:cmd ShortMessage/CONTROL_CHANGE :vel   0 :note 0x3f} -1) => nil)
  )