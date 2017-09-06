(set! *warn-on-reflection* true)
(set! *unchecked-math* :warn-on-boxed)
;;----------------------------------------------------------------
(ns palisades.lakes.multix.diameter.bench
  "Use criterium for alternative multimethod implementations."
  {:author "palisades dot lakes at gmail dot com"
   :since "2017-05-29"
   :version "2017-09-05"}
  (:require [palisades.lakes.bench.prng :as prng]
            [palisades.lakes.bench.generators :as g]
            [palisades.lakes.bench.core :as bench]
            [palisades.lakes.multix.diameter.defs :as defs]))
;;----------------------------------------------------------------
(def options {} #_{:n 1024 :samples 4})
(bench/bench 
  [g/IntegerIntervals (g/integer-interval defs/uint)] 
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
  [g/Sets defs/r2] 
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
  [prng/objects defs/r3] 
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
  [prng/objects defs/r7] 
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
