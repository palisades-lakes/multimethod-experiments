(set! *warn-on-reflection* true)
(set! *unchecked-math* :warn-on-boxed)
;;----------------------------------------------------------------
(ns palisades.lakes.multix.intersects.defs
  
  {:doc "Benchmarks for multiple dispatch alternatives."
   :author "palisades dot lakes at gmail dot com"
   :since "2017-05-29"
   :version "2017-09-05"}
  
  (:refer-clojure :exclude [defmulti])
  
  (:require [palisades.lakes.bench.prng :as prng]
            [palisades.lakes.bench.generators :as g]
            [palisades.lakes.bench.core :as bench]
            [palisades.lakes.multix.sets.protocols :as protocols]
            [palisades.lakes.multix.sets.instancefn :as instancefn]
            [palisades.lakes.multix.sets.signature :as signature]
            [palisades.lakes.multix.sets.multi :as multi]
            [palisades.lakes.multix.sets.hashmaps :as hashmaps]
            [palisades.lakes.multix.sets.nohierarchy :as nohierarchy]
            [palisades.lakes.multix.sets.signatures :as signatures]
            [palisades.lakes.multix.sets.dynafun :as dynafun]
            [palisades.lakes.multix.sets.dynamap :as dynamap]
            [palisades.lakes.multix.sets.dynalin :as dynalin]
            [palisades.lakes.multix.sets.dynest :as dynest])
  
  (:import [clojure.lang IFn IFn$L] 
           [palisades.lakes.bench.java.sets 
            DoubleInterval IntegerInterval Intersects Set Sets]))
;;----------------------------------------------------------------
(let [urp (prng/uniform-random-provider "seeds/Well44497b-2017-07-25.edn")
      umin -100.0
      umax 100.0]
  (def ^IFn$L uint (prng/uniform-int -100 100 urp))
  (def ^IFn ii (g/integer-interval uint))
  (def ^IFn r2 (g/interval-of-2 umin umax urp))
  (def ^IFn r3 (g/set-of-3 umin umax urp))
  (def ^IFn r7 (g/set-of-7 umin umax urp)))
;;----------------------------------------------------------------
(defn invokestatic ^long [^"[Lpalisades.lakes.bench.java.sets.IntegerInterval;" s0 
                          ^"[Lpalisades.lakes.bench.java.sets.IntegerInterval;" s1]
  (Intersects/countStatic s0 s1)) 
;;----------------------------------------------------------------
(defn invokevirtual ^long [^"[Lpalisades.lakes.bench.java.sets.IntegerInterval;" s0 
                           ^"[Lpalisades.lakes.bench.java.sets.IntegerInterval;" s1]
  (Intersects/countVirtual s0 s1))
;;----------------------------------------------------------------
(defn invokeinterface ^long [^"[Lpalisades.lakes.bench.java.sets.Set;" s0 
                             ^"[Lpalisades.lakes.bench.java.sets.IntegerInterval;" s1]
  (Intersects/countInterface s0 s1))
;;----------------------------------------------------------------
(bench/defcounter protocols protocols/intersects?)
(bench/defcounter instanceof Intersects/intersects)
(bench/defcounter instancefn instancefn/intersects?)
(bench/defcounter defmulti multi/intersects?)
(bench/defcounter hashmaps hashmaps/intersects?)
(bench/defcounter nohierarchy nohierarchy/intersects?)
(bench/defcounter signatures signatures/intersects?)
(bench/defcounter dynafun dynafun/intersects?)
(bench/defcounter dynalin dynalin/intersects?)
(bench/defcounter dynest dynest/intersects?)
;;----------------------------------------------------------------
