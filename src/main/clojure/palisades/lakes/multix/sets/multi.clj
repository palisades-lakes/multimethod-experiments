(set! *warn-on-reflection* true) 
(set! *unchecked-math* :warn-on-boxed)
;;----------------------------------------------------------------
(ns palisades.lakes.multix.sets.multi
  
  {:doc "clojure.core/defmulti for set intersection testing."
   :author "palisades dot lakes at gmail dot com"
   :since "2017-04-20"
   :version "2017-08-23"}
  
  (:refer-clojure :exclude [contains?])
  
  (:import [java.util Collections Set]
           [palisades.lakes.bench.java.sets 
            ByteInterval DoubleInterval FloatInterval
            IntegerInterval LongInterval ShortInterval]))
;;----------------------------------------------------------------
(defmulti intersects?
  "Test for general set intersection. 9 methods."
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
  [IntegerInterval Set]
  [^IntegerInterval s0 ^Set s1]
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
  [DoubleInterval Set]
  [^DoubleInterval s0 ^Set s1]
  (.intersects s0 s1))
;;----------------------------------------------------------------
(defmethod intersects? 
  [Set IntegerInterval]
  [^Set s0 ^IntegerInterval s1]
  (.intersects s1 s0))
(defmethod intersects? 
  [Set DoubleInterval]
  [^Set s0 ^DoubleInterval s1]
  (.intersects s1 s0))
(defmethod intersects? 
  [Set Set]
  [^Set s0 ^Set s1] 
  (not (Collections/disjoint s0 s1)))
;;----------------------------------------------------------------
;; contains? 43 methods
;;----------------------------------------------------------------
(defmulti contains?
  {:doc "Test for general set containment. 43 methods"}
  (fn contains?-dispatch [s0 s1] 
    [(.getClass ^Object s0) (.getClass ^Object s1)]))
;;----------------------------------------------------------------
(defmethod contains? [Set Object] [^Set s ^Object x] (.contains s x))
;;----------------------------------------------------------------
(defmethod contains? [ByteInterval Byte] [^ByteInterval s ^Byte x] (.contains s x))
(defmethod contains? [ByteInterval Double] [^ByteInterval s ^Double x] (.contains s x))
(defmethod contains? [ByteInterval Float] [^ByteInterval s ^Float x] (.contains s x))
(defmethod contains? [ByteInterval Integer] [^ByteInterval s ^Integer x] (.contains s x))
(defmethod contains? [ByteInterval Long] [^ByteInterval s ^Long x] (.contains s x))
(defmethod contains? [ByteInterval Short] [^ByteInterval s ^Short x] (.contains s x))
(defmethod contains? [ByteInterval Object] [^ByteInterval s ^Object x] false)
;;----------------------------------------------------------------
(defmethod contains? [DoubleInterval Byte] [^DoubleInterval s ^Byte x] (.contains s x))
(defmethod contains? [DoubleInterval Double] [^DoubleInterval s ^Double x] (.contains s x))
(defmethod contains? [DoubleInterval Float] [^DoubleInterval s ^Float x] (.contains s x))
(defmethod contains? [DoubleInterval Integer] [^DoubleInterval s ^Integer x] (.contains s x))
(defmethod contains? [DoubleInterval Long] [^DoubleInterval s ^Long x] (.contains s x))
(defmethod contains? [DoubleInterval Short] [^DoubleInterval s ^Short x] (.contains s x))
(defmethod contains? [DoubleInterval Object] [^DoubleInterval s ^Object x] false)
;;----------------------------------------------------------------
(defmethod contains? [FloatInterval Byte] [^FloatInterval s ^Byte x] (.contains s x))
(defmethod contains? [FloatInterval Double] [^FloatInterval s ^Double x] (.contains s x))
(defmethod contains? [FloatInterval Float] [^FloatInterval s ^Float x] (.contains s x))
(defmethod contains? [FloatInterval Integer] [^FloatInterval s ^Integer x] (.contains s x))
(defmethod contains? [FloatInterval Long] [^FloatInterval s ^Long x] (.contains s x))
(defmethod contains? [FloatInterval Short] [^FloatInterval s Short x] (.contains s x))
(defmethod contains? [FloatInterval Object] [^FloatInterval s Object x] false)
;;----------------------------------------------------------------
(defmethod contains? [IntegerInterval Byte] [^IntegerInterval s ^Byte x] (.contains s x))
(defmethod contains? [IntegerInterval Double] [^IntegerInterval s ^Double x] (.contains s x))
(defmethod contains? [IntegerInterval Float] [^IntegerInterval s ^Float x] (.contains s x))
(defmethod contains? [IntegerInterval Integer] [^IntegerInterval s ^Integer x] (.contains s x))
(defmethod contains? [IntegerInterval Long] [^IntegerInterval s ^Long x] (.contains s x))
(defmethod contains? [IntegerInterval Short] [^IntegerInterval s ^Short x] (.contains s x))
(defmethod contains? [IntegerInterval Object] [^IntegerInterval s ^Object x] false)
;;----------------------------------------------------------------
(defmethod contains? [LongInterval Byte] [^LongInterval s ^Byte x] (.contains s x))
(defmethod contains? [LongInterval Double] [^LongInterval s ^Double x] (.contains s x))
(defmethod contains? [LongInterval Float] [^LongInterval s ^Float x] (.contains s x))
(defmethod contains? [LongInterval Integer] [^LongInterval s ^Integer x] (.contains s x))
(defmethod contains? [LongInterval Long] [^LongInterval s ^Long x] (.contains s x))
(defmethod contains? [LongInterval Short] [^LongInterval s ^Short x] (.contains s x))
(defmethod contains? [LongInterval Object] [^LongInterval s ^Object x] false)
;;----------------------------------------------------------------
(defmethod contains? [ShortInterval Byte] [^ShortInterval s ^Byte x] (.contains s x))
(defmethod contains? [ShortInterval Double] [^ShortInterval s ^Double x] (.contains s x))
(defmethod contains? [ShortInterval Float] [^ShortInterval s ^Float x] (.contains s x))
(defmethod contains? [ShortInterval Integer] [^ShortInterval s ^Integer x] (.contains s x))
(defmethod contains? [ShortInterval Long] [^ShortInterval s ^Long x] (.contains s x))
(defmethod contains? [ShortInterval Short] [^ShortInterval s ^Short x] (.contains s x))
(defmethod contains? [ShortInterval Object] [^ShortInterval s ^Object x] false)
;;----------------------------------------------------------------
