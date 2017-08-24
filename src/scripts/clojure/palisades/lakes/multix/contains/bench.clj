(set! *warn-on-reflection* true)
(set! *unchecked-math* :warn-on-boxed)
;;----------------------------------------------------------------
(ns palisades.lakes.multix.contains.bench
  "Use criterium for alternative multimethod implementations."
  {:author "palisades dot lakes at gmail dot com"
   :since "2017-05-29"
   :version "2017-08-23"}
  (:require [palisades.lakes.multix.contains.defs :as defs]))
;;----------------------------------------------------------------
;; baselines: args always IntegerInterval, Integer
(defs/bench 
  defs/r1 palisades.lakes.bench.java.sets.IntegerInterval 
  defs/n1 Integer
  [defs/defmulti
   defs/manual-java
   defs/dynafun
   defs/no-hierarchy
   defs/signature-dispatch-value
   defs/non-volatile-cache
   defs/hashmap-tables])
(defs/bench 
  defs/r2 palisades.lakes.bench.java.sets.Set
  defs/n2 Number
  [defs/defmulti
   defs/manual-java
   defs/dynafun
   defs/no-hierarchy
   defs/signature-dispatch-value
   defs/non-volatile-cache
   defs/hashmap-tables])
(defs/bench 
  defs/r3 Object
  defs/n3 Number
  [defs/defmulti
   defs/manual-java
   defs/dynafun
   defs/no-hierarchy
   defs/signature-dispatch-value
   defs/non-volatile-cache
   defs/hashmap-tables])
(defs/bench 
  defs/r7 Object
  defs/n6 Object
  [defs/defmulti
   defs/manual-java
   defs/dynafun
   defs/no-hierarchy
   defs/signature-dispatch-value
   defs/non-volatile-cache
   defs/hashmap-tables])
;;----------------------------------------------------------------
(shutdown-agents)
(System/exit 0)
