(set! *warn-on-reflection* true)
(set! *unchecked-math* :warn-on-boxed)
;;----------------------------------------------------------------
(ns palisades.lakes.multix.diameter.bench
  "Use criterium for alternative multimethod implementations."
  {:author "palisades dot lakes at gmail dot com"
   :since "2017-05-29"
   :version "2017-08-24"}
  (:require [palisades.lakes.bench.random.prng :as prng]
            [palisades.lakes.bench.random.generators :as g]
            [palisades.lakes.multix.contains.defs :as defs]))
;;----------------------------------------------------------------
;; baselines: args always IntegerInterval, Integer
(defs/bench 
    g/IntegerIntervals (g/integer-interval defs/uint)
    [defs/iiint-static
     defs/iiint-virtual
     defs/sint-static
     defs/sint-interface])
;;----------------------------------------------------------------
(shutdown-agents)
(System/exit 0)
