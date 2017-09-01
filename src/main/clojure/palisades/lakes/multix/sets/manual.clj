(set! *warn-on-reflection* true) 
(set! *unchecked-math* :warn-on-boxed)
;;----------------------------------------------------------------
(ns palisades.lakes.multix.sets.manual
  
  {:doc "Measure cost of using CLojure IFn as methods,
         rather than direct Java method call."
   :author "palisades dot lakes at gmail dot com"
   :since "2017-06-09"
   :version "2017-09-01"}
  
  (:import [java.util Collections Set]
           [palisades.lakes.bench.java.sets DoubleInterval IntegerInterval]))
;;----------------------------------------------------------------
;; the emthod
;;----------------------------------------------------------------
(defn- intersectsDD? [^DoubleInterval s0 ^DoubleInterval s1] 
  (.intersects s0 s1))
;;----------------------------------------------------------------
(defn- intersectsDI? [^DoubleInterval s0 ^IntegerInterval s1] 
  (.intersects s1 s0))
;;----------------------------------------------------------------
(defn- intersectsDS? [^DoubleInterval s0 ^Set s1] 
  (.intersects s0 s1))
;;----------------------------------------------------------------
(defn- intersectsID? [^IntegerInterval s0 ^DoubleInterval s1] 
  (.intersects s0 s1))
;;----------------------------------------------------------------
(defn- intersectsII? [^IntegerInterval s0 ^IntegerInterval s1] 
  (.intersects s0 s1))
;;----------------------------------------------------------------
(defn- intersectsIS? [^IntegerInterval s0 ^Set s1] 
  (.intersects s0 s1))
;;----------------------------------------------------------------
(defn- intersectsSD? [^Set s0 ^DoubleInterval s1] 
  (.intersects s1 s0))
;;----------------------------------------------------------------
(defn- intersectsSI? [^Set s0 ^IntegerInterval s1] 
  (.intersects s1 s0))
;;----------------------------------------------------------------
(defn- intersectsSS? [^Set s0 ^Set s1] 
  (not (Collections/disjoint s0 s1)))
;;----------------------------------------------------------------
(defn- cant-intersect-exception [s0 s1]
  (UnsupportedOperationException.
    (print-str 
      "can't intersect" (class s0) "and" (class s1))))
;;----------------------------------------------------------------
(defn intersects? [s0 s1] 
  (cond (instance? DoubleInterval s0)
        (cond (instance? IntegerInterval s1) (intersectsDI? s0 s1)
              (instance? DoubleInterval s1) (intersectsDD? s0 s1)
              (instance? Set s1) (intersectsDS? s0 s1)
              :else (throw (cant-intersect-exception s0 s1)))
        
        (instance? IntegerInterval s0)
        (cond (instance? IntegerInterval s1) (intersectsII? s0 s1)
              (instance? DoubleInterval s1) (intersectsID? s0 s1)
              (instance? Set s1) (intersectsIS? s0 s1)
              :else (throw (cant-intersect-exception s0 s1)))
        
        (instance? Set s0)
        (cond (instance? IntegerInterval s1)
              (intersectsSI? s0 s1)
              (instance? DoubleInterval s1)
              (intersectsSD? s0 s1)
              (instance? Set s1)
              (intersectsSS? s0 s1)
              :else (throw (cant-intersect-exception s0 s1)))
        
        :else 
        (throw (cant-intersect-exception s0 s1))))
;;----------------------------------------------------------------
