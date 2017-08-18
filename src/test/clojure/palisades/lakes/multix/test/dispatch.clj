(set! *warn-on-reflection* true)
(set! *unchecked-math* :warn-on-boxed)
;;----------------------------------------------------------------
(ns palisades.lakes.multix.test.dispatch
  
  {:doc "Unit tests for multix"
   :author "palisades dot lakes at gmail dot com"
   :since "2017-07-13"
   :version "2017-07-13"}
  
  (:require [clojure.test :as test])
  (:import [java.util Collections]
           [com.google.common.collect ImmutableList]
           [palisades.lakes.bench.java.sets DoubleInterval IntegerInterval
            Set]))
;; mvn clojure:test -Dtest=palisades.lakes.multix.test.dispatch
;;----------------------------------------------------------------
(defmulti intersects?
  (fn intersects?-dispatch [s0 s1] 
    (ImmutableList/of (class s0) (class s1))))
(defmethod intersects? 
  (ImmutableList/of java.util.Set java.util.Set)
  [s0 s1] 
  (not (Collections/disjoint s0 s1)))
(defmethod intersects? 
  (ImmutableList/of Set Set)
  [^Set s0 ^Set s1]
  (.intersects s0 s1))
(defmethod intersects? 
  (ImmutableList/of Set Object)
  [^Set s0 ^Object s1]
  (.intersects s0 s1))
(defmethod intersects? 
  (ImmutableList/of Object Set)
  [^Object s0 ^Set s1]
  (.intersects s1 s0))
;;----------------------------------------------------------------
(test/deftest invalid-dispatch
  (let [ii (IntegerInterval/make 0 1)
        di (DoubleInterval/make -1.0 1.0)]
    (test/is 
      (thrown-with-msg? 
        IllegalArgumentException 
        #"No method in multimethod"
        (intersects? ii di)))))
;;----------------------------------------------------------------
