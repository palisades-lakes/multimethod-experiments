(set! *warn-on-reflection* true)
(set! *unchecked-math* :warn-on-boxed)
;;----------------------------------------------------------------
(ns palisades.lakes.multix.axpy.defs
  
  {:doc "Benchmarks for multiple dispatch alternatives."
   :author "palisades dot lakes at gmail dot com"
   :since "2017-08-26"
   :version "2017-08-27"}
  
  (:refer-clojure :exclude [defmulti])
  
  (:require [palisades.lakes.bench.prng :as prng]
            [palisades.lakes.bench.generators :as g]
            [palisades.lakes.bench.core :as bench]
            [palisades.lakes.multix.r2.multi :as multi]
            [palisades.lakes.multix.r2.hashmaps :as hashmaps]
            [palisades.lakes.multix.r2.nohierarchy :as nohierarchy]
            [palisades.lakes.multix.r2.nonvolatile :as nonvolatile]
            [palisades.lakes.multix.r2.signatures :as signatures]
            [palisades.lakes.multix.r2.dynafun :as dynafun])
  
  (:import [clojure.lang IFn IFn$D]
           [palisades.lakes.bench.java.spaces.linear 
            Axpy LinearFunction Vector]))
;;----------------------------------------------------------------
(let [urp (prng/uniform-random-provider "seeds/Well44497b-2017-06-12.edn")
      umin -100.0
      umax 100.0]
  (def ^IFn$D udouble (prng/uniform-double -100 100 urp))
  (def ^IFn d2 (g/d2 udouble))
  (def ^IFn v2 (g/v2 umin umax urp))
  (def ^IFn d22 (g/d22 udouble))
  (def ^IFn m22 (g/m22 umin umax urp)))
;;----------------------------------------------------------------
;; not implemented, too much internal state to expose
#_(defn invokestatic 
   ^double [^"[Lpalisades.lakes.bench.java.spaces.linear.r2.D22;" a 
           ^"[Lpalisades.lakes.bench.java.spaces.linear.r2.D2;" x 
           ^"[Lpalisades.lakes.bench.java.spaces.linear.r2.D2;" y]
  (Axpy/sumL1Static a x y)) 
(defn invokevirtual 
  ^double [^"[Lpalisades.lakes.bench.java.spaces.linear.r2.D22;" a 
           ^"[Lpalisades.lakes.bench.java.spaces.linear.r2.D2;" x 
           ^"[Lpalisades.lakes.bench.java.spaces.linear.r2.D2;" y]
  (Axpy/sumL1Virtual a x y)) 
(defn invokeinterface 
  ^double [^"[Lpalisades.lakes.bench.java.spaces.linear.LinearFunction;" a 
           ^"[Lpalisades.lakes.bench.java.spaces.linear.Vector;" x 
           ^"[Lpalisades.lakes.bench.java.spaces.linear.Vector;" y]
  (Axpy/sumL1Interface a x y)) 
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
       (assert (== (alength ~a) (alength ~x) (alength ~y)))
       (let [n# (int (alength ~a))]
         (loop [i# (int 0)
                total# (double 0)]
           (if (>= i# n#) 
             (double total#)
             (let [^Vector v# (~f (aget ~a i#) (aget ~x i#) (aget ~y i#))]
               (recur (inc i#) (+ total# (.l1Norm v#))))))))))
;;----------------------------------------------------------------
;; not implemented: 216=6x6x6 cases
#_(defsum instanceof Axpy/axpy)
(defsum defmulti multi/axpy)
(defsum hashmaps hashmaps/axpy)
(defsum nonvolatile nonvolatile/axpy)
(defsum signatures signatures/axpy)
(defsum nohierarchy nohierarchy/axpy)
(defsum dynafun dynafun/axpy)
;;----------------------------------------------------------------
