(set! *warn-on-reflection* true)
(set! *unchecked-math* :warn-on-boxed)
;;----------------------------------------------------------------
(ns palisades.lakes.multix.contains.test
  "Use criterium for alternative multimethod implementations."
  {:author "palisades dot lakes at gmail dot com"
   :since "2017-05-29"
   :version "2017-09-05"}
  (:require [palisades.lakes.bench.prng :as prng]
            [palisades.lakes.bench.generators :as g]
            [palisades.lakes.bench.core :as bench]
            [palisades.lakes.multix.contains.defs :as defs]))
;;----------------------------------------------------------------
(def options {:n 1024 :samples 4})
;; baselines: args always IntegerInterval, Integer
#_(bench/bench 
   [g/IntegerIntervals defs/ii
    prng/ints defs/uint]
   [defs/protocols]
   options)
(bench/bench 
  [g/IntegerIntervals defs/ii
   prng/IntegerArray defs/uInteger]
  [defs/protocols
   defs/dynafun defs/dynamap]
  options)
(bench/bench 
  [g/Sets defs/r2
   prng/NumberArray defs/n2]
  [defs/protocols
   defs/dynafun defs/dynamap]
  options)
(bench/bench 
  [prng/objects defs/r3
   prng/NumberArray defs/n2]
  [defs/protocols
   defs/dynafun defs/dynamap]
  options)
(bench/bench 
  [prng/objects defs/r7
   prng/objects defs/n6]
  [defs/protocols
   defs/dynafun defs/dynamap]
  options)
;;----------------------------------------------------------------
#_(shutdown-agents)
#_(System/exit 0)
