(set! *warn-on-reflection* true) 
(set! *unchecked-math* :warn-on-boxed)
;;----------------------------------------------------------------
(ns palisades.lakes.multix.sets.protocols
  
  {:doc "a protocol for general set operations."
   :author "palisades dot lakes at gmail dot com"
   :since "2017-09-05"
   :version "2017-09-05"}
  
  (:refer-clojure :exclude [contains?])
  
  (:require [palisades.lakes.dynafun.core :as d])
  
  (:import [java.util Collections]
           [palisades.lakes.bench.java.sets 
            Contains Diameter Intersects Sets
            ByteInterval DoubleInterval FloatInterval
            IntegerInterval LongInterval ShortInterval]))
;;----------------------------------------------------------------
(defprotocol Set
  (diameter [s] "maximum distance between elements.")
  (contains? [s x] "is x an element of s?")
  (intersects? [s0 s1] "Do s0 and s1 have any elements in common?"))
;;----------------------------------------------------------------
(extend-protocol Set
  
  java.util.Set
  (diameter [^java.util.Set s] (Diameter/diameter s))
  (contains? [^java.util.Set s ^Object x] (.contains s x))
  (intersects? [^java.util.Set s0 s1] (Intersects/intersects s0 s1))
  
  ;; TODO: does extending the individual
  ;; palisades.lakes.bench.java.sets.Set
  ;; implementing classes hlep?
  
  palisades.lakes.bench.java.sets.Set
  (diameter [^palisades.lakes.bench.java.sets.Set s]
    (.diameter s))
  (contains? [^palisades.lakes.bench.java.sets.Set s ^Object x] 
    (.contains s x))
  (intersects? [^palisades.lakes.bench.java.sets.Set s0 
                ^palisades.lakes.bench.java.sets.Set s1] 
    (Intersects/intersects s0 s1)))
;;----------------------------------------------------------------
