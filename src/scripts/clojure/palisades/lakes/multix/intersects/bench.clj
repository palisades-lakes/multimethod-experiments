(set! *warn-on-reflection* true)
(set! *unchecked-math* :warn-on-boxed)
;;----------------------------------------------------------------
(ns palisades.lakes.multix.intersects.bench
  "Use criterium for alternative multimethod implementations."
  {:author "palisades dot lakes at gmail dot com"
   :since "2017-05-29"
   :version "2017-08-18"}
  (:require [palisades.lakes.bench.random.prng :as prng]
            [palisades.lakes.bench.random.generators :as g]
            [palisades.lakes.bench.core :as benchtools]
            [palisades.lakes.multix.intersects.defs :as defs]))
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
                           0 g/generate-array data0 type0 n) 
                         (benchtools/generate-datasets 
                           1 g/generate-array data1 type1 n))]
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
(let [urp (prng/uniform-random-provider 
            "seeds/Well44497b-2017-07-25.edn")
      uig (prng/uniform-int-generator -1.0e6 1.0e6 urp)
      udg (prng/uniform-double-generator -1.0e6 1.0e6 urp)
      ii (g/integer-intervals uig)
      di (g/double-intervals udg)
      ss (g/random-singleton-set uig)
      ran2 (prng/nested-uniform-generator [ii di] urp)
      ran3 (prng/nested-uniform-generator [ii di ss] urp)]
  ;; baselines: both args always IntegerInterval
  (bench ii palisades.lakes.bench.java.sets.IntegerInterval 
         ii palisades.lakes.bench.java.sets.IntegerInterval    
         [defs/manual-java
          defs/invokestatic
          defs/invokevirtual
          defs/invokeinterface
          #_defs/signature-lookup
          defs/hashmap-tables
          defs/no-hierarchy
          defs/signature-dispatch-value
          defs/non-volatile-cache
          defs/defmulti])
  ;; 50% probability of repeat same method, 
  ;; 1st arg always IntegerInterval
  ;; 2nd randomly IntegerInterval or DoubleInterval
  (bench ii ran2
         [defs/defmulti
          #_defs/multi0
          defs/hashmap-tables
          defs/manual-java
          #_defs/nested-lookup
          #_defs/signature-lookup
          defs/no-hierarchy
          defs/signature-dispatch-value
          defs/non-volatile-cache])
  ;; 1/9 probability of same method
  ;; 1st and 2nd args randomly from IntegerInterval, 
  ;; DoubleInterval and SingletonSet
  (bench ran3 ran3
        [defs/defmulti
         #_defs/multi0
         defs/hashmap-tables
         defs/manual-java
         #_defs/nested-lookup
         #_defs/signature-lookup
         defs/no-hierarchy
         defs/signature-dispatch-value
         defs/non-volatile-cache]))
(shutdown-agents)
(System/exit 0)
;;----------------------------------------------------------------
