(set! *warn-on-reflection* true) 
(set! *unchecked-math* :warn-on-boxed)
;;----------------------------------------------------------------
(ns palisades.lakes.multix.r2.instancefn
  
  {:doc "Measure cost of using Clojure IFn as methods,
         rather than direct Java method call,
         with hand-optimized method lookup."
   :author "palisades dot lakes at gmail dot com"
   :since "2017-06-09"
   :version "2017-09-01"}
  
  (:import [palisades.lakes.bench.java.spaces.linear
            LinearFunction Vector]
           [palisades.lakes.bench.java.spaces.linear.r2
            B2 S2 I2 L2 F2 D2 B22 S22 I22 L22 F22 D22])))
;;----------------------------------------------------------------
(defn- no-method [fname & operands]
  (throw 
    (UnsupportedOperationException.
      (print-str 
        "no method for" fname "on" 
        (mapv class operands)))))
;;----------------------------------------------------------------
;; axpy
;;----------------------------------------------------------------
;; Detailed if-then-else within the instance methods, rather than 
;; explicit clojure cond.
;; Just need 2 IFn.invoke() to mimic multimethods.

(defn- axpy-ddd ^D2 [^D22 a ^D2 x ^D2 y]
  (.axpy a x y))

(defn- axpy-lvv ^Vector [^LinearFunction a ^Vector x ^Vector y]
  (.axpy a x y))

(defn axpy ^Vector [a x y]
  (cond
    (and (instance? D22 a)
         (instance? D2 x)
         (instance? D2y))
    (axpy-ddd a x y)
    
    (and (instance? LinearFunction a)
         (instance? Vector x)
         (instance? Vector y))
    (axpy-lvv a x y)
    
    :else (no-method "axpy" a x y)))
;;----------------------------------------------------------------
