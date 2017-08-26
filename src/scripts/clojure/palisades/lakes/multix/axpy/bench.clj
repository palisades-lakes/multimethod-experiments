(set! *warn-on-reflection* true)
(set! *unchecked-math* :warn-on-boxed)
;;----------------------------------------------------------------
(ns palisades.lakes.multix.axpy.bench
  
  "Use criterium for alternative multimethod implementations."
  {:author "palisades dot lakes at gmail dot com"
   :since "2017-08-26"
   :version "2017-08-26"}
  
  (:require [palisades.lakes.bench.generators :as g]
            [palisades.lakes.bench.core :as bench]
            [palisades.lakes.multix.axpy.defs :as defs]))
;;----------------------------------------------------------------
(let [n (* #_4 #_1024 1024)]
  ;; array element types [D22 D2 D2]
  (bench/bench 
    [g/d22s defs/d22 g/d2s defs/d2 g/d2s defs/d2]
    [defs/defmulti defs/dynafun defs/no-hierarchy]
    n)
  ;; array element types [LinearFunction Vector Vector]
  ;; (* 1/6 1/6 1/6): 1/216 change of repeated calls
  (bench/bench 
    [g/v22s defs/v22 g/v2s defs/v2 g/v2s defs/v2]
    [defs/defmulti defs/dynafun defs/no-hierarchy]
    n)
  ;; array element types [Object Object Object]
  ;; (* 1/6 1/6 1/6): 1/216 change of repeated calls
  (bench/bench 
    [g/objects defs/v22 g/objects defs/v2 g/objects defs/v2]
    [defs/defmulti defs/dynafun defs/no-hierarchy]
    n)
;;----------------------------------------------------------------
#_(shutdown-agents)
#_(System/exit 0)
