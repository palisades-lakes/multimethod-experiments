(set! *warn-on-reflection* true)
(set! *unchecked-math* :warn-on-boxed)
;;----------------------------------------------------------------
(ns palisades.lakes.multix.diameter.bench
  "Use criterium for alternative multimethod implementations."
  {:author "palisades dot lakes at gmail dot com"
   :since "2017-05-29"
   :version "2017-08-25"}
  (:require [palisades.lakes.bench.prng :as prng]
            [palisades.lakes.bench.generators :as g]
            [palisades.lakes.bench.core :as bench]
            [palisades.lakes.multix.diameter.defs :as defs]))
;;----------------------------------------------------------------
(let [n (* 1 4 1024 1024)]
  (bench/bench 
    [g/IntegerIntervals (g/integer-interval defs/uint)] 
    [defs/ii-static
     #_defs/ii-virtual
     #_defs/s-static
     #_defs/s-interface
     #_defs/o-lookup])
  #_(bench/bench 
      [g/Sets r2] 
      [defs/s-static
       defs/s-interface
       defs/o-lookup])
  #_(bench/bench 
      [g/Sets r3] 
      [defs/s-static
       defs/s-interface
       defs/o-lookup])
  #_(bench/bench 
      [g/Sets r7] 
      [defs/s-static
       defs/s-interface
       defs/o-lookup]))
;;----------------------------------------------------------------
(shutdown-agents)
(System/exit 0)
