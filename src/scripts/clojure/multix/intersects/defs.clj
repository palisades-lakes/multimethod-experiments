(set! *warn-on-reflection* true)
(set! *unchecked-math* :warn-on-boxed)
;;----------------------------------------------------------------
(ns multix.intersects.defs
  
  {:doc "Benchmarks for multiple dispatch alternatives."
   :author "palisades dot lakes at gmail dot com"
   :since "2017-05-29"
   :version "2017-08-16"}
  
  (:refer-clojure :exclude [defmulti])
  
  (:require [multix.sets.manual :as nested]
            [multix.sets.signature :as signature]
            [multix.sets.multi :as multi]
            [multix.sets.multi0 :as multi0]
            [multix.sets.multi1 :as multi1]
            [multix.sets.faster :as faster]
            [multix.sets.faster2 :as faster2]
            [multix.sets.faster3 :as faster3])
  
  (:import [benchtools.java.sets DoubleInterval IntegerInterval
            Intersects Sets]))
;;----------------------------------------------------------------
;; TODO: 
;; (since we can't just pass in a function)
;;----------------------------------------------------------------
(defn invokestatic 
  ^long [^"[Lbenchtools.java.sets.IntegerInterval;" s0 
         ^"[Lbenchtools.java.sets.IntegerInterval;" s1]
  (Intersects/countIntersections s0 s1)) 
;;----------------------------------------------------------------
(defn invokevirtual 
  ^long [^"[Lbenchtools.java.sets.IntegerInterval;" s0 
         ^"[Lbenchtools.java.sets.IntegerInterval;" s1]
  (Sets/countIntersections s0 s1))
;;----------------------------------------------------------------
(defn invokeinterface 
  ^long [^"[Lbenchtools.java.sets.Set;" s0 
         ^"[Lbenchtools.java.sets.IntegerInterval;" s1]
  (Sets/countIntersections s0 s1))
;;----------------------------------------------------------------
;; macro for counting loop since some of the calls
;; are java methods and not functions, and would force
;; dynamic function call rather than allowing static linking.

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
;;----------------------------------------------------------------
