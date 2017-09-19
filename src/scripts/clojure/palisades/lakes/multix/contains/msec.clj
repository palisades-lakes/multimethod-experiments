(set! *warn-on-reflection* true)
(set! *unchecked-math* :warn-on-boxed)
;;----------------------------------------------------------------
(ns palisades.lakes.multix.contains.msec
  "Use criterium for alternative multimethod implementations."
  {:author "palisades dot lakes at gmail dot com"
   :since "2017-09-18"
   :version "2017-09-18"}
  (:require [palisades.lakes.bench.prng :as prng]
            [palisades.lakes.bench.generators :as g]
            [palisades.lakes.bench.core :as bench]
            [palisades.lakes.multix.contains.defs :as defs]))
;;----------------------------------------------------------------
(def options {:n (* 1024 1024) :samples (* 16 1024)})
;; baselines: args always IntegerInterval, Integer
(bench/profile 
  [g/IntegerIntervals defs/ii
   prng/ints defs/uint]
  [defs/invokestaticPrimitive
   defs/invokevirtualPrimitive
   defs/invokeinterfacePrimitive]
  options)
(bench/profile 
  [g/IntegerIntervals defs/ii
   prng/IntegerArray defs/uInteger]
  [defs/invokestatic
   defs/invokevirtual
   defs/invokeinterface
   defs/protocols
   defs/instanceof
   defs/instancefn
   defs/defmulti
   defs/hashmaps
   defs/signatures
   defs/nohierarchy
   defs/dynafun 
   defs/dynamap]
  options)
(bench/profile 
  [g/Sets defs/r2
   prng/NumberArray defs/n2]
  [defs/invokeinterface
   defs/protocols
   defs/instanceof
   defs/instancefn
   defs/defmulti
   defs/hashmaps
   defs/signatures
   defs/nohierarchy
   defs/dynafun 
   defs/dynamap]
  options)
(bench/profile 
  [prng/objects defs/r3
   prng/NumberArray defs/n2]
  [defs/protocols
   defs/instanceof
   defs/instancefn
   defs/defmulti
   defs/hashmaps
   defs/signatures
   defs/nohierarchy
   defs/dynafun 
   defs/dynamap]
  options)
(bench/profile 
  [prng/objects defs/r7
   prng/objects defs/n6]
  [defs/protocols
   defs/instanceof
   defs/instancefn
   defs/defmulti
   defs/hashmaps
   defs/signatures
   defs/nohierarchy
   defs/dynafun 
   defs/dynamap]
  options)
;;----------------------------------------------------------------
#_(shutdown-agents)
#_(System/exit 0)
