(set! *warn-on-reflection* true) 
(set! *unchecked-math* :warn-on-boxed)
;;----------------------------------------------------------------
(ns multix.sets.multi0
  
  {:author "mcdonald dot john dot alan at gmail dot com"
   :since "2017-07-29"
   :version "2017-08-02"}
    (:require [multix.multi0 :as d])

  (:import [java.util Collections]
           [benchtools.java.sets DoubleInterval IntegerInterval]))
;;----------------------------------------------------------------
(d/defmulti intersects?
  "Test for general set intersection."
  {}
  (fn intersects?-dispatch [s0 s1] 
    [(.getClass ^Object s0) (.getClass ^Object s1)]))
;;----------------------------------------------------------------
(d/defmethod intersects? 
  [IntegerInterval IntegerInterval]
  [^IntegerInterval s0 ^IntegerInterval s1]
  (.intersects s0 s1))
(d/defmethod intersects? 
  [IntegerInterval DoubleInterval]
  [^IntegerInterval s0 ^DoubleInterval s1]
  (.intersects s0 s1))
(d/defmethod intersects? 
  [IntegerInterval java.util.Set]
  [^IntegerInterval s0 ^java.util.Set s1]
  (.intersects s0 s1))
;;----------------------------------------------------------------
(d/defmethod intersects? 
  [DoubleInterval IntegerInterval]
  [^DoubleInterval s0 ^IntegerInterval s1]
  (.intersects s1 s0))
(d/defmethod intersects? 
  [DoubleInterval DoubleInterval]
  [^DoubleInterval s0 ^DoubleInterval s1]
  (.intersects s0 s1))
(d/defmethod intersects? 
  [DoubleInterval java.util.Set]
  [^DoubleInterval s0 ^java.util.Set s1]
  (.intersects s0 s1))
;;----------------------------------------------------------------
(d/defmethod intersects? 
  [java.util.Set IntegerInterval]
  [^java.util.Set s0 ^IntegerInterval s1]
  (.intersects s1 s0))
(d/defmethod intersects? 
  [java.util.Set DoubleInterval]
  [^java.util.Set s0 ^DoubleInterval s1]
  (.intersects s1 s0))
(d/defmethod intersects? 
  [java.util.Set java.util.Set]
  [^java.util.Set s0 ^java.util.Set s1] 
  (not (Collections/disjoint s0 s1)))
;;----------------------------------------------------------------
