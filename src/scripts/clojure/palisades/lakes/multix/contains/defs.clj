(set! *warn-on-reflection* true)
(set! *unchecked-math* :warn-on-boxed)
;;----------------------------------------------------------------
(ns palisades.lakes.multix.contains.defs
  
  {:doc "Benchmarks for multiple dispatch alternatives."
   :author "palisades dot lakes at gmail dot com"
   :since "2017-05-29"
   :version "2017-08-23"}
  
  (:refer-clojure :exclude [defmulti])
  
  (:require [palisades.lakes.bench.random.prng :as prng]
            [palisades.lakes.bench.random.generators :as g]
            [palisades.lakes.bench.core :as benchtools]
            [palisades.lakes.multix.sets.multi :as multi]
            [palisades.lakes.multix.sets.multi0 :as multi0]
            [palisades.lakes.multix.sets.multi1 :as multi1]
            [palisades.lakes.multix.sets.faster :as faster]
            [palisades.lakes.multix.sets.faster2 :as faster2]
            [palisades.lakes.multix.sets.faster3 :as faster3]
            [palisades.lakes.multix.sets.dynafun :as dynafun])
  
  (:import [clojure.lang IFn] 
           [org.apache.commons.rng UniformRandomProvider]
           [org.apache.commons.rng.sampling CollectionSampler]
           [palisades.lakes.bench.java.sets Contains]))
;;----------------------------------------------------------------
(defn uniformDoubleOrIntegerGenerator 
  ^clojure.lang.IFn [^double umin
                     ^double umax
                     ^UniformRandomProvider urp]
  (let [lmin (long umin)
        lmax (long umax)
        ^CollectionSampler cs 
        (CollectionSampler. 
          urp 
          [(prng/uniformDoubleGenerator umin umax urp)
           (prng/uniformIntegerGenerator lmin lmax urp)])]
    (fn UniformDoubleOrInteger ^Number [] ((.sample cs)))))
;;----------------------------------------------------------------
(let [urp (prng/uniform-random-provider "seeds/Well44497b-2017-06-13.edn")
      umin -100.0
      umax 100.0]
  (def ^IFn r1 (g/integer-intervals (prng/uniform-int-generator -100 100 urp)))
  (def ^IFn r2 (g/interval-of-2 umin umax urp))
  (def ^IFn r3 (g/set-of-3 umin umax urp))
  (def ^IFn r7 (g/set-of-7 umin umax urp))
  (def ^IFn n1 (prng/uniformIntegerGenerator umin umax urp))
  (def ^IFn n2 (uniformDoubleOrIntegerGenerator umin umax urp))
  (def ^IFn n6 (prng/uniformNumberGenerator umin umax urp)))
;;----------------------------------------------------------------
;; macro for counting loop instead of function,
;; since some of the calls are to java methods and not functions, 
;; and in any case would force dynamic function call rather than 
;; allowing static linking.

(defmacro defbench [benchname fname]
  (let [s0 (gensym "sets") 
        s1 (gensym "elements")
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
(defbench manual-java Contains/contains)
(defbench defmulti multi/contains?)
(defbench multi0 multi0/contains?)
(defbench hashmap-tables multi1/contains?)
(defbench no-hierarchy faster/contains?)
(defbench non-volatile-cache faster2/contains?)
(defbench signature-dispatch-value faster3/contains?)
(defbench dynafun dynafun/contains?)
;;----------------------------------------------------------------
(defn bench 
  ([data0 type0 data1 type1 fns] 
    (let [n (* 1 4 1024 1024)]
      (println (benchtools/fn-name data0) n 
               (benchtools/fn-name data1) n 
               (.toString (java.time.LocalDateTime/now))) 
      (time
        (with-open [w (benchtools/log-writer *ns* data0 data1 n)]
          (binding [*out* w]
            (benchtools/print-system-info w)
            (let [data-map 
                  (merge (benchtools/generate-datasets 
                           0 g/generate-objects data0 type0 n) 
                         (benchtools/generate-datasets 
                           1 g/generate-objects data1 type1 n))]
              (reduce
                (fn [records record]
                  (if record
                    (let [records (conj records record)]
                      (benchtools/write-tsv 
                        records 
                        (benchtools/data-file *ns* data0 data1 n))
                      records)
                    records))
                []
                (for [f fns]
                  (do
                    (Thread/sleep (int (* 64 1000))) 
                    (time 
                      (benchtools/criterium 
                        f data-map 
                        {:tail-quantile 0.05 :samples 100})))))))))))
  ([data0 data1 fns] 
    (bench data0 Object data1 Object fns)))
;;----------------------------------------------------------------
