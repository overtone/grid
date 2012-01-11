(ns overtone.grid.monome
  (:require  [monome-serial.core :as monome-core]
             [monome-serial.led :as monome]
             [monome-serial.event-handlers :as handlers])
  (:use [overtone.grid]))

;;; Might it be possible or desirable to transition to
;;; (extend-type Monome
;;;    Grid
;;;    ...
;;;   )
;;; and get rid of this intermediate MonomeGrid record? Currently the
;;; only blocker is that Monome doesn't know its own size and so can't
;;; implement (dimensions monome).

(defrecord MonomeGrid [monome n-cols n-rows]
  Grid
  (on-action [this f group name]
    (handlers/on-action monome f group name))
  (dimensions [this]
    [n-cols n-rows])
  (set-all-leds [this colour]
    (if (zero? colour)
      (monome/clear monome)
      (monome/all monome)))
  (led-on [this x y colour]
    (if (zero? colour)
      (monome/led-off monome x y)
      (monome/led-on monome x y)))
  (led-frame [this rows]
    ;; FIXME need to translate a large grid into multiple 8x8
    ;; grids and specify idx values
    ;; and also translate :on and :off values into
    ;; monome-serial friendly rows
    #_(apply monome/frame monome rows)
    nil))

(def MONOME-KINDS
  {
   :64       [8   8]
   :128      [16  8]
   :256      [16 16]
   })

(defn- detect-kind
  [path]
  (condp re-find path
    #"-m64-"      :64
    #"-m128-"     :128
    #"-m256-"     :256
    :unknown))

(defn- monome-info
  [kind]
  (let [info (get MONOME-KINDS kind)]
    (when-not info
      (throw (Exception. (str "Unknown monome kind " [kind] ". Expected one of " (keys MONOME-KINDS)))))
    info))

(defn make-monome
  "Initialise a monome. When passed only a path, will attempt to infer
  the dimensions of the monome from the pathname.

  It is also possible to explicitly specify the number of cols and rows.

  Raises an exception if the supplied path isn't valid or is already in use"
  ([path]
     (let [kind (detect-kind path)
           [n-cols n-rows] (monome-info kind)]
       (make-monome path n-cols n-rows)))
  ([path n-cols n-rows]
     (let [monome (monome-core/connect path)]
       (MomoneGrid. monome n-cols n-rows))))
