(set! *warn-on-reflection* true)
(set! *unchecked-math* :warn-on-boxed)
;;----------------------------------------------------------------
(ns palisades.lakes.multix.diameter.bench
  "Use criterium for alternative multimethod implementations."
  {:author "palisades dot lakes at gmail dot com"
   :since "2017-05-29"
   :version "2017-09-03"}
  (:require [palisades.lakes.bench.prng :as prng]
            [palisades.lakes.bench.generators :as g]
            [palisades.lakes.bench.core :as bench]
            [palisades.lakes.multix.diameter.defs :as defs]))
;;----------------------------------------------------------------
(bench/bench 
  [g/IntegerIntervals (g/integer-interval defs/uint)] 
  [defs/dynest
   #_defs/invokestatic
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
  [g/Sets defs/r2] 
  [defs/dynest
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
  [prng/objects defs/r3] 
  [defs/dynest
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
  [prng/objects defs/r7] 
  [defs/dynest
   #_defs/instanceof
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
