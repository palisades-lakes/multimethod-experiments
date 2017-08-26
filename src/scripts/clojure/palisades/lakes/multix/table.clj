(set! *warn-on-reflection* true)
(set! *unchecked-math* :warn-on-boxed)
;;----------------------------------------------------------------
(ns palisades.lakes.multix.table
  "Table benchmark data. 
   TODO: get this working again? For all the benchmarks?"
  {:author "palisades dot lakes at gmail dot com"
   :since "2017-06-14"
   :version "2017-08-26"}
  (:require [clojure.java.io :as io]
            [palisades.lakes.bench.core :as bench]))
;;----------------------------------------------------------------
(let [for-ns 
      (create-ns 'palisades.lakes.multix.intersects.bench)
      folder (bench/data-folder for-ns)]
  (bench/write-tsv 
    (bench/summary-table
      for-ns 
      "LENOVO.*.*.*.8388608.2017-07-30")
    (io/file folder "summary.tsv")))
;;----------------------------------------------------------------
