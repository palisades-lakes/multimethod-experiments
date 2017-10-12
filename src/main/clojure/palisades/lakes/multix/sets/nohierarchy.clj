(set! *warn-on-reflection* true) 
(set! *unchecked-math* :warn-on-boxed)
;;----------------------------------------------------------------
(ns palisades.lakes.multix.sets.nohierarchy
  
  
  {:doc "palisades.lakes.multimethods.core/defmulti for set intersection testing;
         with no hierarchy and Signature2 dispatch values."
   :author "palisades dot lakes at gmail dot com"
   :since "2017-04-20"
   :version "2017-10-11"}
  
  (:refer-clojure :exclude [contains?])
  
  (:require [palisades.lakes.multimethods.core :as d])
  
  (:import [java.util Collections]
           [palisades.lakes.bench.java.sets 
            Contains Diameter Intersects Set Sets
            ByteInterval DoubleInterval FloatInterval
            IntegerInterval LongInterval ShortInterval]))
;;----------------------------------------------------------------
;; diameter 2 methods primitive return value
;;----------------------------------------------------------------
(d/defmulti ^Double/TYPE diameter
  "Max distance between elements. 2 methods."
  {}
  class
  :hierarchy false)
;;----------------------------------------------------------------
(d/defmethod diameter java.util.Set ^double [^java.util.Set s] 
  (Diameter/diameter s))
;;----------------------------------------------------------------
(d/defmethod diameter Set ^double [^Set s] (.diameter s))
;;----------------------------------------------------------------
;; intersects? 9 methods
;;----------------------------------------------------------------
(d/defmulti intersects?
  "Test for general set intersection. 9 methods."
  {}
  d/signature
  :hierarchy false)
;;----------------------------------------------------------------
(d/defmethod intersects? 
  (d/to-signature IntegerInterval IntegerInterval)
  [^IntegerInterval s0 ^IntegerInterval s1]
  (.intersects s0 s1))
(d/defmethod intersects? 
  (d/to-signature IntegerInterval DoubleInterval)
  [^IntegerInterval s0 ^DoubleInterval s1]
  (.intersects s0 s1))
(d/defmethod intersects? 
  (d/to-signature IntegerInterval java.util.Set)
  [^IntegerInterval s0 ^java.util.Set s1]
  (.intersects s0 s1))
;;----------------------------------------------------------------
(d/defmethod intersects? 
  (d/to-signature DoubleInterval IntegerInterval)
  [^DoubleInterval s0 ^IntegerInterval s1]
  (.intersects s1 s0))
(d/defmethod intersects? 
  (d/to-signature DoubleInterval DoubleInterval)
  [^DoubleInterval s0 ^DoubleInterval s1]
  (.intersects s0 s1))
(d/defmethod intersects? 
  (d/to-signature DoubleInterval java.util.Set)
  [^DoubleInterval s0 ^java.util.Set s1]
  (.intersects s0 s1))
;;----------------------------------------------------------------
(d/defmethod intersects? 
  (d/to-signature java.util.Set IntegerInterval)
  [^java.util.Set s0 ^IntegerInterval s1]
  (.intersects s1 s0))
(d/defmethod intersects? 
  (d/to-signature java.util.Set DoubleInterval)
  [^java.util.Set s0 ^DoubleInterval s1]
  (.intersects s1 s0))
(d/defmethod intersects? 
  (d/to-signature java.util.Set java.util.Set)
  [^java.util.Set s0 ^java.util.Set s1] 
  (not (Collections/disjoint s0 s1)))
;;----------------------------------------------------------------
;; contains? 43 methods
;;----------------------------------------------------------------
(d/defmulti contains?
  {:doc "Test for general set containment. 43 methods"}
  d/signature
  :hierarchy false)
