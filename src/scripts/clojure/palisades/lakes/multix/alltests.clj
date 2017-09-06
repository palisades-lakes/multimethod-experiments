(set! *warn-on-reflection* true)
(set! *unchecked-math* :warn-on-boxed)
;;----------------------------------------------------------------
(ns palisades.lakes.multix.alltests
  
  "Run all the benchmarks."
  
  {:author "palisades dot lakes at gmail dot com"
   :since "2017-08-25"
   :version "2017-09-05"}
  
  (:require 
    [palisades.lakes.multix.axpy.test]
    [palisades.lakes.multix.contains.test]
    [palisades.lakes.multix.diameter.test]
    [palisades.lakes.multix.intersects.test]))
;;----------------------------------------------------------------
