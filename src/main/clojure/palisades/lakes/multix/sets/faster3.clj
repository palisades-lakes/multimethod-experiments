(set! *warn-on-reflection* true) 
(set! *unchecked-math* :warn-on-boxed)
;;----------------------------------------------------------------
(ns palisades.lakes.multix.sets.faster3
  
  {:doc "palisades.lakes.multimethods.core/defmulti for set intersection testing;
         with no hierarchy and PersistentVector dispatch values."
   :author "palisades dot lakes at gmail dot com"
   :since "2017-07-29"
   :version "2017-08-05"}
    (:require [palisades.lakes.multimethods.core :as d])

  (:import [java.util Collections]
           [palisades.lakes.bench.java.sets DoubleInterval IntegerInterval]))
;;----------------------------------------------------------------
(d/defmulti intersects?
  "Test for general set intersection."
  {}
  (fn intersects?-dispatch [s0 s1] (d/extract-signature s0 s1)))
;;----------------------------------------------------------------
(d/defmethod intersects? 
  (d/signature IntegerInterval IntegerInterval)
  [^IntegerInterval s0 ^IntegerInterval s1]
  (.intersects s0 s1))
(d/defmethod intersects? 
  (d/signature IntegerInterval DoubleInterval)
  [^IntegerInterval s0 ^DoubleInterval s1]
  (.intersects s0 s1))
(d/defmethod intersects? 
  (d/signature IntegerInterval java.util.Set)
  [^IntegerInterval s0 ^java.util.Set s1]
  (.intersects s0 s1))
;;----------------------------------------------------------------
(d/defmethod intersects? 
  (d/signature DoubleInterval IntegerInterval)
  [^DoubleInterval s0 ^IntegerInterval s1]
  (.intersects s1 s0))
(d/defmethod intersects? 
  (d/signature DoubleInterval DoubleInterval)
  [^DoubleInterval s0 ^DoubleInterval s1]
  (.intersects s0 s1))
(d/defmethod intersects? 
  (d/signature DoubleInterval java.util.Set)
  [^DoubleInterval s0 ^java.util.Set s1]
  (.intersects s0 s1))
;;----------------------------------------------------------------
(d/defmethod intersects? 
  (d/signature java.util.Set IntegerInterval)
  [^java.util.Set s0 ^IntegerInterval s1]
  (.intersects s1 s0))
(d/defmethod intersects? 
  (d/signature java.util.Set DoubleInterval)
  [^java.util.Set s0 ^DoubleInterval s1]
  (.intersects s1 s0))
(d/defmethod intersects? 
  (d/signature java.util.Set java.util.Set)
  [^java.util.Set s0 ^java.util.Set s1] 
  (not (Collections/disjoint s0 s1)))
;;----------------------------------------------------------------
