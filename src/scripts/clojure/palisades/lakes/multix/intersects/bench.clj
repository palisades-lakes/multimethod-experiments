(set! *warn-on-reflection* true)
(set! *unchecked-math* :warn-on-boxed)
;;----------------------------------------------------------------
(ns palisades.lakes.multix.intersects.bench
  
  "Use criterium for alternative multimethod implementations."
  {:author "palisades dot lakes at gmail dot com"
   :since "2017-05-29"
   :version "2017-08-25"}
  
  (:require [palisades.lakes.bench.core :as bench]
            [palisades.lakes.multix.intersects.defs :as defs]))
;;----------------------------------------------------------------
(let [n (* 1 4 1024 1024)]
  ;; baselines: both args always IntegerInterval
  (bench/bench 
    [defs/r1 palisades.lakes.bench.java.sets.IntegerInterval 
     defs/r1 palisades.lakes.bench.java.sets.IntegerInterval]
    [defs/invokestatic
     defs/invokevirtual
     defs/invokeinterface
     defs/defmulti
     defs/manual-java
     defs/dynafun
     defs/no-hierarchy
     defs/signature-dispatch-value
     defs/non-volatile-cache
     defs/hashmap-tables]
    n)
  ;; 50% probability of repeat same method, 
  ;; 1st arg always IntegerInterval
  ;; 2nd randomly IntegerInterval or DoubleInterval
  (bench/bench 
    [defs/r1 palisades.lakes.bench.java.sets.IntegerInterval 
     defs/r2 palisades.lakes.bench.java.sets.Set]
    [defs/defmulti
     defs/manual-java
     defs/dynafun
     defs/no-hierarchy
     defs/signature-dispatch-value
     defs/non-volatile-cache
     defs/hashmap-tables]
    n)
  ;; 1/9 probability of same method
  ;; 1st and 2nd args randomly from IntegerInterval, 
  ;; DoubleInterval and SingletonSet
  (bench/bench 
    [defs/r3 Object
     defs/r3 Object]
    [defs/defmulti
     defs/manual-java
     defs/dynafun
     defs/no-hierarchy
     defs/signature-dispatch-value
     defs/non-volatile-cache
     defs/hashmap-tables]
    n))
;;----------------------------------------------------------------
(shutdown-agents)
(System/exit 0)
