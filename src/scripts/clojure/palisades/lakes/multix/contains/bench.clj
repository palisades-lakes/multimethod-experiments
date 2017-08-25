(set! *warn-on-reflection* true)
(set! *unchecked-math* :warn-on-boxed)
;;----------------------------------------------------------------
(ns palisades.lakes.multix.contains.bench
  "Use criterium for alternative multimethod implementations."
  {:author "palisades dot lakes at gmail dot com"
   :since "2017-05-29"
   :version "2017-08-24"}
  (:require [palisades.lakes.bench.random.prng :as prng]
            [palisades.lakes.bench.random.generators :as g]
            [palisades.lakes.multix.contains.defs :as defs]))
;;----------------------------------------------------------------
;; baselines: args always IntegerInterval, Integer
#_(defs/bench 
    g/IntegerIntervals (g/integer-interval defs/uint)
    prng/ints defs/uint
    [defs/iiint-static
     defs/iiint-virtual
     defs/sint-static
     defs/sint-interface])
(defs/bench 
  g/IntegerIntervals (g/integer-interval defs/uint)
  prng/IntegerArray defs/uInteger
  [defs/iiInteger-static
   defs/iiInteger-virtual
   defs/sInteger-static
   defs/sInteger-interface
   defs/oo-static
   defs/manual-java
   defs/dynafun
   defs/defmulti
   defs/no-hierarchy
   #_defs/signature-dispatch-value
   #_defs/non-volatile-cache
   #_defs/hashmap-tables])
#_(defs/bench 
    defs/r2 palisades.lakes.bench.java.sets.Set
    defs/n2 Number
    [defs/defmulti
     defs/manual-java
     defs/dynafun
     defs/no-hierarchy
     defs/signature-dispatch-value
     defs/non-volatile-cache
     defs/hashmap-tables])
#_(defs/bench 
    defs/r3 Object
    defs/n3 Number
    [defs/defmulti
     defs/manual-java
     defs/dynafun
     defs/no-hierarchy
     defs/signature-dispatch-value
     defs/non-volatile-cache
     defs/hashmap-tables])
#_(defs/bench 
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
