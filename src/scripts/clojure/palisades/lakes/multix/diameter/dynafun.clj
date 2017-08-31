(set! *warn-on-reflection* true)
(set! *unchecked-math* :warn-on-boxed)
;;----------------------------------------------------------------
(ns palisades.lakes.multix.diameter.dynafun
  {:author "palisades dot lakes at gmail dot com"
   :since "2017-08-30"
   :version "2017-08-30"}
  (:require [palisades.lakes.bench.prng :as prng]
            [palisades.lakes.bench.core :as bench]
            [palisades.lakes.multix.diameter.defs :as defs]))
;;----------------------------------------------------------------
(bench/profile [prng/objects defs/r7] 
               [defs/dynafun]
               {:n (* 4 1024 1024)
                :samples 1024})
;;----------------------------------------------------------------
(shutdown-agents)
#_(System/exit 0)
