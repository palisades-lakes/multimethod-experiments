(set! *warn-on-reflection* true)
(set! *unchecked-math* :warn-on-boxed)
;;----------------------------------------------------------------
(ns palisades.lakes.multix.contains.defs
  
  {:doc "Benchmarks for multiple dispatch alternatives."
   :author "palisades dot lakes at gmail dot com"
   :since "2017-05-29"
   :version "2017-08-31"}
  
  (:refer-clojure :exclude [defmulti])
  
  (:require [palisades.lakes.bench.prng :as prng]
            [palisades.lakes.bench.generators :as g]
            [palisades.lakes.bench.core :as bench]
            [palisades.lakes.multix.sets.multi :as multi]
            [palisades.lakes.multix.sets.hashmaps :as hashmaps]
            [palisades.lakes.multix.sets.nohierarchy :as nohierarchy]
            [palisades.lakes.multix.sets.nonvolatile :as nonvolatile]
            [palisades.lakes.multix.sets.signatures :as signatures]
            [palisades.lakes.multix.sets.dynafun :as dynafun]
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
(defn invokestaticPrimitive ^long [^"[Lpalisades.lakes.bench.java.sets.IntegerInterval;" s0 
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
;; macro for counting loop instead of function,
;; since some of the calls are to java methods and not functions, 
;; and in any case would force dynamic function call rather than 
;; allowing static linking.

(defmacro defcounter [benchname fname ]
  (let [s0 (gensym "Sets") 
        s1 (gensym "elements")
        args [(with-meta s0 {:tag 'objects})
              (with-meta s1 {:tag 'objects})]
        args (with-meta args {:tag 'long})]
    #_(binding [*print-meta* true] (pp/pprint args))
    `(defn ~benchname ~args
       (let [n# (int (min (alength ~s0) (alength ~s1)))]
         (loop [i# (int 0)
                total# (long 0)]
           (cond (>= i# n#) (long total#)
                 (~fname (aget ~s0 i#) (aget ~s1 i#)) 
                 (recur (inc i#) (inc total#))
                 :else (recur (inc i#) total#)))))))
;;----------------------------------------------------------------
(defcounter instanceof Contains/contains)
(defcounter defmulti multi/contains?)
(defcounter hashmaps hashmaps/contains?)
(defcounter nonvolatile nonvolatile/contains?)
(defcounter signatures signatures/contains?)
(defcounter nohierarchy nohierarchy/contains?)
(defcounter dynafun dynafun/contains?)
(defcounter dynarity dynarity/contains?)
;----------------------------------------------------------------
