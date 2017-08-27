(set! *warn-on-reflection* true)
(set! *unchecked-math* :warn-on-boxed)
;;----------------------------------------------------------------
(ns palisades.lakes.multix.axpy.bench
  
  "Use criterium for alternative multimethod implementations."
  {:author "palisades dot lakes at gmail dot com"
   :since "2017-08-26"
   :version "2017-08-26"}
  
  (:require [palisades.lakes.bench.prng :as prng]
            [palisades.lakes.bench.generators :as g]
            [palisades.lakes.bench.core :as bench]
            [palisades.lakes.multix.axpy.defs :as defs]))
;;----------------------------------------------------------------
;; array element types [D22 D2 D2]
(bench/bench 
  "axpy" ;; could pull from the namespace
  [g/d22s defs/d22 
   g/d2s defs/d2 
   g/d2s defs/d2]
  [#_defs/invokestatic
   defs/invokevirtual
   defs/invokeinterface
   defs/defmulti
   #_defs/if-then-else-instanceof
   defs/dynafun
   defs/no-hierarchy
   defs/signature-dispatch-value
   defs/non-volatile-cache
   defs/hashmap-tables])
;; array element types [LinearFunction Vector Vector]
;; (* 1/6 1/6 1/6): 1/216 change of repeated calls
(bench/bench 
  "axpy"
  [g/linearfunctions defs/m22 
   g/vectors defs/v2 
   g/vectors defs/v2]
  [defs/invokevirtual
   defs/invokeinterface
   defs/defmulti
   #_defs/if-then-else-instanceof
   defs/dynafun
   defs/no-hierarchy
   defs/signature-dispatch-value
   defs/non-volatile-cache
   defs/hashmap-tables])
;; array element types [Object Object Object]
;; (* 1/6 1/6 1/6): 1/216 change of repeated calls
(bench/bench 
  "axpy"
  [prng/objects defs/m22 
   prng/objects defs/v2 
   prng/objects defs/v2]
  [defs/defmulti
   #_defs/if-then-else-instanceof
   defs/dynafun
   defs/no-hierarchy
   defs/signature-dispatch-value
   defs/non-volatile-cache
   defs/hashmap-tables])
;;----------------------------------------------------------------
#_(shutdown-agents)
#_(System/exit 0)
