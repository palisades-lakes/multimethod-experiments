(set! *warn-on-reflection* true)
(set! *unchecked-math* :warn-on-boxed)
;;----------------------------------------------------------------
(ns defmulti.intersects.table
  "Table benchmark dispatch of (intersects? set0 set1)."
  {:author "mcdonald dot john dot alan at gmail dot com"
   :since "2017-06-14"
   :version "2017-07-30"}
  (:require [clojure.java.io :as io]
            [benchtools.core :as benchtools]))
;;----------------------------------------------------------------
(let [for-ns 
      (create-ns 'multix.intersects.bench)
      folder (benchtools/data-folder for-ns)]
  (benchtools/write-tsv 
    (benchtools/summary-table
      for-ns 
      "LENOVO.*.*.*.8388608.2017-07-30")
    (io/file folder "summary.tsv")))
;;----------------------------------------------------------------
