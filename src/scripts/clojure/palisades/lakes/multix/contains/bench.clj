(set! *warn-on-reflection* true)
(set! *unchecked-math* :warn-on-boxed)
;;----------------------------------------------------------------
(ns palisades.lakes.multix.contains.bench
  "Use criterium for alternative multimethod implementations."
  {:author "palisades dot lakes at gmail dot com"
   :since "2017-05-29"
   :version "2017-09-02"}
  (:require [palisades.lakes.bench.prng :as prng]
            [palisades.lakes.bench.generators :as g]
            [palisades.lakes.bench.core :as bench]
            [palisades.lakes.multix.contains.defs :as defs]))
;;----------------------------------------------------------------
;; baselines: args always IntegerInterval, Integer
(bench/bench 
  [g/IntegerIntervals defs/ii
   prng/ints defs/uint]
  [defs/invokestaticPrimitive
   defs/invokevirtualPrimitive
   defs/invokeinterfacePrimitive])
(bench/bench 
  [g/IntegerIntervals defs/ii
   prng/IntegerArray defs/uInteger]
  [#_defs/invokestatic
   #_defs/invokevirtual
   #_defs/invokeinterface
   #_defs/instanceof
   defs/instancefn
   #_defs/defmulti
   #_defs/hashmaps
   #_defs/signatures
   defs/nohierarchy
   defs/dynafun
   defs/dynalin
   #_defs/dynarity])
(bench/bench 
  [g/Sets defs/r2
   prng/NumberArray defs/n2]
  [#_defs/invokeinterface
   #_defs/instanceof
   defs/instancefn
   #_defs/defmulti
   #_defs/hashmaps
   #_defs/signatures
   defs/nohierarchy
   defs/dynafun
   defs/dynalin
   #_defs/dynarity])
(bench/bench 
  [prng/objects defs/r3
   prng/NumberArray defs/n2]
  [#_defs/instanceof
   defs/instancefn
   #_defs/defmulti
   #_defs/hashmaps
   #_defs/signatures
   defs/nohierarchy
   defs/dynafun
   defs/dynalin
   #_defs/dynarity])
(bench/bench 
  [prng/objects defs/r7
   prng/objects defs/n6]
  [#_defs/instanceof
   defs/instancefn
   #_defs/defmulti
   #_defs/hashmaps
   #_defs/signatures
   defs/nohierarchy
   defs/dynafun
   defs/dynalin
   #_defs/dynarity])
;;----------------------------------------------------------------
#_(shutdown-agents)
#_(System/exit 0)
