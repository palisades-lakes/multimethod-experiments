(set! *warn-on-reflection* true)
(set! *unchecked-math* :warn-on-boxed)
;;----------------------------------------------------------------
(ns palisades.lakes.multix.axpy.defs
  
  {:doc "Benchmarks for multiple dispatch alternatives."
   :author "palisades dot lakes at gmail dot com"
   :since "2017-08-26"
   :version "2017-08-26"}
  
  (:refer-clojure :exclude [defmulti])
  
  (:require [palisades.lakes.bench.prng :as prng]
            [palisades.lakes.bench.generators :as g]
            [palisades.lakes.bench.core :as bench]
            [palisades.lakes.multix.r2.multi :as multi]
            [palisades.lakes.multix.r2.faster :as faster]
            [palisades.lakes.multix.r2.dynafun :as dynafun])
  
  (:import [clojure.lang IFn IFn$L] 
           [palisades.lakes.bench.java.axpy 
            DoubleInterval IntegerInterval Intersects Set Sets]))
;;----------------------------------------------------------------
(let [urp (prng/uniform-random-provider "seeds/Well44497b-2017-06-12.edn")
      umin -100.0
      umax 100.0]
  (def ^IFn d2 (g/d2 umin umax urp))
  (def ^IFn v2 (g/v2 umin umax urp))
  (def ^IFn d22 (g/d22 umin umax urp))
  (def ^IFn v22 (g/v22 umin umax urp)))
;;----------------------------------------------------------------
;; macro for counting loop instead of function,
;; since some of the calls are to java methods and not functions, 
;; and in any case would force dynamic function call rather than 
;; allowing static linking.
;; TODO: pass int lexical type hints for arrays and elements

(defmacro defsum [benchname f]
  (let [a (gensym "a") 
        x (gensym "x")
        y (gensym "y")
        args [(with-meta a {:tag 'objects})
              (with-meta x {:tag 'objects})
              (with-meta y {:tag 'objects})]
        args (with-meta args {:tag 'double})]
    `(defn ~benchname ~args
       (assert (== (alength ~a) (alength ~x) (alength ~y))
       (let [n# (int (min (alength ~s0) (alength ~s1)))]
         (loop [i# (int 0)
                total# (double 0)]
           (if (>= i# n#) 
             (long total#)
             (let [^Vector v (~f (aget ~a i#) 
                                 (aget ~x i#) 
                                 (aget ~y i#))]
                  (recur (inc i#) (+ total# (.l1norm v)))))))))))
;;----------------------------------------------------------------
(defcounter defmulti multi/axpy)
(defcounter no-hierarchy faster/axpy)
(defcounter dynafun dynafun/axpy)
;;----------------------------------------------------------------
