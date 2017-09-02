(set! *warn-on-reflection* true)
(set! *unchecked-math* :warn-on-boxed)
;;----------------------------------------------------------------
(ns palisades.lakes.multix.contains.defs
  
  {:doc "Benchmarks for multiple dispatch alternatives."
   :author "palisades dot lakes at gmail dot com"
   :since "2017-05-29"
   :version "2017-09-02"}
  
  (:refer-clojure :exclude [defmulti])
  
  (:require [palisades.lakes.bench.prng :as prng]
            [palisades.lakes.bench.generators :as g]
            [palisades.lakes.bench.core :as bench]
            [palisades.lakes.multix.sets.instancefn :as instancefn]
            [palisades.lakes.multix.sets.multi :as multi]
            [palisades.lakes.multix.sets.hashmaps :as hashmaps]
            [palisades.lakes.multix.sets.nohierarchy :as nohierarchy]
            [palisades.lakes.multix.sets.signatures :as signatures]
            [palisades.lakes.multix.sets.dynafun :as dynafun]
            [palisades.lakes.multix.sets.dynalin :as dynalin]
            [palisades.lakes.multix.sets.dynarity :as dynarity])
  
  (:import [clojure.lang IFn IFn$L] 
           [org.apache.commons.rng UniformRandomProvider]
           [org.apache.commons.rng.sampling CollectionSampler]
           [palisades.lakes.bench.java.sets Contains]))
;;----------------------------------------------------------------
(defn uniformDoubleOrInteger
  ^clojure.lang.IFn [^double umin
                     ^double umax
                     ^UniformRandomProvider urp]
  (let [lmin (long umin)
        lmax (long umax)
        ^CollectionSampler cs 
        (CollectionSampler. 
          urp 
          [(prng/uniformDouble umin umax urp)
           (prng/uniformInteger lmin lmax urp)])]
    (fn uniformDoubleOrInteger ^Number [] ((.sample cs)))))
;;----------------------------------------------------------------
(let [urp (prng/uniform-random-provider "seeds/Well44497b-2017-06-13.edn")
      umin -100.0
      umax 100.0]
  (def ^IFn$L uint (prng/uniform-int -100 100 urp))
  (def ^IFn ii (g/integer-interval uint))
  (def ^IFn r2 (g/interval-of-2 umin umax urp))
  (def ^IFn r3 (g/set-of-3 umin umax urp))
  (def ^IFn r7 (g/set-of-7 umin umax urp))
  (def ^IFn uInteger (prng/uniformInteger umin umax urp))
  (def ^IFn n2 (uniformDoubleOrInteger umin umax urp))
  (def ^IFn n6 (prng/uniformNumber umin umax urp)))
;;----------------------------------------------------------------
(defn invokestaticPrimitive 
  ^long [^"[Lpalisades.lakes.bench.java.sets.IntegerInterval;" s0 
         ^"[I" s1]
  (Contains/countStatic s0 s1)) 

(defn invokestatic 
  ^long [^"[Lpalisades.lakes.bench.java.sets.IntegerInterval;" s0 
         ^"[Ljava.lang.Integer;" s1]
  (Contains/countStatic s0 s1))

(defn invokevirtualPrimitive 
  ^long [^"[Lpalisades.lakes.bench.java.sets.IntegerInterval;" s0 
         ^"[I" s1]
  (Contains/countVirtual s0 s1)) 

(defn invokevirtual 
  ^long [^"[Lpalisades.lakes.bench.java.sets.IntegerInterval;" s0 
         ^"[Ljava.lang.Integer;" s1]
  (Contains/countVirtual s0 s1))

(defn invokeinterfacePrimitive 
  ^long [^"[Lpalisades.lakes.bench.java.sets.Set;" s0 
         ^"[I" s1]
  (Contains/countInterface s0 s1))

(defn invokeinterface 
  ^long [^"[Lpalisades.lakes.bench.java.sets.Set;" s0 
         ^"[Ljava.lang.Number;" s1]
  (Contains/countInterface s0 s1))
;;----------------------------------------------------------------
(bench/defcounter instanceof Contains/contains)
(bench/defcounter instancefn instancefn/contains?)
(bench/defcounter defmulti multi/contains?)
(bench/defcounter hashmaps hashmaps/contains?)
(bench/defcounter signatures signatures/contains?)
(bench/defcounter nohierarchy nohierarchy/contains?)
(bench/defcounter dynafun dynafun/contains?)
(bench/defcounter dynalin dynalin/contains?)
(bench/defcounter dynarity dynarity/contains?)
;----------------------------------------------------------------
