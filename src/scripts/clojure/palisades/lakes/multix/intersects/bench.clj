(set! *warn-on-reflection* true)
(set! *unchecked-math* :warn-on-boxed)
;;----------------------------------------------------------------
(ns palisades.lakes.multix.intersects.bench
  
  "Use criterium for alternative multimethod implementations."
  {:author "palisades dot lakes at gmail dot com"
   :since "2017-05-29"
   :version "2017-08-27"}
  
  (:require [palisades.lakes.bench.prng :as prng]
            [palisades.lakes.bench.generators :as g]
            [palisades.lakes.bench.core :as bench]
            [palisades.lakes.multix.intersects.defs :as defs]))
;;----------------------------------------------------------------
;; baselines: both args always IntegerInterval
(bench/bench 
  [g/IntegerIntervals defs/ii
   g/IntegerIntervals defs/ii]
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
;; 50% probability of repeat same method, 
;; 1st arg always IntegerInterval
;; 2nd randomly IntegerInterval or DoubleInterval
(bench/bench 
  [g/IntegerIntervals defs/ii
   g/Sets defs/r2]
  [defs/invokevirtual
   defs/invokeinterface
   defs/defmulti
   defs/if-then-else-instanceof
   defs/dynafun
   defs/no-hierarchy
   defs/signature-dispatch-value
   defs/non-volatile-cache
   defs/hashmap-tables])
;; 1/9 probability of same method
;; 1st and 2nd args randomly from IntegerInterval, 
;; DoubleInterval and SingletonSet
(bench/bench 
  [prng/objects defs/r3
   prng/objects defs/r3]
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
