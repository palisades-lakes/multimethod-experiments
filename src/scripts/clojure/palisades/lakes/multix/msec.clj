(set! *warn-on-reflection* true)
(set! *unchecked-math* :warn-on-boxed)
;;----------------------------------------------------------------
(ns palisades.lakes.multix.msec
  
  "Run all the benchmarks."
  
  {:author "palisades dot lakes at gmail dot com"
   :since "2017-09-18"
   :version "2017-09-18"}
  
  (:require 
    [palisades.lakes.multix.axpy.msec]
    [palisades.lakes.multix.contains.msec]
    [palisades.lakes.multix.diameter.msec]
    [palisades.lakes.multix.intersects.msec]))
;;----------------------------------------------------------------
