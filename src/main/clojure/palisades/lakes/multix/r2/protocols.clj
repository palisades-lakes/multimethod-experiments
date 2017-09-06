(set! *warn-on-reflection* true) 
(set! *unchecked-math* :warn-on-boxed)
;;----------------------------------------------------------------
(ns palisades.lakes.multix.r2.protocols
  
  {:doc "benchmarking generic function implementations
         testing 3 arg dispatch and too many methods"
   :author "palisades dot lakes at gmail dot com"
   :since "2017-08-22"
   :version "2017-09-05"}
  
  (:require [palisades.lakes.dynafun.core :as d])
  
  (:import [palisades.lakes.bench.java.spaces.linear
            LinearFunction Vector]
           [palisades.lakes.bench.java.spaces.linear.r2
            B2 S2 I2 L2 F2 D2 B22 S22 I22 L22 F22 D22]))
;;----------------------------------------------------------------
(defprotocol LinearFn
  (axpy 
    [a ^Vector x ^Vector y] 
    "a*x + y"))
;;----------------------------------------------------------------
(extend-protocol LinearFn
  B22 
  (axpy [^B22 a ^Vector x ^Vector y]
    (.axpy a x y))
  D22 
  (axpy [^D22 a ^Vector x ^Vector y]
    (.axpy a x y))
  F22 
  (axpy [^F22 a ^Vector x ^Vector y]
    (.axpy a x y))
  I22 
  (axpy [^I22 a ^Vector x ^Vector y]
    (.axpy a x y))
  L22 
  (axpy [^L22 a ^Vector x ^Vector y]
    (.axpy a x y))
  S22 
  (axpy [^S22 a ^Vector x ^Vector y]
    (.axpy a x y)))
;;----------------------------------------------------------------
