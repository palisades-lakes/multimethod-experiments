(set! *warn-on-reflection* true)
(set! *unchecked-math* :warn-on-boxed)
;;----------------------------------------------------------------
(ns multix.intersects.defs
  
  {:doc "Benchmarks for multiple dispatch alternatives."
   :author "mcdonald dot john dot alan at gmail dot com"
   :since "2017-05-29"
   :version "2017-08-07"}
  
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
;; TODO: macro for counting loop 
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
(defn manual-java ^long [^objects s0 ^objects s1]
  (let [n (int (min (alength s0) (alength s1)))]
    (loop [i (int 0)
           total (long 0)]
      (cond (>= i n) (long total)
            (Intersects/manual (aget s0 i) (aget s1 i)) 
            (recur (inc i) (inc total))
            :else (recur (inc i) total)))))
;;----------------------------------------------------------------
(defn nested-lookup ^long [^objects s0 ^objects s1]
  (let [n (int (min (alength s0) (alength s1)))]
    (loop [i (int 0)
           total (long 0)]
      (cond (>= i n) (long total)
            (nested/intersects? (aget s0 i) (aget s1 i)) 
            (recur (inc i) (inc total))
            :else (recur (inc i) total)))))
;;----------------------------------------------------------------
(defn signature-lookup ^long [^objects s0 ^objects s1]
  (let [n (int (min (alength s0) (alength s1)))]
    (loop [i (int 0)
           total (long 0)]
      (cond (>= i n) (long total)
            (signature/intersects? (aget s0 i) (aget s1 i)) 
            (recur (inc i) (inc total))
            :else (recur (inc i) total)))))
;;----------------------------------------------------------------
(defn defmulti ^long [^objects s0 ^objects s1]
  (let [n (int (min (alength s0) (alength s1)))]
    (loop [i (int 0)
           total (long 0)]
      (cond (>= i n) (long total)
            (multi/intersects? (aget s0 i) (aget s1 i)) 
            (recur (inc i) (inc total))
            :else (recur (inc i) total)))))
;;----------------------------------------------------------------
(defn multi0 ^long [^objects s0 ^objects s1]
  (let [n (int (min (alength s0) (alength s1)))]
    (loop [i (int 0)
           total (long 0)]
      (cond (>= i n) (long total)
            (multi0/intersects? (aget s0 i) (aget s1 i)) 
            (recur (inc i) (inc total))
            :else (recur (inc i) total)))))
;;----------------------------------------------------------------
(defn hashmap-tables ^long [^objects s0 ^objects s1]
  (let [n (int (min (alength s0) (alength s1)))]
    (loop [i (int 0)
           total (long 0)]
      (cond (>= i n) (long total)
            (multi1/intersects? (aget s0 i) (aget s1 i)) 
            (recur (inc i) (inc total))
            :else (recur (inc i) total)))))
;;----------------------------------------------------------------
(defn no-hierarchy ^long [^objects s0 ^objects s1]
  (let [n (int (min (alength s0) (alength s1)))]
    (loop [i (int 0)
           total (long 0)]
      (cond (>= i n) (long total)
            (faster/intersects? (aget s0 i) (aget s1 i)) 
            (recur (inc i) (inc total))
            :else (recur (inc i) total)))))
;;----------------------------------------------------------------
(defn non-volatile-cache ^long [^objects s0 ^objects s1]
  (let [n (int (min (alength s0) (alength s1)))]
    (loop [i (int 0)
           total (long 0)]
      (cond (>= i n) (long total)
            (faster2/intersects? (aget s0 i) (aget s1 i)) 
            (recur (inc i) (inc total))
            :else (recur (inc i) total)))))
;;----------------------------------------------------------------
(defn signature-dispatch-value ^long [^objects s0 ^objects s1]
  (let [n (int (min (alength s0) (alength s1)))]
    (loop [i (int 0)
           total (long 0)]
      (cond (>= i n) (long total)
            (faster3/intersects? (aget s0 i) (aget s1 i)) 
            (recur (inc i) (inc total))
            :else (recur (inc i) total)))))
;;----------------------------------------------------------------