;;----------------------------------------------------------------
(d/defmethod contains? (d/to-signature java.util.Set Object) [^java.util.Set s ^Object x] (.contains s x))
;;----------------------------------------------------------------
(d/defmethod contains? (d/to-signature ByteInterval Byte) [^ByteInterval s ^Byte x] (.contains s x))
(d/defmethod contains? (d/to-signature ByteInterval Double) [^ByteInterval s ^Double x] (.contains s x))
(d/defmethod contains? (d/to-signature ByteInterval Float) [^ByteInterval s ^Float x] (.contains s x))
(d/defmethod contains? (d/to-signature ByteInterval Integer) [^ByteInterval s ^Integer x] (.contains s x))
(d/defmethod contains? (d/to-signature ByteInterval Long) [^ByteInterval s ^Long x] (.contains s x))
(d/defmethod contains? (d/to-signature ByteInterval Short) [^ByteInterval s ^Short x] (.contains s x))
(d/defmethod contains? (d/to-signature ByteInterval Object) [^ByteInterval s ^Object x] false)
;;----------------------------------------------------------------
(d/defmethod contains? (d/to-signature DoubleInterval Byte) [^DoubleInterval s ^Byte x] (.contains s x))
(d/defmethod contains? (d/to-signature DoubleInterval Double) [^DoubleInterval s ^Double x] (.contains s x))
(d/defmethod contains? (d/to-signature DoubleInterval Float) [^DoubleInterval s ^Float x] (.contains s x))
(d/defmethod contains? (d/to-signature DoubleInterval Integer) [^DoubleInterval s ^Integer x] (.contains s x))
(d/defmethod contains? (d/to-signature DoubleInterval Long) [^DoubleInterval s ^Long x] (.contains s x))
(d/defmethod contains? (d/to-signature DoubleInterval Short) [^DoubleInterval s ^Short x] (.contains s x))
(d/defmethod contains? (d/to-signature DoubleInterval Object) [^DoubleInterval s ^Object x] false)
;;----------------------------------------------------------------
(d/defmethod contains? (d/to-signature FloatInterval Byte) [^FloatInterval s ^Byte x] (.contains s x))
(d/defmethod contains? (d/to-signature FloatInterval Double) [^FloatInterval s ^Double x] (.contains s x))
(d/defmethod contains? (d/to-signature FloatInterval Float) [^FloatInterval s ^Float x] (.contains s x))
(d/defmethod contains? (d/to-signature FloatInterval Integer) [^FloatInterval s ^Integer x] (.contains s x))
(d/defmethod contains? (d/to-signature FloatInterval Long) [^FloatInterval s ^Long x] (.contains s x))
(d/defmethod contains? (d/to-signature FloatInterval Short) [^FloatInterval s ^Short x] (.contains s x))
(d/defmethod contains? (d/to-signature FloatInterval Object) [^FloatInterval s ^Object x] false)
;;----------------------------------------------------------------
(d/defmethod contains? (d/to-signature IntegerInterval Byte) [^IntegerInterval s ^Byte x] (.contains s x))
(d/defmethod contains? (d/to-signature IntegerInterval Double) [^IntegerInterval s ^Double x] (.contains s x))
(d/defmethod contains? (d/to-signature IntegerInterval Float) [^IntegerInterval s ^Float x] (.contains s x))
(d/defmethod contains? (d/to-signature IntegerInterval Integer) [^IntegerInterval s ^Integer x] (.contains s x))
(d/defmethod contains? (d/to-signature IntegerInterval Long) [^IntegerInterval s ^Long x] (.contains s x))
(d/defmethod contains? (d/to-signature IntegerInterval Short) [^IntegerInterval s ^Short x] (.contains s x))
(d/defmethod contains? (d/to-signature IntegerInterval Object) [^IntegerInterval s ^Object x] false)
;;----------------------------------------------------------------
(d/defmethod contains? (d/to-signature LongInterval Byte) [^LongInterval s ^Byte x] (.contains s x))
(d/defmethod contains? (d/to-signature LongInterval Double) [^LongInterval s ^Double x] (.contains s x))
(d/defmethod contains? (d/to-signature LongInterval Float) [^LongInterval s ^Float x] (.contains s x))
(d/defmethod contains? (d/to-signature LongInterval Integer) [^LongInterval s ^Integer x] (.contains s x))
(d/defmethod contains? (d/to-signature LongInterval Long) [^LongInterval s ^Long x] (.contains s x))
(d/defmethod contains? (d/to-signature LongInterval Short) [^LongInterval s ^Short x] (.contains s x))
(d/defmethod contains? (d/to-signature LongInterval Object) [^LongInterval s ^Object x] false)
;;----------------------------------------------------------------
(d/defmethod contains? (d/to-signature ShortInterval Byte) [^ShortInterval s ^Byte x] (.contains s x))
(d/defmethod contains? (d/to-signature ShortInterval Double) [^ShortInterval s ^Double x] (.contains s x))
(d/defmethod contains? (d/to-signature ShortInterval Float) [^ShortInterval s ^Float x] (.contains s x))
(d/defmethod contains? (d/to-signature ShortInterval Integer) [^ShortInterval s ^Integer x] (.contains s x))
(d/defmethod contains? (d/to-signature ShortInterval Long) [^ShortInterval s ^Long x] (.contains s x))
(d/defmethod contains? (d/to-signature ShortInterval Short) [^ShortInterval s ^Short x] (.contains s x))
(d/defmethod contains? (d/to-signature ShortInterval Object) [^ShortInterval s ^Object x] false)
;;----------------------------------------------------------------
