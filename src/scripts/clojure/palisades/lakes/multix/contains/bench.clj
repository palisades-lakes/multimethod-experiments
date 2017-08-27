(set! *warn-on-reflection* true)
(set! *unchecked-math* :warn-on-boxed)
;;----------------------------------------------------------------
(ns palisades.lakes.multix.contains.bench
  "Use criterium for alternative multimethod implementations."
  {:author "palisades dot lakes at gmail dot com"
   :since "2017-05-29"
   :version "2017-08-25"}
  (:require [palisades.lakes.bench.prng :as prng]
            [palisades.lakes.bench.generators :as g]
            [palisades.lakes.bench.core :as bench]
            [palisades.lakes.multix.contains.defs :as defs]))
;;----------------------------------------------------------------
;; baselines: args always IntegerInterval, Integer
(bench/bench 
  [g/IntegerIntervals defs/ii
   prng/ints defs/uint]
  [defs/invokestaticPrimitive
   defs/invokevirtualPrimitive
   defs/invokeinterfacePrimitive])
(bench/bench 
  [g/IntegerIntervals defs/ii
   prng/IntegerArray defs/uInteger]
  [defs/invokestatic
   defs/invokevirtual
   defs/invokeinterface
   defs/defmulti
   defs/if-then-else-instanceof
   defs/dynafun
   defs/no-hierarchy
   defs/signature-dispatch-value
   defs/non-volatile-cache
   defs/hashmap-tables])
(bench/bench 
  [g/Sets defs/r2
   prng/NumberArray defs/n2]
  [defs/invokeinterface
   defs/defmulti
   defs/if-then-else-instanceof
   defs/dynafun
   defs/no-hierarchy
   defs/signature-dispatch-value
   defs/non-volatile-cache
   defs/hashmap-tables])
(bench/bench 
  [prng/objects defs/r3
   prng/NumberArray defs/n2]
  [defs/defmulti
   defs/if-then-else-instanceof
   defs/dynafun
   defs/no-hierarchy
   defs/signature-dispatch-value
   defs/non-volatile-cache
   defs/hashmap-tables])
(bench/bench 
  [prng/objects defs/r7
   prng/objects defs/n6]
  [defs/defmulti
   defs/if-then-else-instanceof
   defs/dynafun
   defs/no-hierarchy
   defs/signature-dispatch-value
   defs/non-volatile-cache
   defs/hashmap-tables])
;;----------------------------------------------------------------
#_(shutdown-agents)
#_(System/exit 0)
