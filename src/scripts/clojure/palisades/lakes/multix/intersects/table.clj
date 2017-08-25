(set! *warn-on-reflection* true)
(set! *unchecked-math* :warn-on-boxed)
;;----------------------------------------------------------------
(ns defmulti.intersects.table
  "Table benchmark dispatch of (intersects? set0 set1)."
  {:author "palisades dot lakes at gmail dot com"
   :since "2017-06-14"
   :version "2017-07-30"}
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
