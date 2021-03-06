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
  [#_defs/instanceof
   defs/instancefn
   defs/nohierarchy
   defs/dynafun
   defs/dynalin
   #_defs/instanceof
   #_defs/nohierarchy
   #_defs/dynafun]
  {:samples 256})
;;----------------------------------------------------------------
(shutdown-agents)
(System/exit 0)
