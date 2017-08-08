(set! *warn-on-reflection* true)
(set! *unchecked-math* :warn-on-boxed)
;;----------------------------------------------------------------
(ns multix.intersects.faster
  "Profile dispatch of (intersects? set0 set1)."
  {:author "mcdonald dot john dot alan at gmail dot com"
   :since "2017-05-29"
   :version "2017-07-30"}
  (:require [benchtools.random.prng :as prng]
            [benchtools.random.generators :as g]
            [benchtools.core :as benchtools]
            [multix.intersects.defs :as defs]))
;;----------------------------------------------------------------
(time
  (let [urp (prng/uniform-random-provider 
              "seeds/Well44497b-2017-07-25.edn")
        uig (prng/uniform-int-generator -1.0e6 1.0e6 urp)
        udg (prng/uniform-double-generator -1.0e6 1.0e6 urp)
        data0 (g/integer-intervals uig)
        data1 (prng/nested-uniform-generator
                [data0 (g/double-intervals udg)] urp)
        n (* 4 4 1024 1024)]
    (println (benchtools/fn-name data0) n (benchtools/fn-name data1) n 
             (.toString (java.time.LocalDateTime/now)))
    (with-open [w (benchtools/log-writer *ns* data0 data1 n)]
      (binding [*out* w]
        (let [data-map 
              (merge (benchtools/generate-datasets 
                       0 g/generate-array data0 n) 
                     (benchtools/generate-datasets 
                       1 g/generate-array data1 n))]
          (time 
            (benchtools/milliseconds defs/faster data-map)))))))
(shutdown-agents)
(System/exit 0)
;;----------------------------------------------------------------
