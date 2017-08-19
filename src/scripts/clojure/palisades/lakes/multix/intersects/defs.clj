(set! *warn-on-reflection* true)
(set! *unchecked-math* :warn-on-boxed)
;;----------------------------------------------------------------
(ns palisades.lakes.multix.intersects.defs
  
  {:doc "Benchmarks for multiple dispatch alternatives."
   :author "palisades dot lakes at gmail dot com"
   :since "2017-05-29"
   :version "2017-08-19"}
  
  (:refer-clojure :exclude [defmulti])
  
  (:require [palisades.lakes.multix.sets.manual :as nested]
            [palisades.lakes.multix.sets.signature :as signature]
            [palisades.lakes.multix.sets.multi :as multi]
            [palisades.lakes.multix.sets.multi0 :as multi0]
            [palisades.lakes.multix.sets.multi1 :as multi1]
            [palisades.lakes.multix.sets.faster :as faster]
            [palisades.lakes.multix.sets.faster2 :as faster2]
            [palisades.lakes.multix.sets.faster3 :as faster3]
            [palisades.lakes.multix.sets.dynafun :as dynafun])
  
  (:import [palisades.lakes.bench.java.sets DoubleInterval IntegerInterval
            Intersects Sets]))
;;----------------------------------------------------------------
;; TODO: 
;; (since we can't just pass in a function)
;;----------------------------------------------------------------
(defn invokestatic 
  ^long [^"[Lpalisades.lakes.bench.java.sets.IntegerInterval;" s0 
         ^"[Lpalisades.lakes.bench.java.sets.IntegerInterval;" s1]
  (Intersects/countIntersections s0 s1)) 
;;----------------------------------------------------------------
(defn invokevirtual 
  ^long [^"[Lpalisades.lakes.bench.java.sets.IntegerInterval;" s0 
         ^"[Lpalisades.lakes.bench.java.sets.IntegerInterval;" s1]
  (Sets/countIntersections s0 s1))
;;----------------------------------------------------------------
(defn invokeinterface 
  ^long [^"[Lpalisades.lakes.bench.java.sets.Set;" s0 
         ^"[Lpalisades.lakes.bench.java.sets.IntegerInterval;" s1]
  (Sets/countIntersections s0 s1))
;;----------------------------------------------------------------
;; macro for counting loop instead of function,
;; since some of the calls are to java methods and not functions, 
;; and in any case would force dynamic function call rather than 
;; allowing static linking.

(defmacro defbench [benchname fname]
  (let [s0 (gensym "sets") 
        s1 (gensym "sets")
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
(defbench manual-java Intersects/manual)
(defbench nested-lookup nested/intersects?)
(defbench signature-lookup signature/intersects?)
(defbench defmulti multi/intersects?)
(defbench multi0 multi0/intersects?)
(defbench hashmap-tables multi1/intersects?)
(defbench no-hierarchy faster/intersects?)
(defbench non-volatile-cache faster2/intersects?)
(defbench signature-dispatch-value faster3/intersects?)
(defbench dynafun dynafun/intersects?)
;;----------------------------------------------------------------
