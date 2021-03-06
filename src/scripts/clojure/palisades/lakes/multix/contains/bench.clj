(set! *warn-on-reflection* true)
(set! *unchecked-math* :warn-on-boxed)
;;----------------------------------------------------------------
(ns palisades.lakes.multix.contains.bench
  "Use criterium for alternative multimethod implementations."
  {:author "palisades dot lakes at gmail dot com"
   :since "2017-05-29"
   :version "2017-10-11"}
  (:require [palisades.lakes.bench.prng :as prng]
            [palisades.lakes.bench.generators :as g]
            [palisades.lakes.bench.core :as bench]
            [palisades.lakes.multix.contains.defs :as defs]))
;;----------------------------------------------------------------
(def options {} #_{:n 1024 :samples 4})
;; baselines: args always IntegerInterval, Integer
(bench/bench 
   [g/IntegerIntervals defs/ii
    prng/ints defs/uint]
   [defs/invokestaticPrimitive
    defs/invokevirtualPrimitive
    defs/invokeinterfacePrimitive]
   options)
(bench/bench 
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
(bench/bench 
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
(bench/bench 
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
(bench/bench 
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
