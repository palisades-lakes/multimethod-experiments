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
            [palisades.lakes.multix.diameter.defs :as defs]))
;;----------------------------------------------------------------
(defs/bench 
    [g/IntegerIntervals (g/integer-interval defs/uint)] 
    [defs/ii-static
     #_defs/ii-virtual
     #_defs/s-static
     #_defs/s-interface
     #_defs/o-lookup])
#_(defs/bench 
    [g/Sets r2] 
    [defs/s-static
     defs/s-interface
     defs/o-lookup])
#_(defs/bench 
    [g/Sets r3] 
    [defs/s-static
     defs/s-interface
     defs/o-lookup])
#_(defs/bench 
    [g/Sets r7] 
    [defs/s-static
     defs/s-interface
     defs/o-lookup])
;;----------------------------------------------------------------
(shutdown-agents)
(System/exit 0)
