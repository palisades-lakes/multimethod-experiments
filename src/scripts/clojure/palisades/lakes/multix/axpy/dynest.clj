(set! *warn-on-reflection* true)
(set! *unchecked-math* :warn-on-boxed)
;;----------------------------------------------------------------
(ns palisades.lakes.multix.axpy.dynest
  {:author "palisades dot lakes at gmail dot com"
   :since "2017-08-30"
   :version "2017-08-30"}
  (:require [palisades.lakes.bench.prng :as prng]
            [palisades.lakes.bench.core :as bench]
            [palisades.lakes.multix.axpy.defs :as defs]))
;;----------------------------------------------------------------
(bench/profile [prng/objects defs/m22 
                prng/objects defs/v2 
                prng/objects defs/v2] 
               [defs/dynest]
               {:n (* 4 1024 1024)
                :samples 1024})
;;----------------------------------------------------------------
(shutdown-agents)
#_(System/exit 0)
