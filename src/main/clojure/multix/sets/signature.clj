(set! *warn-on-reflection* true) 
(set! *unchecked-math* :warn-on-boxed)
;;----------------------------------------------------------------
(ns multix.sets.signature
  
  {:doc "Manual 'method' (function) lookup fromm signature."
   :author "mcdonald dot john dot alan at gmail dot com"
   :since "2017-06-09"
   :version "2017-08-05"}
  (:require [faster.multimethods.core :as d])
  (:import [java.util Collections Set]
           [benchtools.java.sets DoubleInterval IntegerInterval]))
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
(let [II (d/signature IntegerInterval IntegerInterval)
      ID (d/signature IntegerInterval DoubleInterval)
      IS (d/signature IntegerInterval Set)
      DI (d/signature DoubleInterval IntegerInterval)
      DD (d/signature DoubleInterval DoubleInterval)
      DS (d/signature DoubleInterval Set)
      SI (d/signature Set IntegerInterval)
      SD (d/signature Set DoubleInterval)
      SS (d/signature Set Set)]
  (defn intersects? [s0 s1] 
    (let [s (d/extract-signature s0 s1)]
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
