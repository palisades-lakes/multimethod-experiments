(set! *warn-on-reflection* true) 
(set! *unchecked-math* :warn-on-boxed)
;;----------------------------------------------------------------
(ns palisades.lakes.multix.sets.dynafun
  
  {:doc "palisades.lakes.dynafun.core/dynafun
         for set intersection testing."
   :author "palisades dot lakes at gmail dot com"
   :since "2017-08-19"
   :version "2017-08-30"}
  
  (:refer-clojure :exclude [contains?])
  
  (:require [palisades.lakes.dynafun.core :as d])
  
  (:import [java.util Collections]
           [palisades.lakes.bench.java.sets 
            Contains Diameter Intersects Set Sets
            ByteInterval DoubleInterval FloatInterval
            IntegerInterval LongInterval ShortInterval]))
;;----------------------------------------------------------------
;; diameter 2 methods primitive return value
;;----------------------------------------------------------------
(d/dynafun ^Double/TYPE diameter
           {:doc "Max distance between elements."})
;;----------------------------------------------------------------
(d/defmethod diameter ^double [^java.util.Set s] 
  (Diameter/diameter s))
#_(d/defmethod diameter ^double [^java.util.Set s] 
  (if (>= 1 (.size s)) 
     0.0
     (let [it (.iterator s)
           x0 (double (.doubleValue ^Number (.next it)))]
       (if (Double/isNaN x0)
         Double/NaN
         (loop [smin x0
               smax x0]
           (if-not (.hasNext it)
             (- smax smin)
             (let [^Number x (.next it)
                   xx (.doubleValue x)]
              (cond (Double/isNaN xx) Double/NaN
                    (xx < smin) (recur xx smax)
                    (xx > smax) (recur smin xx)
                     :else (recur smin smax)))))))))
;;----------------------------------------------------------------
(d/defmethod diameter ^double [^Set s] (.diameter s))
;(d/defmethod diameter ^double [^ByteInterval s] (.diameter s))
;(d/defmethod diameter ^double [^DoubleInterval s] (.diameter s))
;(d/defmethod diameter ^double [^FloatInterval s] (.diameter s))
;(d/defmethod diameter ^double [^IntegerInterval s] (.diameter s))
;(d/defmethod diameter ^double [^LongInterval s] (.diameter s))
;(d/defmethod diameter ^double [^ShortInterval s] (.diameter s))
;(d/defmethod diameter ^double [^ByteInterval s] (double (- (.max s) (.min s))))
;(d/defmethod diameter ^double [^DoubleInterval s] (- (.max s) (.min s)))
;(d/defmethod diameter ^double [^FloatInterval s] (- (.max s) (.min s)))
;(d/defmethod diameter ^double [^IntegerInterval s] (double (- (.max s) (.min s))))
;(d/defmethod diameter ^double [^LongInterval s] (double (- (.max s) (.min s))))
;(d/defmethod diameter ^double [^ShortInterval s] (double (- (.max s) (.min s))))
;;----------------------------------------------------------------
;; intersects? 9 methods
;;----------------------------------------------------------------
(d/dynafun intersects?
           {:doc "Test for general set intersection. 9 methods."})
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
(d/defmethod intersects? [^java.util.Set s0 ^IntegerInterval s1]
  (.intersects s1 s0))
(d/defmethod intersects? [^java.util.Set s0 ^DoubleInterval s1]
  (.intersects s1 s0))
(d/defmethod intersects? [^java.util.Set s0 ^java.util.Set s1] 
  (not (Collections/disjoint s0 s1)))
;;----------------------------------------------------------------
;; contains? 43 methods
;;----------------------------------------------------------------
(d/dynafun contains? {:doc "Test for general set containment."})
;;----------------------------------------------------------------
(d/defmethod contains? [^java.util.Set s ^Object x] (.contains s x))
;;----------------------------------------------------------------
(d/defmethod contains? [^ByteInterval s ^Byte x] (.contains s x))
(d/defmethod contains? [^ByteInterval s ^Double x] (.contains s x))
(d/defmethod contains? [^ByteInterval s ^Float x] (.contains s x))
(d/defmethod contains? [^ByteInterval s ^Integer x] (.contains s x))
(d/defmethod contains? [^ByteInterval s ^Long x] (.contains s x))
(d/defmethod contains? [^ByteInterval s ^Short x] (.contains s x))
(d/defmethod contains? [^ByteInterval s ^Object x] false)
;;----------------------------------------------------------------
(d/defmethod contains? [^DoubleInterval s ^Byte x] (.contains s x))
(d/defmethod contains? [^DoubleInterval s ^Double x] (.contains s x))
(d/defmethod contains? [^DoubleInterval s ^Float x] (.contains s x))
(d/defmethod contains? [^DoubleInterval s ^Integer x] (.contains s x))
(d/defmethod contains? [^DoubleInterval s ^Long x] (.contains s x))
(d/defmethod contains? [^DoubleInterval s ^Short x] (.contains s x))
(d/defmethod contains? [^DoubleInterval s ^Object x] false)
;;----------------------------------------------------------------
(d/defmethod contains? [^FloatInterval s ^Byte x] (.contains s x))
(d/defmethod contains? [^FloatInterval s ^Double x] (.contains s x))
(d/defmethod contains? [^FloatInterval s ^Float x] (.contains s x))
(d/defmethod contains? [^FloatInterval s ^Integer x] (.contains s x))
(d/defmethod contains? [^FloatInterval s ^Long x] (.contains s x))
(d/defmethod contains? [^FloatInterval s ^Short x] (.contains s x))
(d/defmethod contains? [^FloatInterval s ^Object x] false)
;;----------------------------------------------------------------
(d/defmethod contains? [^IntegerInterval s ^Byte x] (.contains s x))
(d/defmethod contains? [^IntegerInterval s ^Double x] (.contains s x))
(d/defmethod contains? [^IntegerInterval s ^Float x] (.contains s x))
(d/defmethod contains? [^IntegerInterval s ^Integer x] (.contains s x))
(d/defmethod contains? [^IntegerInterval s ^Long x] (.contains s x))
(d/defmethod contains? [^IntegerInterval s ^Short x] (.contains s x))
(d/defmethod contains? [^IntegerInterval s ^Object x] false)
;;----------------------------------------------------------------
(d/defmethod contains? [^LongInterval s ^Byte x] (.contains s x))
(d/defmethod contains? [^LongInterval s ^Double x] (.contains s x))
(d/defmethod contains? [^LongInterval s ^Float x] (.contains s x))
(d/defmethod contains? [^LongInterval s ^Integer x] (.contains s x))
(d/defmethod contains? [^LongInterval s ^Long x] (.contains s x))
(d/defmethod contains? [^LongInterval s ^Short x] (.contains s x))
(d/defmethod contains? [^LongInterval s ^Object x] false)
;;----------------------------------------------------------------
(d/defmethod contains? [^ShortInterval s ^Byte x] (.contains s x))
(d/defmethod contains? [^ShortInterval s ^Double x] (.contains s x))
(d/defmethod contains? [^ShortInterval s ^Float x] (.contains s x))
(d/defmethod contains? [^ShortInterval s ^Integer x] (.contains s x))
(d/defmethod contains? [^ShortInterval s ^Long x] (.contains s x))
(d/defmethod contains? [^ShortInterval s ^Short x] (.contains s x))
(d/defmethod contains? [^ShortInterval s ^Object x] false)
;;----------------------------------------------------------------
