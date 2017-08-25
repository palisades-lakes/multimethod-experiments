(set! *warn-on-reflection* true)
(set! *unchecked-math* :warn-on-boxed)
;;----------------------------------------------------------------
(ns palisades.lakes.multix.intersects.msec
  "Simple faster (no criterium) benchmarks"
  {:author "palisades dot lakes at gmail dot com"
   :since "2017-05-29"
   :version "2017-07-30"}
  (:require [palisades.lakes.bench.random.prng :as prng]
            [palisades.lakes.bench.random.generators :as g]
            [palisades.lakes.bench.core :as benchtools]
            [palisades.lakes.multix.intersects.defs :as defs]))
;;----------------------------------------------------------------
(time
  (let [urp (prng/uniform-random-provider 
              "seeds/Well44497b-2017-07-25.edn")
        uig (prng/uniform-int-generator -1.0e6 1.0e6 urp)
        udg (prng/uniform-double-generator -1.0e6 1.0e6 urp)
        data0 (g/IntegerIntervals uig)
        data1 (prng/nested-uniform-generator
                [data0 (g/DoubleIntervals udg)] urp)
        n (* 1 4 1024 1024)]
    (println (benchtools/fn-name data0) n (benchtools/fn-name data1) n 
             (.toString (java.time.LocalDateTime/now)))
    (time
      (with-open [w (benchtools/log-writer *ns* data0 data1 n)]
        (binding [*out* w]
          (benchtools/print-system-info w)
          (let [data-map 
                (merge (benchtools/generate-datasets 
                         0 g/generate-objects data0 n) 
                       (benchtools/generate-datasets 
                         1 g/generate-objects data1 n))]
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
              (for [f [defs/manual-java
                       defs/nested-lookup
                       defs/signature-lookup
                       defs/faster
                       defs/multi]]
                (do
                  (println)
                  (println "{" :name (benchtools/fn-name f))
                  (println :msec (benchtools/milliseconds f data-map) "}"))))))))))
(shutdown-agents)
(System/exit 0)
;;----------------------------------------------------------------
