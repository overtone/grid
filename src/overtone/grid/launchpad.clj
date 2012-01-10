(ns overtone.grid.launchpad
  (:use [overtone grid midi]
        [clojure.set :only [map-invert]])
  (:import (javax.sound.midi ShortMessage)))

;;; -- this section to be pushed upstream to overtone.midi

(def cmd->java-cmd (map-invert midi-shortmessage-command))

(defn make-ShortMessage
  ([cmd byte1 byte2]
     {:pre [(contains? cmd->java-cmd cmd)]}
     (doto (ShortMessage.)
       (.setMessage (cmd->java-cmd cmd) byte1 byte2)))
  ([channel cmd byte1 byte2]
     {:pre [(contains? cmd->java-cmd cmd)]}
     (doto (ShortMessage.)
       (.setMessage (cmd->java-cmd cmd) channel byte1 byte2))))

(defn midi-send [sink msg]
  (.send (:receiver sink) msg -1))

;;; -- end section to be pushed upstream

(def RED 15)
(def OFF 12)

(defn midi-note->coords [note]
  (let [y   (quot note 16)
        x   (rem note 16)]
    [x y]))

(defn coords->midi-note [x y]
  (+ x (* 16 y)))

(defn make-launchpad []
  (if-let [launchpad-in (midi-in "Launchpad")]
    (if-let [launchpad-out (midi-out "Launchpad")]
      (reify Grid
        (on-action [this f group name] ; currently ignoring group and name
          (midi-handle-events launchpad-in
                              (fn [event ts]
                                (let [note  (:note event)
                                      [x y] (midi-note->coords note)]
                                  (if (zero? (:vel event))
                                    (f :release x y)
                                    (f :press   x y))))))
        (dimensions [this]
          [8 8])
        (clear-all-leds [this]
          ;; FIXME: use burst mode and possibly double buffering
          (for [x (range 8)
                y (range 8)]
            (led-off this x y)))
        (illuminate-all-leds [this]
          ;; FIXME: use burst mode and possibly double buffering
          (for [x (range 8)
                y (range 8)]
            (led-on this x y)))
        (led-on [this x y]
          (midi-note-on launchpad-out (coords->midi-note x y) RED))
        (led-off [this x y]
          (midi-note-on launchpad-out (coords->midi-note x y) OFF))
        (led-frame [this rows]
          ;; send a dummy message to ensure we start from the origin.
          ;; this message sets the button layout to the default X-Y
          ;; layout rather than drum layout. I don't expect we'll ever
          ;; want the drum layout; such a transformation could be done
          ;; at a higher level
          (midi-send launchpad-out (make-ShortMessage :control-change 0 1))
          (doseq [[a b] (partition 2 (apply concat rows))]
            (let [a     (if (= :on a) RED OFF)
                  b     (if (= :on b) RED OFF)]
              (midi-send launchpad-out (make-ShortMessage 2 :note-on a b))))))
      (throw (Exception. "Found launchpad for input but couldn't find it for output")))
    (throw (Exception. "Couldn't find launchpad"))))
