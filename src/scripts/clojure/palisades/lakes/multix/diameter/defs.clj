(set! *warn-on-reflection* true)
(set! *unchecked-math* :warn-on-boxed)
;;----------------------------------------------------------------
(ns palisades.lakes.multix.diameter.defs
  
  {:doc "Benchmarks for multiple dispatch alternatives."
   :author "palisades dot lakes at gmail dot com"
   :since "2017-05-29"
   :version "2017-09-03"}
  
  (:refer-clojure :exclude [defmulti])
  
  (:require [clojure.string :as s]
            [clojure.pprint :as pp]
            [palisades.lakes.bench.prng :as prng]
            [palisades.lakes.bench.generators :as g]
            [palisades.lakes.bench.core :as bench]
            [palisades.lakes.multix.sets.instancefn :as instancefn]
            [palisades.lakes.multix.sets.multi :as multi]
            [palisades.lakes.multix.sets.hashmaps :as hashmaps]
            [palisades.lakes.multix.sets.nohierarchy :as nohierarchy]
            [palisades.lakes.multix.sets.signatures :as signatures]
            [palisades.lakes.multix.sets.dynafun :as dynafun]
            [palisades.lakes.multix.sets.dynalin :as dynalin]
            [palisades.lakes.multix.sets.dynest :as dynest]
            [palisades.lakes.multix.sets.dynarity :as dynarity])
  
  (:import [clojure.lang IFn IFn$L] 
           [org.apache.commons.rng UniformRandomProvider]
           [org.apache.commons.rng.sampling CollectionSampler]
           [palisades.lakes.bench.java.sets Diameter]))
;;----------------------------------------------------------------
(let [urp (prng/uniform-random-provider "seeds/Well44497b-2017-06-13.edn")
      umin -100.0
      umax 100.0]
  (def ^IFn$L uint (prng/uniform-int -100 100 urp))
  (def ^IFn r2 (g/interval-of-2 umin umax urp))
  (def ^IFn r3 (g/set-of-3 umin umax urp))
  (def ^IFn r7 (g/set-of-7 umin umax urp)))
;;----------------------------------------------------------------
(defn invokestatic ^double [^"[Lpalisades.lakes.bench.java.sets.IntegerInterval;" data]
  (Diameter/maxStatic data)) 
(defn invokevirtual ^double [^"[Lpalisades.lakes.bench.java.sets.IntegerInterval;" data]
  (Diameter/maxVirtual data)) 
(defn invokeinterface ^double [^"[Lpalisades.lakes.bench.java.sets.Set;" data]
  (Diameter/maxInterface data)) 
;;----------------------------------------------------------------
(bench/defmax instanceof Diameter/diameter)
(bench/defmax instancefn instancefn/diameter)
(bench/defmax defmulti multi/diameter)
(bench/defmax hashmaps hashmaps/diameter)
(bench/defmax signatures signatures/diameter)
(bench/defmax nohierarchy nohierarchy/diameter)
(bench/defmax dynafun dynafun/diameter)
(bench/defmax dynalin dynalin/diameter)
(bench/defmax dynest dynest/diameter)
(bench/defmax dynarity dynarity/diameter)
;;----------------------------------------------------------------
