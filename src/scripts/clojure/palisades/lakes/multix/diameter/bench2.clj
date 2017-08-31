(set! *warn-on-reflection* true)
(set! *unchecked-math* :warn-on-boxed)
;;----------------------------------------------------------------
(ns palisades.lakes.multix.diameter.bench2
  "Use criterium for alternative multimethod implementations."
  {:author "palisades dot lakes at gmail dot com"
   :since "2017-08-30"
   :version "2017-08-30"}
  (:require [palisades.lakes.bench.prng :as prng]
            [palisades.lakes.bench.generators :as g]
            [palisades.lakes.bench.core :as bench]
            [palisades.lakes.multix.diameter.defs :as defs]))
;;----------------------------------------------------------------
(bench/bench 
  [prng/objects defs/r7] 
  [defs/instanceof
   defs/nohierarchy
   defs/dynafun
   defs/instanceof
   defs/nohierarchy
   defs/dynafun]
  {:n (* 1024 1024)
   :samples 1024})
;;----------------------------------------------------------------
(shutdown-agents)
(System/exit 0)
