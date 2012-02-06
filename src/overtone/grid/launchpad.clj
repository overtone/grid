(ns overtone.grid.launchpad
  (:use [overtone grid midi]
        [clojure.set :only [map-invert]])
  (:import (javax.sound.midi ShortMessage)))

;;; Launchpad implementation of a Grid controller.
;;; currently only uses the 8x8 grid section, not the extra 16
;;; peripheral buttons.

;;; You may wish to consult the Launchpad Programmers Reference when
;;; reading this file:
;;; http://novationmusic.com/support/launchpad/

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

(defn coords->midi-note [x y]
  (+ x (* 16 y)))

(def midi-note->coords
  (into {} (for [x (range 8)
                 y (range 8)]
             [(+ x (* y 16)) [x y]])))

;;; messages to control double-buffering.
;;;
;;; these messages display one buffer, copy that buffer's contents to
;;; the other buffer, and set the other buffer to update in the background.
(def display-buffer-0 (make-ShortMessage :control-change 0 (+ 32 16 4)))
(def display-buffer-1 (make-ShortMessage :control-change 0 (+ 32 16 1)))


;;; Summary of the colour data format sent to the launchpad:
;;;
;;; It's easiest to split the number into two-bit "crumbs":
;;;
;;; 4r300 <- LED green, single buffer
;;; 4r030 <- LED off, copy to both buffers
;;; 4r003 <- LED red, single buffer
;;;
;;; The first crumb is the green value; the last crumb is the red
;;; value. They separately controls a green and a red LED
;;; respectively. Setting a value of 1 or 2 rather than 3 dims the
;;; LED. You can get other colours by mixing red and green; eg the
;;; launchpad programmer's reference suggests 4r333 (63) for amber and
;;; 4r332 (62) for yellow.
;;;
;;; The middle crumb contains two control bits with regard to double
;;; buffering. A value of 3 copies the sent colour to both buffers,
;;; while a value of 0 only sends it to the currently updating buffer
;;; (which is not the currently displayed one). Generally you want it
;;; to be 3 for setting a single LED at a time, and 0 while updating
;;; the whole field in double-buffering mode. For other values, see
;;; the programmer's reference.

(def colours
  {:red    4r003
   :green  4r300
   :yellow 4r302
   :off    4r000})

(defn both-buffers [colour]
  (bit-or colour 4r030))

(defn midi-handler [f]
  (fn [event ts]
    (when (= (:cmd event) (cmd->java-cmd :note-on))
      (when (contains? midi-note->coords (:note event))
        (let [note  (:note event)
              [x y] (midi-note->coords note)]
          (if (zero? (:vel event))
            (f :release x y)
            (f :press   x y)))))))

(defrecord Launchpad [launchpad-in launchpad-out palette]
  Grid
  (on-action [this f group name]   ; currently ignoring group and name
    (midi-handle-events launchpad-in (midi-handler f)))
  (set-all-leds [this colour]
    (led-frame this (repeat 8 (repeat 8 colour))))
  (led-set [this x y colour]
    (midi-note-on launchpad-out (coords->midi-note x y) (both-buffers (colours (palette colour)))))
  (led-frame [this leds]
    (midi-send launchpad-out display-buffer-0)
    (let [coords (for [y (range 8)
                       x (range 8)]
                   [x y])]
      (doseq [[coord-1 coord-2] (partition 2 coords)]
        (let [colour-1 (colours (palette (get leds coord-1 0)))
              colour-2 (colours (palette (get leds coord-2 0)))]
          (midi-send launchpad-out (make-ShortMessage 2 :note-on colour-1 colour-2)))))
    (midi-send launchpad-out display-buffer-1)) )

(defmethod print-method Launchpad [lp w]
  (.write w (format "#<Launchpad palette%s>" (:palette lp))))

(def default-palette
  [:off :red :green :yellow])

(defn make-launchpad
  "Creates an 8x8 Grid implementation backed by a launchpad."
  ([] (make-launchpad default-palette))
  ([palette]
     (if-let [launchpad-in (midi-in "Launchpad")]
       (if-let [launchpad-out (midi-out "Launchpad")]
         (Launchpad. launchpad-in launchpad-out palette)
         (throw (Exception. "Found launchpad for input but couldn't find it for output")))
       (throw (Exception. "Couldn't find launchpad")))))
