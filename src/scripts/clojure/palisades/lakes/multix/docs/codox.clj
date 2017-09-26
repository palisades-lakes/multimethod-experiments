(set! *warn-on-reflection* false)
(set! *unchecked-math* false)
;;----------------------------------------------------------------
(ns palisades.lakes.multix.docs.codox
  
  {:doc "Generate codox for multimethod-experiments."
   :author "palisades dot lakes at gmail dot com"
   :since "2017-08-05"
   :version "2017-09-26"}
  
  (:require [clojure.java.io :as io]
            [codox.main :as codox]))
;;----------------------------------------------------------------
#_(set! *warn-on-reflection* true)
#_(set! *unchecked-math* :warn-on-boxed)
;;----------------------------------------------------------------
(let [version "0.0.7"
      project-name "multimethod-experiments"
      description "Benchmark alternate implementations of multimethods."
      options {:name project-name
               :version version 
               :description description
               :language :clojure
               :root-path (io/file "./")
               :output-path "target/doc"
               :source-paths ["src/main/clojure"]
               :source-uri (str "https://github.com/palisades-lakes/"
                                project-name
                                "/tree/"
                                project-name
                                "-{version}/{filepath}#L{line}")
               :namespaces :all
               ;;:doc-paths ["docs"]
               :doc-files ["docs/overview.md" 
                           "docs/benchmarks.md" 
                           "docs/changes.md" ]
               :html {:namespace-list :flat}
               ;;:exclude-vars #"^(map)?->\p{Upper}"
               :metadata {:doc "TODO: write docs"
                          :doc/format :markdown}
               :themes [:default]}]
  (codox/generate-docs options))
;;----------------------------------------------------------------

