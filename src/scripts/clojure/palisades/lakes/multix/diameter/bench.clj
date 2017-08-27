(set! *warn-on-reflection* true)
(set! *unchecked-math* :warn-on-boxed)
;;----------------------------------------------------------------
(ns palisades.lakes.multix.diameter.bench
  "Use criterium for alternative multimethod implementations."
  {:author "palisades dot lakes at gmail dot com"
   :since "2017-05-29"
   :version "2017-08-27"}
  (:require [palisades.lakes.bench.prng :as prng]
            [palisades.lakes.bench.generators :as g]
            [palisades.lakes.bench.core :as bench]
            [palisades.lakes.multix.diameter.defs :as defs]))
;;----------------------------------------------------------------
(bench/bench 
  [g/IntegerIntervals (g/integer-interval defs/uint)] 
  [defs/invokestatic
   defs/invokevirtual
   defs/invokeinterface
   defs/if-then-else-instanceof
   defs/defmulti
   defs/hashmaps
   defs/nonvolatile
   defs/signatures
   defs/nohierarchy
   defs/dynafun])
(bench/bench 
  [g/Sets defs/r2] 
  [defs/invokeinterface
   defs/if-then-else-instanceof
   defs/defmulti
   defs/hashmaps
   defs/nonvolatile
   defs/signatures
   defs/nohierarchy
   defs/dynafun])
(bench/bench 
  [prng/objects defs/r3] 
  [defs/if-then-else-instanceof
   defs/defmulti
   defs/hashmaps
   defs/nonvolatile
   defs/signatures
   defs/nohierarchy
   defs/dynafun])
(bench/bench 
  [prng/objects defs/r7] 
  [defs/if-then-else-instanceof
   defs/defmulti
   defs/hashmaps
   defs/nonvolatile
   defs/signatures
   defs/nohierarchy
   defs/dynafun])
;;----------------------------------------------------------------
#_(shutdown-agents)
#_(System/exit 0)
