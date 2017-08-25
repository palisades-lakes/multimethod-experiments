(set! *warn-on-reflection* true)
(set! *unchecked-math* :warn-on-boxed)
;;----------------------------------------------------------------
(ns palisades.lakes.multix.intersects.defs
  
  {:doc "Benchmarks for multiple dispatch alternatives."
   :author "palisades dot lakes at gmail dot com"
   :since "2017-05-29"
   :version "2017-08-25"}
  
  (:refer-clojure :exclude [defmulti])
  
  (:require [palisades.lakes.bench.prng :as prng]
            [palisades.lakes.bench.generators :as g]
            [palisades.lakes.bench.core :as bench]
            [palisades.lakes.multix.sets.manual :as nested]
            [palisades.lakes.multix.sets.signature :as signature]
            [palisades.lakes.multix.sets.multi :as multi]
            [palisades.lakes.multix.sets.multi0 :as multi0]
            [palisades.lakes.multix.sets.multi1 :as multi1]
            [palisades.lakes.multix.sets.faster :as faster]
            [palisades.lakes.multix.sets.faster2 :as faster2]
            [palisades.lakes.multix.sets.faster3 :as faster3]
            [palisades.lakes.multix.sets.dynafun :as dynafun])
  
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
  (Intersects/count s0 s1)) 
;;----------------------------------------------------------------
(defn invokevirtual ^long [^"[Lpalisades.lakes.bench.java.sets.IntegerInterval;" s0 
                           ^"[Lpalisades.lakes.bench.java.sets.IntegerInterval;" s1]
  (Sets/countIntersections s0 s1))
;;----------------------------------------------------------------
(defn invokeinterface ^long [^"[Lpalisades.lakes.bench.java.sets.Set;" s0 
                             ^"[Lpalisades.lakes.bench.java.sets.IntegerInterval;" s1]
  (Sets/countIntersections s0 s1))
;;----------------------------------------------------------------
;; macro for counting loop instead of function,
;; since some of the calls are to java methods and not functions, 
;; and in any case would force dynamic function call rather than 
;; allowing static linking.

(defmacro defcounter [benchname fname]
  (let [s0 (gensym "Sets") 
        s1 (gensym "Sets")
        args [(with-meta s0 {:tag 'objects})
              (with-meta s1 {:tag 'objects})]
        args (with-meta args {:tag 'long})]
    `(defn ~benchname ~args
       (let [n# (int (min (alength ~s0) (alength ~s1)))]
         (loop [i# (int 0)
                total# (long 0)]
           (cond (>= i# n#) (long total#)
                 (~fname (aget ~s0 i#) (aget ~s1 i#)) 
                 (recur (inc i#) (inc total#))
                 :else (recur (inc i#) total#)))))))
;;----------------------------------------------------------------
(defcounter manual-java Intersects/manual)
(defcounter nested-lookup nested/intersects?)
(defcounter signature-lookup signature/intersects?)
(defcounter defmulti multi/intersects?)
(defcounter multi0 multi0/intersects?)
(defcounter hashmap-tables multi1/intersects?)
(defcounter no-hierarchy faster/intersects?)
(defcounter non-volatile-cache faster2/intersects?)
(defcounter signature-dispatch-value faster3/intersects?)
(defcounter dynafun dynafun/intersects?)
;;----------------------------------------------------------------
