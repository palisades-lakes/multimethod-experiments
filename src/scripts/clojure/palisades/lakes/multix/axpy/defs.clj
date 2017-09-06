(set! *warn-on-reflection* true)
(set! *unchecked-math* :warn-on-boxed)
;;----------------------------------------------------------------
(ns palisades.lakes.multix.axpy.defs
  
  {:doc "Benchmarks for multiple dispatch alternatives."
   :author "palisades dot lakes at gmail dot com"
   :since "2017-08-26"
   :version "2017-09-05"}
  
  (:refer-clojure :exclude [defmulti])
  
  (:require [palisades.lakes.bench.prng :as prng]
            [palisades.lakes.bench.generators :as g]
            [palisades.lakes.bench.core :as bench]
            [palisades.lakes.multix.r2.protocols :as protocols]
            [palisades.lakes.multix.r2.instancefn :as instancefn]
            [palisades.lakes.multix.r2.multi :as multi]
            [palisades.lakes.multix.r2.hashmaps :as hashmaps]
            [palisades.lakes.multix.r2.nohierarchy :as nohierarchy]
            [palisades.lakes.multix.r2.signatures :as signatures]
            [palisades.lakes.multix.r2.dynafun :as dynafun]
            [palisades.lakes.multix.r2.dynalin :as dynalin]
            [palisades.lakes.multix.r2.dynamap :as dynamap]
            [palisades.lakes.multix.r2.dynest :as dynest])
  
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
  (Axpy/maxL1Virtual a x y)) 
(defn invokeinterface 
  ^double [^"[Lpalisades.lakes.bench.java.spaces.linear.LinearFunction;" a 
           ^"[Lpalisades.lakes.bench.java.spaces.linear.Vector;" x 
           ^"[Lpalisades.lakes.bench.java.spaces.linear.Vector;" y]
  (Axpy/maxL1Interface a x y)) 
;;----------------------------------------------------------------
(bench/defmaxl1 protocols protocols/axpy)
(bench/defmaxl1 instanceof Axpy/axpy)
(bench/defmaxl1 instancefn instancefn/axpy)
(bench/defmaxl1 defmulti multi/axpy)
(bench/defmaxl1 hashmaps hashmaps/axpy)
(bench/defmaxl1 signatures signatures/axpy)
(bench/defmaxl1 nohierarchy nohierarchy/axpy)
(bench/defmaxl1 dynafun dynafun/axpy)
(bench/defmaxl1 dynamap dynamap/axpy)
(bench/defmaxl1 dynalin dynalin/axpy)
(bench/defmaxl1 dynest dynest/axpy)
;;----------------------------------------------------------------
