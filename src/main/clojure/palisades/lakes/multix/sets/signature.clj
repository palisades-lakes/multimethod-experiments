(set! *warn-on-reflection* true) 
(set! *unchecked-math* :warn-on-boxed)
;;----------------------------------------------------------------
(ns palisades.lakes.multix.sets.signature
  
  {:doc "Manual 'method' (function) lookup fromm signature."
   :author "palisades dot lakes at gmail dot com"
   :since "2017-06-09"
   :version "2017-10-11"}
  (:require [palisades.lakes.multimethods.core :as d])
  (:import [java.util Collections Set]
           [palisades.lakes.bench.java.sets
            DoubleInterval IntegerInterval]
           [palisades.lakes.multimethods.java Signature2]))
;;----------------------------------------------------------------
(defn- intersectsII? [^IntegerInterval s0 ^IntegerInterval s1] 
  (.intersects s0 s1))
(defn- intersectsID? [^IntegerInterval s0 ^DoubleInterval s1] 
  (.intersects s0 s1))
(defn- intersectsIS? [^IntegerInterval s0 ^Set s1] 
  (.intersects s0 s1))
;;----------------------------------------------------------------
(defn- intersectsDI? [^DoubleInterval s0 ^IntegerInterval s1] 
  (.intersects s1 s0))
(defn- intersectsDD? [^DoubleInterval s0 ^DoubleInterval s1] 
  (.intersects s0 s1))
(defn- intersectsDS? [^DoubleInterval s0 ^Set s1] 
  (.intersects s0 s1))
;;----------------------------------------------------------------
(defn- intersectsSI? [^Set s0 ^IntegerInterval s1] 
  (.intersects s1 s0))
(defn- intersectsSD? [^Set s0 ^DoubleInterval s1] 
  (.intersects s1 s0))
(defn- intersectsSS? [^Set s0 ^Set s1] 
  (not (Collections/disjoint s0 s1)))
;;----------------------------------------------------------------
(defn- cant-intersect-exception [s0 s1]
  (UnsupportedOperationException.
    (print-str 
      "can't intersect" (class s0) "and" (class s1))))
;;----------------------------------------------------------------
(let [II (d/to-signature IntegerInterval IntegerInterval)
      ID (d/to-signature IntegerInterval DoubleInterval)
      IS (d/to-signature IntegerInterval Set)
      DI (d/to-signature DoubleInterval IntegerInterval)
      DD (d/to-signature DoubleInterval DoubleInterval)
      DS (d/to-signature DoubleInterval Set)
      SI (d/to-signature Set IntegerInterval)
      SD (d/to-signature Set DoubleInterval)
      SS (d/to-signature Set Set)]
  (defn intersects? [s0 s1] 
    (let [^Signature2 s (d/signature s0 s1)]
      ;; very arbitrary assumptions about what s0 and s1 might
      ;; be and how they are related to the 'method' definitions
      (cond 
        (.equals II s) (intersectsII? s0 s1)
        (.equals ID s) (intersectsID? s0 s1)
        (.isAssignableFrom IS s) (intersectsIS? s0 s1)
        (.equals DI s) (intersectsDI? s0 s1)
        (.equals DD s) (intersectsDD? s0 s1)
        (.isAssignableFrom DS s) (intersectsDS? s0 s1)
        (.isAssignableFrom SI s) (intersectsSI? s0 s1)
        (.isAssignableFrom SD s) (intersectsSD? s0 s1)
        (.isAssignableFrom SS s) (intersectsSS? s0 s1)
        :else
        (throw (cant-intersect-exception s0 s1))))))
;;----------------------------------------------------------------
