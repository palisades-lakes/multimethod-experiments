(set! *warn-on-reflection* true) 
(set! *unchecked-math* :warn-on-boxed)
;;----------------------------------------------------------------
(ns palisades.lakes.multix.sets.dynafun
  
  {:doc "palisades.lakes.dynafun.core/dynafun
         for set intersection testing."
   :author "palisades dot lakes at gmail dot com"
   :since "2017-08-19"
   :version "2017-08-19"}
    (:require [palisades.lakes.dynafun.core :as d])

  (:import [java.util Collections Set]
           [palisades.lakes.bench.java.sets 
            DoubleInterval IntegerInterval]))
;;----------------------------------------------------------------
(d/dynafun intersects?
  {:doc "Test for general set intersection."})
;;----------------------------------------------------------------
(d/defmethod intersects? [^IntegerInterval s0 ^IntegerInterval s1]
  (.intersects s0 s1))
(d/defmethod intersects? [^IntegerInterval s0 ^DoubleInterval s1]
  (.intersects s0 s1))
(d/defmethod intersects? [^IntegerInterval s0 ^java.util.Set s1]
  (.intersects s0 s1))
;;----------------------------------------------------------------
(d/defmethod intersects? [^DoubleInterval s0 ^IntegerInterval s1]
  (.intersects s1 s0))
(d/defmethod intersects? [^DoubleInterval s0 ^DoubleInterval s1]
  (.intersects s0 s1))
(d/defmethod intersects? [^DoubleInterval s0 ^java.util.Set s1]
  (.intersects s0 s1))
;;----------------------------------------------------------------
(d/defmethod intersects? [^Set s0 ^IntegerInterval s1]
  (.intersects s1 s0))
(d/defmethod intersects? [^Set s0 ^DoubleInterval s1]
  (.intersects s1 s0))
(d/defmethod intersects? [^Set s0 ^Set s1] 
  (not (Collections/disjoint s0 s1)))
;;----------------------------------------------------------------
