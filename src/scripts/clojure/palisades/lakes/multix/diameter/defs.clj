(set! *warn-on-reflection* true)
(set! *unchecked-math* :warn-on-boxed)
;;----------------------------------------------------------------
(ns palisades.lakes.multix.diameter.defs
  
  {:doc "Benchmarks for multiple dispatch alternatives."
   :author "palisades dot lakes at gmail dot com"
   :since "2017-05-29"
   :version "2017-08-24"}
  
  (:refer-clojure :exclude [defmulti])
  
  (:require [clojure.pprint :as pp]
            [palisades.lakes.bench.random.prng :as prng]
            [palisades.lakes.bench.random.generators :as g]
            [palisades.lakes.bench.core :as benchtools]
            [palisades.lakes.multix.sets.multi :as multi]
            [palisades.lakes.multix.sets.multi0 :as multi0]
            [palisades.lakes.multix.sets.multi1 :as multi1]
            [palisades.lakes.multix.sets.faster :as faster]
            [palisades.lakes.multix.sets.faster2 :as faster2]
            [palisades.lakes.multix.sets.faster3 :as faster3]
            [palisades.lakes.multix.sets.dynafun :as dynafun])
  
  (:import [clojure.lang IFn IFn$L] 
           [org.apache.commons.rng UniformRandomProvider]
           [org.apache.commons.rng.sampling CollectionSampler]
           [palisades.lakes.bench.java.sets Diameter]))
;;----------------------------------------------------------------
(let [urp (prng/uniform-random-provider "seeds/Well44497b-2017-06-13.edn")
      umin -100.0
      umax 100.0]
  (def ^IFn r2 (g/interval-of-2 umin umax urp))
  (def ^IFn r3 (g/set-of-3 umin umax urp))
  (def ^IFn r7 (g/set-of-7 umin umax urp)))
;;----------------------------------------------------------------
;; macro for counting loop instead of function,
;; since some of the calls are to java methods and not functions, 
;; and in any case would force dynamic function call rather than 
;; allowing static linking.

(defmacro defcounter [benchname fname]
  (let [s (gensym "Sets") 
        args [(with-meta s {:tag 'objects})]
        args (with-meta args {:tag 'double})]
    #_(binding [*print-meta* true] (pp/pprint args))
    `(defn ~benchname ~args
       (let [n# (int (alength ~s))]
         (loop [i# (int 0)
                total# (double 0)]
           (if (>= i# n#) 
             (double total#)
             (recur (inc i#) (+ total# (~fname (aget ~s i#)) ))))))))
;;----------------------------------------------------------------
(defcounter manual-java Diameter/diameter)
(defcounter defmulti multi/diameter?)
(defcounter multi0 multi0/diameter?)
(defcounter hashmap-tables multi1/diameter?)
(defcounter no-hierarchy faster/diameter?)
(defcounter non-volatile-cache faster2/diameter?)
(defcounter signature-dispatch-value faster3/diameter?)
(defcounter dynafun dynafun/diameter?)
;;----------------------------------------------------------------
(defn iiint-static ^long [^"[Lpalisades.lakes.bench.java.sets.IntegerInterval;" s0 
                          ^"[I" s1]
  (Diameter/countStatic s0 s1)) 

(defn iiInteger-static ^long [^"[Lpalisades.lakes.bench.java.sets.IntegerInterval;" s0 
                              ^"[Ljava.lang.Integer;" s1]
  (Diameter/countStatic s0 s1))

(defn sint-static ^long [^"[Lpalisades.lakes.bench.java.sets.Set;" s0 
                         ^"[I" s1]
  (Diameter/countStatic s0 s1))

(defn sInteger-static ^long [^"[Lpalisades.lakes.bench.java.sets.Set;" s0 
                             ^"[Ljava.lang.Integer;" s1]
  (Diameter/countStatic s0 s1))

(defn oo-static ^long [^objects s0 ^objects s1]
  (Diameter/countStatic s0 s1))
;;----------------------------------------------------------------
(defn iiint-virtual ^long [^"[Lpalisades.lakes.bench.java.sets.IntegerInterval;" s0 
                           ^"[I" s1]
  (Diameter/countStatic s0 s1)) 

(defn iiInteger-virtual ^long [^"[Lpalisades.lakes.bench.java.sets.IntegerInterval;" s0 
                               ^"[Ljava.lang.Integer;" s1]
  (Diameter/countStatic s0 s1))
;;----------------------------------------------------------------
(defn sint-interface ^long [^"[Lpalisades.lakes.bench.java.sets.Set;" s0 
                            ^"[I" s1]
  (Diameter/countStatic s0 s1))

(defn sInteger-interface ^long [^"[Lpalisades.lakes.bench.java.sets.Set;" s0 
                                ^"[Ljava.lang.Integer;" s1]
  (Diameter/countStatic s0 s1))
;;----------------------------------------------------------------
(defn bench 
  ([dataset-generator element-generator fns] 
    (let [n (* 1 1 1024 1024)]
      (println (benchtools/fn-name dataset-generator)  
               (benchtools/fn-name element-generator) 
               n) 
      (println (.toString (java.time.LocalDateTime/now))) 
      (time
        (with-open [w (benchtools/log-writer 
                        *ns* [dataset-generator element-generator] n)]
          (binding [*out* w]
            (benchtools/print-system-info w)
            (let [data-map 
                  (benchtools/generate-datasets 
                      0 dataset-generator element-generator n) ]
              (reduce
                (fn [records record]
                  (if record
                    (let [records (conj records record)]
                      (benchtools/write-tsv 
                        records 
                        (benchtools/data-file 
                          *ns* [dataset-generator element-generator] n))
                      records)
                    records))
                []
                (for [f fns]
                  (do
                    (Thread/sleep (int (* 8 1000))) 
                    (println (.toString (java.time.LocalDateTime/now))) 
                    (time 
                      (benchtools/criterium 
                        f data-map 
                        {:tail-quantile 0.05 :samples 100}))))))))))))
;;----------------------------------------------------------------
