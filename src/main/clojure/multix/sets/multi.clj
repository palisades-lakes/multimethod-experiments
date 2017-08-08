(set! *warn-on-reflection* true) 
(set! *unchecked-math* :warn-on-boxed)
;;----------------------------------------------------------------
(ns multix.sets.multi
  
  {:doc "clojure.core/defmulti for set intersection testing."
   :author "mcdonald dot john dot alan at gmail dot com"
   :since "2017-04-20"
   :version "2017-08-03"}
  
  (:import [java.util Collections]
           [benchtools.java.sets DoubleInterval IntegerInterval]))
;;----------------------------------------------------------------
(defmulti intersects?
  "Test for general set intersection."
  {}
  (fn intersects?-dispatch [s0 s1] 
    [(.getClass ^Object s0) (.getClass ^Object s1)]))
;;----------------------------------------------------------------
(defmethod intersects? 
  [IntegerInterval IntegerInterval]
  [^IntegerInterval s0 ^IntegerInterval s1]
  (.intersects s0 s1))
(defmethod intersects? 
  [IntegerInterval DoubleInterval]
  [^IntegerInterval s0 ^DoubleInterval s1]
  (.intersects s0 s1))
(defmethod intersects? 
  [IntegerInterval java.util.Set]
  [^IntegerInterval s0 ^java.util.Set s1]
  (.intersects s0 s1))
;;----------------------------------------------------------------
(defmethod intersects? 
  [DoubleInterval IntegerInterval]
  [^DoubleInterval s0 ^IntegerInterval s1]
  (.intersects s1 s0))
(defmethod intersects? 
  [DoubleInterval DoubleInterval]
  [^DoubleInterval s0 ^DoubleInterval s1]
  (.intersects s0 s1))
(defmethod intersects? 
  [DoubleInterval java.util.Set]
  [^DoubleInterval s0 ^java.util.Set s1]
  (.intersects s0 s1))
;;----------------------------------------------------------------
(defmethod intersects? 
  [java.util.Set IntegerInterval]
  [^java.util.Set s0 ^IntegerInterval s1]
  (.intersects s1 s0))
(defmethod intersects? 
  [java.util.Set DoubleInterval]
  [^java.util.Set s0 ^DoubleInterval s1]
  (.intersects s1 s0))
(defmethod intersects? 
  [java.util.Set java.util.Set]
  [^java.util.Set s0 ^java.util.Set s1] 
  (not (Collections/disjoint s0 s1)))
;;----------------------------------------------------------------
