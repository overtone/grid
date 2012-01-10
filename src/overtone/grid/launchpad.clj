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

(defn midi-note->coords [note]
  (let [y   (quot note 16)
        x   (rem note 16)]
    [x y]))

(defn coords->midi-note [x y]
  (+ x (* 16 y)))

;;; messages to control double-buffering.
;;;
;;; these messages display one buffer, copy that buffer's contents to
;;; the other buffer, and set the other buffer to update in the background.
(def display-buffer-0 (make-ShortMessage :control-change 0 (+ 32 16 4)))
(def display-buffer-1 (make-ShortMessage :control-change 0 (+ 32 16 1)))

(def colours
  {:red    {:both-buffers 0x0f
            :one-buffer   0x03}
   :green  {:both-buffers 0x3c
            :one-buffer   0x30}
   :yellow {:both-buffers 0x3e
            :one-buffer   0x32}
   :off    {:both-buffers 0x0c
            :one-buffer   0x00}
   })

(defn make-launchpad
  "Creates a Grid implementation backed by a launchpad. If colour is
specified, LEDs will be lit in the given colour; the default is red."
  ([]
     (make-launchpad :red))
  ([colour]
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
             ;; there is a "reset launchpad" cmd:
             ;; (make-ShortMessage :control-change 0 0)
             ;; this will turn all LEDs off but it will also wipe any
             ;; other state, such as the duty cycle. So let's not do that.
             (led-frame this (repeat 8 (repeat 8 :off))))
           (illuminate-all-leds [this]
             ;; similarly, there is a "light all LEDs" cmd:
             ;; (make-ShortMessage :control-change 0 0x7f)
             ;; but again, this wipes all other state.
             (led-frame this (repeat 8 (repeat 8 :on))))
           (led-on [this x y]
             (midi-note-on launchpad-out (coords->midi-note x y) (get-in colours [colour :both-buffers])))
           (led-off [this x y]
             (midi-note-on launchpad-out (coords->midi-note x y) (get-in colours [:off :both-buffers])))
           (led-frame [this rows]
             (midi-send launchpad-out display-buffer-0)
             (doseq [[a b] (partition 2 (apply concat rows))]
               (let [on-val  (get-in colours [colour :one-buffer])
                     off-val (get-in colours [:off   :one-buffer])
                     a     (if (= :on a) on-val off-val)
                     b     (if (= :on b) on-val off-val)]
                 (midi-send launchpad-out (make-ShortMessage 2 :note-on a b))))
             (midi-send launchpad-out display-buffer-1)))
         (throw (Exception. "Found launchpad for input but couldn't find it for output")))
       (throw (Exception. "Couldn't find launchpad")))))
