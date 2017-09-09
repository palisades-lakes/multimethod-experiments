(set! *warn-on-reflection* true) 
(set! *unchecked-math* :warn-on-boxed)
;;----------------------------------------------------------------
(ns palisades.lakes.multix.sets.protocols
  
  {:doc "a protocol for general set operations."
   :author "palisades dot lakes at gmail dot com"
   :since "2017-09-05"
   :version "2017-09-09"}
  
  (:refer-clojure :exclude [contains?])
  
  (:require [palisades.lakes.dynafun.core :as d])
  
  (:import [java.util Collections]
           [palisades.lakes.bench.java.sets 
            Contains Diameter Intersects Sets
            ByteInterval DoubleInterval FloatInterval
            IntegerInterval LongInterval ShortInterval]))
;;----------------------------------------------------------------
(defprotocol Set
  (^double diameter [s] "maximum distance between elements.")
  (contains? [s x] "is x an element of s?")
  (intersects? [s0 s1] "Do s0 and s1 have any elements in common?"))
;;----------------------------------------------------------------
(extend-protocol Set
  
  java.util.Set
  (diameter ^double [^java.util.Set s] (Diameter/diameter s))
  (contains? [^java.util.Set s x] (.contains s x))
  (intersects? [^java.util.Set s0 s1] (Intersects/intersects s0 s1))
  
  ;; TODO: does extending the individual
  ;; palisades.lakes.bench.java.sets.Set
  ;; implementing classes help?
  ;; Seems not so.
  ;; All the variations on defmulti go thru the interface, so
  ;; that's the proper comparison anyway.
  
  palisades.lakes.bench.java.sets.Set
  (diameter ^double [^palisades.lakes.bench.java.sets.Set s] (.diameter s))
  (contains? [^palisades.lakes.bench.java.sets.Set s x] (.contains s x))
  (intersects? [^palisades.lakes.bench.java.sets.Set s0 s1] (.intersects s0 s1)))

;  palisades.lakes.bench.java.sets.ByteInterval
;  (^double diameter ^double [^palisades.lakes.bench.java.sets.ByteInterval s]
;    (.diameter s))
;  (contains? [^palisades.lakes.bench.java.sets.ByteInterval s x] 
;    (.contains s x))
;  (intersects? [^palisades.lakes.bench.java.sets.ByteInterval s0 s1] 
;    (.intersects s0 s1))
;  
;  palisades.lakes.bench.java.sets.DoubleInterval
;  (^double diameter ^double [^palisades.lakes.bench.java.sets.DoubleInterval s]
;    (.diameter s))
;  (contains? [^palisades.lakes.bench.java.sets.DoubleInterval s x] 
;    (.contains s x))
;  (intersects? [^palisades.lakes.bench.java.sets.DoubleInterval s0 s1] 
;    (.intersects s0 s1))
;  
;  palisades.lakes.bench.java.sets.FloatInterval
;  (^double diameter [^palisades.lakes.bench.java.sets.FloatInterval s]
;    (.diameter s))
;  (contains? [^palisades.lakes.bench.java.sets.FloatInterval s x] 
;    (.contains s x))
;  (intersects? [^palisades.lakes.bench.java.sets.FloatInterval s0 s1] 
;    (.intersects s0 s1))
;  
;  palisades.lakes.bench.java.sets.IntegerInterval
;  (^double diameter [^palisades.lakes.bench.java.sets.IntegerInterval s]
;    (.diameter s))
;  (contains? [^palisades.lakes.bench.java.sets.IntegerInterval s x] 
;    (.contains s x))
;  (intersects? [^palisades.lakes.bench.java.sets.IntegerInterval s0 s1] 
;    (.intersects s0 s1))
;  
;  palisades.lakes.bench.java.sets.LongInterval
;  (^double diameter [^palisades.lakes.bench.java.sets.LongInterval s]
;    (.diameter s))
;  (contains? [^palisades.lakes.bench.java.sets.LongInterval s x] 
;    (.contains s x))
;  (intersects? [^palisades.lakes.bench.java.sets.LongInterval s0 s1] 
;    (.intersects s0 s1))
;  
;  palisades.lakes.bench.java.sets.ShortInterval
;  (^double diameter [^palisades.lakes.bench.java.sets.ShortInterval s]
;    (.diameter s))
;  (contains? [^palisades.lakes.bench.java.sets.ShortInterval s x] 
;    (.contains s x))
;  (intersects? [^palisades.lakes.bench.java.sets.ShortInterval s0 s1] 
;    (.intersects s0 s1)))
  
;;----------------------------------------------------------------
