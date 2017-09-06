(set! *warn-on-reflection* true)
(set! *unchecked-math* :warn-on-boxed)
;;----------------------------------------------------------------
(ns palisades.lakes.multix.all
  
  "Run all the benchmarks."
  
  {:author "palisades dot lakes at gmail dot com"
   :since "2017-08-25"
   :version "2017-09-06"}
  
  (:require 
    #_[palisades.lakes.multix.axpy.bench]
    #_[palisades.lakes.multix.contains.bench]
    [palisades.lakes.multix.diameter.bench]
    [palisades.lakes.multix.intersects.bench]))
;;----------------------------------------------------------------
