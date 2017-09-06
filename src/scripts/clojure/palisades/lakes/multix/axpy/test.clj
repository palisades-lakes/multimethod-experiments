(set! *warn-on-reflection* true)
(set! *unchecked-math* :warn-on-boxed)
;;----------------------------------------------------------------
(ns palisades.lakes.multix.axpy.test
  
  "Use criterium for alternative multimethod implementations."
  {:author "palisades dot lakes at gmail dot com"
   :since "2017-08-26"
   :version "2017-09-05"}
  
  (:require [palisades.lakes.bench.prng :as prng]
            [palisades.lakes.bench.generators :as g]
            [palisades.lakes.bench.core :as bench]
            [palisades.lakes.multix.axpy.defs :as defs]))
;;----------------------------------------------------------------
(def options {:n 1024 :samples 4})
;; array element types [D22 D2 D2]
(bench/bench 
  [g/d22s defs/d22 
   g/d2s defs/d2 
   g/d2s defs/d2]
  [defs/protocols
   defs/dynafun defs/dynamap]
  options)
;; array element types [LinearFunction Vector Vector]
;; (* 1/6 1/6 1/6): 1/216 change of repeated calls
(bench/bench 
  [g/linearfunctions defs/m22 
   g/vectors defs/v2 
   g/vectors defs/v2]
  [defs/protocols
   defs/dynafun defs/dynamap]
  options)
;; array element types [Object Object Object]
;; (* 1/6 1/6 1/6): 1/216 change of repeated calls
(bench/bench 
  [prng/objects defs/m22 
   prng/objects defs/v2 
   prng/objects defs/v2]
  [defs/protocols
   defs/dynafun defs/dynamap]
  options)
;;----------------------------------------------------------------
#_(shutdown-agents)
#_(System/exit 0)
