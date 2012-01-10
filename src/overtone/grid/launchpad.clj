(ns overtone.grid.launchpad
  (:use [overtone grid midi]))

(def RED 15)

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
          (midi-note-off launchpad-out (coords->midi-note x y)))
        (led-frame [this rows]
          ;; FIXME: use burst mode and possibly double buffering
          (doseq [[y row]  (map vector (iterate inc 0) rows)
                  [x cell] (map vector (iterate inc 0) row)]
            (case cell
              :on  (led-on  this x y)
              :off (led-off this x y)))))
      (throw (Exception. "Found launchpad for input but couldn't find it for output")))
    (throw (Exception. "Couldn't find launchpad"))))
