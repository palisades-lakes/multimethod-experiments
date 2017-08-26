(set! *warn-on-reflection* true) 
(set! *unchecked-math* :warn-on-boxed)
;;----------------------------------------------------------------
(ns palisades.lakes.multix.sets.multi1
  
  {:doc "Replace PesistantHashMap with HashMap in MultiFn."
   :author "palisades dot lakes at gmail dot com"
   :since "2017-07-29"
   :version "2017-08-26"}
  
  (:refer-clojure :exclude [contains?])
  
  (:require [palisades.lakes.multix.multi1 :as d])
  
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
  class)
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
;; contains? 43 methods
;;----------------------------------------------------------------
(d/defmulti contains?
  {:doc "Test for general set containment. 43 methods"}
  (fn contains?-dispatch [s0 s1] 
    [(.getClass ^Object s0) (.getClass ^Object s1)]))
;;----------------------------------------------------------------
(d/defmethod contains? [java.util.Set Object] [^java.util.Set s ^Object x] (.contains s x))
;;----------------------------------------------------------------
(d/defmethod contains? [ByteInterval Byte] [^ByteInterval s ^Byte x] (.contains s x))
(d/defmethod contains? [ByteInterval Double] [^ByteInterval s ^Double x] (.contains s x))
(d/defmethod contains? [ByteInterval Float] [^ByteInterval s ^Float x] (.contains s x))
(d/defmethod contains? [ByteInterval Integer] [^ByteInterval s ^Integer x] (.contains s x))
(d/defmethod contains? [ByteInterval Long] [^ByteInterval s ^Long x] (.contains s x))
(d/defmethod contains? [ByteInterval Short] [^ByteInterval s ^Short x] (.contains s x))
(d/defmethod contains? [ByteInterval Object] [^ByteInterval s ^Object x] false)
;;----------------------------------------------------------------
(d/defmethod contains? [DoubleInterval Byte] [^DoubleInterval s ^Byte x] (.contains s x))
(d/defmethod contains? [DoubleInterval Double] [^DoubleInterval s ^Double x] (.contains s x))
(d/defmethod contains? [DoubleInterval Float] [^DoubleInterval s ^Float x] (.contains s x))
(d/defmethod contains? [DoubleInterval Integer] [^DoubleInterval s ^Integer x] (.contains s x))
(d/defmethod contains? [DoubleInterval Long] [^DoubleInterval s ^Long x] (.contains s x))
(d/defmethod contains? [DoubleInterval Short] [^DoubleInterval s ^Short x] (.contains s x))
(d/defmethod contains? [DoubleInterval Object] [^DoubleInterval s ^Object x] false)
;;----------------------------------------------------------------
(d/defmethod contains? [FloatInterval Byte] [^FloatInterval s ^Byte x] (.contains s x))
(d/defmethod contains? [FloatInterval Double] [^FloatInterval s ^Double x] (.contains s x))
(d/defmethod contains? [FloatInterval Float] [^FloatInterval s ^Float x] (.contains s x))
(d/defmethod contains? [FloatInterval Integer] [^FloatInterval s ^Integer x] (.contains s x))
(d/defmethod contains? [FloatInterval Long] [^FloatInterval s ^Long x] (.contains s x))
(d/defmethod contains? [FloatInterval Short] [^FloatInterval s ^Short x] (.contains s x))
(d/defmethod contains? [FloatInterval Object] [^FloatInterval s ^Object x] false)
;;----------------------------------------------------------------
(d/defmethod contains? [IntegerInterval Byte] [^IntegerInterval s ^Byte x] (.contains s x))
(d/defmethod contains? [IntegerInterval Double] [^IntegerInterval s ^Double x] (.contains s x))
(d/defmethod contains? [IntegerInterval Float] [^IntegerInterval s ^Float x] (.contains s x))
(d/defmethod contains? [IntegerInterval Integer] [^IntegerInterval s ^Integer x] (.contains s x))
(d/defmethod contains? [IntegerInterval Long] [^IntegerInterval s ^Long x] (.contains s x))
(d/defmethod contains? [IntegerInterval Short] [^IntegerInterval s ^Short x] (.contains s x))
(d/defmethod contains? [IntegerInterval Object] [^IntegerInterval s ^Object x] false)
;;----------------------------------------------------------------
(d/defmethod contains? [LongInterval Byte] [^LongInterval s ^Byte x] (.contains s x))
(d/defmethod contains? [LongInterval Double] [^LongInterval s ^Double x] (.contains s x))
(d/defmethod contains? [LongInterval Float] [^LongInterval s ^Float x] (.contains s x))
(d/defmethod contains? [LongInterval Integer] [^LongInterval s ^Integer x] (.contains s x))
(d/defmethod contains? [LongInterval Long] [^LongInterval s ^Long x] (.contains s x))
(d/defmethod contains? [LongInterval Short] [^LongInterval s ^Short x] (.contains s x))
(d/defmethod contains? [LongInterval Object] [^LongInterval s ^Object x] false)
;;----------------------------------------------------------------
(d/defmethod contains? [ShortInterval Byte] [^ShortInterval s ^Byte x] (.contains s x))
(d/defmethod contains? [ShortInterval Double] [^ShortInterval s ^Double x] (.contains s x))
(d/defmethod contains? [ShortInterval Float] [^ShortInterval s ^Float x] (.contains s x))
(d/defmethod contains? [ShortInterval Integer] [^ShortInterval s ^Integer x] (.contains s x))
(d/defmethod contains? [ShortInterval Long] [^ShortInterval s ^Long x] (.contains s x))
(d/defmethod contains? [ShortInterval Short] [^ShortInterval s ^Short x] (.contains s x))
(d/defmethod contains? [ShortInterval Object] [^ShortInterval s ^Object x] false)
;;----------------------------------------------------------------
