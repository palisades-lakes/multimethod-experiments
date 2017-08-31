(set! *warn-on-reflection* true)
(set! *unchecked-math* :warn-on-boxed)
;;----------------------------------------------------------------
(ns palisades.lakes.multix.diameter.defs
  
  {:doc "Benchmarks for multiple dispatch alternatives."
   :author "palisades dot lakes at gmail dot com"
   :since "2017-05-29"
   :version "2017-08-31"}
  
  (:refer-clojure :exclude [defmulti])
  
  (:require [clojure.string :as s]
            [clojure.pprint :as pp]
            [palisades.lakes.bench.prng :as prng]
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
;; macro for counting loop instead of function,
;; since some of the calls are to java methods and not functions, 
;; and in any case would force dynamic function call rather than 
;; allowing static linking.

(defmacro defmax [benchname fname]
  (let [s (gensym "Sets") 
        args [(with-meta s {:tag 'objects})]
        args (with-meta args {:tag 'double})]
    `(defn ~benchname ~args
       (let [n# (int (alength ~s))]
         (loop [i# (int 0)
                dmax# Double/NEGATIVE_INFINITY]
           (if (>= i# n#) 
             dmax#
             (recur 
               (inc i#) 
               (Math/max dmax# (double (~fname (aget ~s i#)))))))))))
;;----------------------------------------------------------------
(defmax instanceof Diameter/diameter)
(defmax defmulti multi/diameter)
(defmax hashmaps hashmaps/diameter)
(defmax nonvolatile nonvolatile/diameter)
(defmax signatures signatures/diameter)
(defmax nohierarchy nohierarchy/diameter)
(defmax dynafun dynafun/diameter)
(defmax dynarity dynarity/diameter)
;;----------------------------------------------------------------
