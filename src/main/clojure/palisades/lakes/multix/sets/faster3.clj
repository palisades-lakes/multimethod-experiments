(set! *warn-on-reflection* true) 
(set! *unchecked-math* :warn-on-boxed)
;;----------------------------------------------------------------
(ns palisades.lakes.multix.sets.faster3
  
  {:doc "palisades.lakes.multimethods.core/defmulti for set intersection testing;
         with no hierarchy and PersistentVector dispatch values."
   :author "palisades dot lakes at gmail dot com"
   :since "2017-07-29"
   :version "2017-08-23"}

  (:refer-clojure :exclude [contains?])
  
  (:require [palisades.lakes.multimethods.core :as d])
  
  (:import [java.util Collections Set]
           [palisades.lakes.bench.java.sets 
            ByteInterval DoubleInterval FloatInterval
            IntegerInterval LongInterval ShortInterval]))
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
  (d/signature IntegerInterval Set)
  [^IntegerInterval s0 ^Set s1]
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
  (d/signature DoubleInterval Set)
  [^DoubleInterval s0 ^Set s1]
  (.intersects s0 s1))
;;----------------------------------------------------------------
(d/defmethod intersects? 
  (d/signature Set IntegerInterval)
  [^Set s0 ^IntegerInterval s1]
  (.intersects s1 s0))
(d/defmethod intersects? 
  (d/signature Set DoubleInterval)
  [^Set s0 ^DoubleInterval s1]
  (.intersects s1 s0))
(d/defmethod intersects? 
  (d/signature Set Set)
  [^Set s0 ^Set s1] 
  (not (Collections/disjoint s0 s1)))
;;----------------------------------------------------------------
;; contains? 43 methods
;;----------------------------------------------------------------
(d/defmulti contains?
  {:doc "Test for general set containment. 43 methods"}
  (fn contains?-dispatch [s0 s1] (d/extract-signature s0 s1)))
;;----------------------------------------------------------------
(d/defmethod contains? (d/signature Set Object) [^Set s ^Object x] (.contains s x))
;;----------------------------------------------------------------
(d/defmethod contains? (d/signature ByteInterval Byte) [^ByteInterval s ^Byte x] (.contains s x))
(d/defmethod contains? (d/signature ByteInterval Double) [^ByteInterval s ^Double x] (.contains s x))
(d/defmethod contains? (d/signature ByteInterval Float) [^ByteInterval s ^Float x] (.contains s x))
(d/defmethod contains? (d/signature ByteInterval Integer) [^ByteInterval s ^Integer x] (.contains s x))
(d/defmethod contains? (d/signature ByteInterval Long) [^ByteInterval s ^Long x] (.contains s x))
(d/defmethod contains? (d/signature ByteInterval Short) [^ByteInterval s ^Short x] (.contains s x))
(d/defmethod contains? (d/signature ByteInterval Object) [^ByteInterval s ^Object x] false)
;;----------------------------------------------------------------
(d/defmethod contains? (d/signature DoubleInterval Byte) [^DoubleInterval s ^Byte x] (.contains s x))
(d/defmethod contains? (d/signature DoubleInterval Double) [^DoubleInterval s ^Double x] (.contains s x))
(d/defmethod contains? (d/signature DoubleInterval Float) [^DoubleInterval s ^Float x] (.contains s x))
(d/defmethod contains? (d/signature DoubleInterval Integer) [^DoubleInterval s ^Integer x] (.contains s x))
(d/defmethod contains? (d/signature DoubleInterval Long) [^DoubleInterval s ^Long x] (.contains s x))
(d/defmethod contains? (d/signature DoubleInterval Short) [^DoubleInterval s ^Short x] (.contains s x))
(d/defmethod contains? (d/signature DoubleInterval Object) [^DoubleInterval s ^Object x] false)
;;----------------------------------------------------------------
(d/defmethod contains? (d/signature FloatInterval Byte) [^FloatInterval s ^Byte x] (.contains s x))
(d/defmethod contains? (d/signature FloatInterval Double) [^FloatInterval s ^Double x] (.contains s x))
(d/defmethod contains? (d/signature FloatInterval Float) [^FloatInterval s ^Float x] (.contains s x))
(d/defmethod contains? (d/signature FloatInterval Integer) [^FloatInterval s ^Integer x] (.contains s x))
(d/defmethod contains? (d/signature FloatInterval Long) [^FloatInterval s ^Long x] (.contains s x))
(d/defmethod contains? (d/signature FloatInterval Short) [^FloatInterval s ^Short x] (.contains s x))
(d/defmethod contains? (d/signature FloatInterval Object) [^FloatInterval s ^Object x] false)
;;----------------------------------------------------------------
(d/defmethod contains? (d/signature IntegerInterval Byte) [^IntegerInterval s ^Byte x] (.contains s x))
(d/defmethod contains? (d/signature IntegerInterval Double) [^IntegerInterval s ^Double x] (.contains s x))
(d/defmethod contains? (d/signature IntegerInterval Float) [^IntegerInterval s ^Float x] (.contains s x))
(d/defmethod contains? (d/signature IntegerInterval Integer) [^IntegerInterval s ^Integer x] (.contains s x))
(d/defmethod contains? (d/signature IntegerInterval Long) [^IntegerInterval s ^Long x] (.contains s x))
(d/defmethod contains? (d/signature IntegerInterval Short) [^IntegerInterval s ^Short x] (.contains s x))
(d/defmethod contains? (d/signature IntegerInterval Object) [^IntegerInterval s ^Object x] false)
;;----------------------------------------------------------------
(d/defmethod contains? (d/signature LongInterval Byte) [^LongInterval s ^Byte x] (.contains s x))
(d/defmethod contains? (d/signature LongInterval Double) [^LongInterval s ^Double x] (.contains s x))
(d/defmethod contains? (d/signature LongInterval Float) [^LongInterval s ^Float x] (.contains s x))
(d/defmethod contains? (d/signature LongInterval Integer) [^LongInterval s ^Integer x] (.contains s x))
(d/defmethod contains? (d/signature LongInterval Long) [^LongInterval s ^Long x] (.contains s x))
(d/defmethod contains? (d/signature LongInterval Short) [^LongInterval s ^Short x] (.contains s x))
(d/defmethod contains? (d/signature LongInterval Object) [^LongInterval s ^Object x] false)
;;----------------------------------------------------------------
(d/defmethod contains? (d/signature ShortInterval Byte) [^ShortInterval s ^Byte x] (.contains s x))
(d/defmethod contains? (d/signature ShortInterval Double) [^ShortInterval s ^Double x] (.contains s x))
(d/defmethod contains? (d/signature ShortInterval Float) [^ShortInterval s ^Float x] (.contains s x))
(d/defmethod contains? (d/signature ShortInterval Integer) [^ShortInterval s ^Integer x] (.contains s x))
(d/defmethod contains? (d/signature ShortInterval Long) [^ShortInterval s ^Long x] (.contains s x))
(d/defmethod contains? (d/signature ShortInterval Short) [^ShortInterval s ^Short x] (.contains s x))
(d/defmethod contains? (d/signature ShortInterval Object) [^ShortInterval s ^Object x] false)
;;----------------------------------------------------------------
