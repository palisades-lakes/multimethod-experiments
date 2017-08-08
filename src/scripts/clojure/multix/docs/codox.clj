(set! *warn-on-reflection* false)
(set! *unchecked-math* false)
;;----------------------------------------------------------------
(ns multix.docs.codox
  
  {:doc "Generate codox for multimethod-experiments."
   :author "palisades dot lakes at gmail dot com"
   :since "2017-08-05"
   :version "2017-08-05"}
  
  (:require [clojure.java.io :as io]
            [codox.main :as codox]))
;;----------------------------------------------------------------
#_(set! *warn-on-reflection* true)
#_(set! *unchecked-math* :warn-on-boxed)
;;----------------------------------------------------------------
(let [options  {:project 
                {:name "multimethod-experiments"
                 :version "0.0.0" 
                 :description 
                 "Benchmark alternate implementations of multimethods."}
                :language :clojure
                :root-path (io/file "./")
                :output-path "docs/codox"
                :source-paths ["src/main/clojure" "src/scripts/clojure"]
                ;;:source-uri "https://github.com/palisades-lakes/faster-multimethods/blob/{version}/{filepath}#L{line}"
                :namespaces :all
                ;;:doc-paths ["docs"]
                :doc-files ["README.md"]
                :html {:namespace-list :flat}
                ;;:exclude-vars #"^(map)?->\p{Upper}"
                :metadata {:doc "TODO: write docs"
                           :doc/format :markdown}
                :themes [:default]}]
  (codox/generate-docs options))
;;----------------------------------------------------------------

