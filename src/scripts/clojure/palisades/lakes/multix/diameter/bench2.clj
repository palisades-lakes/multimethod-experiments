(set! *warn-on-reflection* true)
(set! *unchecked-math* :warn-on-boxed)
;;----------------------------------------------------------------
(ns palisades.lakes.multix.diameter.bench2
  "Use criterium for alternative multimethod implementations."
  {:author "palisades dot lakes at gmail dot com"
   :since "2017-08-30"
   :version "2017-08-31"}
  (:require [palisades.lakes.bench.prng :as prng]
            [palisades.lakes.bench.generators :as g]
            [palisades.lakes.bench.core :as bench]
            [palisades.lakes.multix.diameter.defs :as defs]))
;;----------------------------------------------------------------
(bench/bench 
  [g/Sets defs/r2] 
  #_[prng/objects defs/r7] 
  [defs/instanceof
   #_defs/nohierarchy
   defs/dynafun
   defs/dynarity
   #_defs/instanceof
   #_defs/nohierarchy
   #_defs/dynafun
   #_defs/dynarity]
  {:n (* 1024 1024)
   :samples 256})
;;----------------------------------------------------------------
(shutdown-agents)
(System/exit 0)
