(set! *warn-on-reflection* true)
(set! *unchecked-math* :warn-on-boxed)
;;----------------------------------------------------------------
(ns palisades.lakes.multix.contains.bench
  "Use criterium for alternative multimethod implementations."
  {:author "palisades dot lakes at gmail dot com"
   :since "2017-05-29"
   :version "2017-08-25"}
  (:require [palisades.lakes.bench.prng :as prng]
            [palisades.lakes.bench.generators :as g]
            [palisades.lakes.bench.core :as bench]
            [palisades.lakes.multix.contains.defs :as defs]))
;;----------------------------------------------------------------
(let [n (* 1 4 1024 1024)]
  ;; baselines: args always IntegerInterval, Integer
  (bench/bench 
    [g/IntegerIntervals defs/ii
     prng/ints defs/uint]
    [defs/iiint-static
     defs/iiint-virtual
     defs/sint-static
     defs/sint-interface]
    n)
  (bench/bench 
    [g/IntegerIntervals defs/ii
     prng/IntegerArray defs/uInteger]
    [defs/iiInteger-static
     defs/iiInteger-virtual
     defs/sInteger-static
     defs/sInteger-interface
     defs/oo-static
     defs/manual-java
     defs/dynafun
     defs/defmulti
     defs/no-hierarchy
     defs/signature-dispatch-value
     defs/non-volatile-cache
     defs/hashmap-tables]
    n)
  (bench/bench 
      [g/Sets defs/r2
       prng/NumberArray defs/n2]
      [defs/sInteger-interface
       defs/oo-static
       defs/defmulti
       defs/manual-java
       defs/dynafun
       defs/no-hierarchy
       defs/signature-dispatch-value
       defs/non-volatile-cache
       defs/hashmap-tables]
    n)
  (bench/bench 
      [prng/objects defs/r3
       prng/NumberArray defs/n2]
      [defs/oo-static
       defs/defmulti
       defs/manual-java
       defs/dynafun
       defs/no-hierarchy
       defs/signature-dispatch-value
       defs/non-volatile-cache
       defs/hashmap-tables]
    n)
  (bench/bench 
      [prng/objects defs/r7
       prng/objects defs/n6]
      [defs/oo-static
       defs/defmulti
       defs/manual-java
       defs/dynafun
       defs/no-hierarchy
       defs/signature-dispatch-value
       defs/non-volatile-cache
       defs/hashmap-tables]
    n))
;;----------------------------------------------------------------
(shutdown-agents)
(System/exit 0)
 