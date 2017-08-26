(set! *warn-on-reflection* true)
(set! *unchecked-math* :warn-on-boxed)
;;----------------------------------------------------------------
(ns palisades.lakes.multix.diameter.bench
  "Use criterium for alternative multimethod implementations."
  {:author "palisades dot lakes at gmail dot com"
   :since "2017-05-29"
   :version "2017-08-25"}
  (:require [palisades.lakes.bench.prng :as prng]
            [palisades.lakes.bench.generators :as g]
            [palisades.lakes.bench.core :as bench]
            [palisades.lakes.multix.diameter.defs :as defs]))
;;----------------------------------------------------------------
(let [n (* 1 4 1024 1024)]
  (bench/bench 
    [g/IntegerIntervals (g/integer-interval defs/uint)] 
    [defs/defmulti
     defs/ii-static
     defs/ii-virtual
     defs/s-interface
     defs/o-lookup
     defs/dynafun
     defs/no-hierarchy
     defs/signature-dispatch-value
     defs/non-volatile-cache
     defs/hashmap-tables
     defs/defmulti]
    n)
  (bench/bench 
    [g/Sets defs/r2] 
    [defs/s-interface
     defs/o-lookup
     defs/dynafun
     defs/no-hierarchy
     defs/signature-dispatch-value
     defs/non-volatile-cache
     defs/hashmap-tables
     defs/defmulti]
    n)
  (bench/bench 
    [prng/objects defs/r3] 
    [defs/o-lookup
     defs/dynafun
     defs/no-hierarchy
     defs/signature-dispatch-value
     defs/non-volatile-cache
     defs/hashmap-tables
     defs/defmulti]
    n)
  (bench/bench 
    [prng/objects defs/r7] 
    [defs/o-lookup
     defs/dynafun
     defs/no-hierarchy
     defs/signature-dispatch-value
     defs/non-volatile-cache
     defs/hashmap-tables
     defs/defmulti]
    n))
;;----------------------------------------------------------------
#_(shutdown-agents)
#_(System/exit 0)
