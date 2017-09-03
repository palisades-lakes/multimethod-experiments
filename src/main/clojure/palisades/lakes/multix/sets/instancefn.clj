(set! *warn-on-reflection* true) 
(set! *unchecked-math* :warn-on-boxed)
;;----------------------------------------------------------------
(ns palisades.lakes.multix.sets.instancefn
  
  {:doc "Measure cost of using Clojure IFn as methods,
         rather than direct Java method call,
         with hand-optimized method lookup."
   :author "palisades dot lakes at gmail dot com"
   :since "2017-06-09"
   :version "2017-09-02"}
  
  (:refer-clojure :exclude [contains?])
  
  (:import [java.util Collections]
           [palisades.lakes.bench.java.sets 
            Contains Diameter Intersects Set Sets
            ByteInterval DoubleInterval FloatInterval
            IntegerInterval LongInterval ShortInterval]))
;;----------------------------------------------------------------
(defn- no-method [fname & operands]
  (throw 
    (UnsupportedOperationException.
      (print-str 
        "no method for" fname "on" 
        (mapv class operands)))))
;;----------------------------------------------------------------
;; diameter
;;----------------------------------------------------------------
(defn- diameter-java-util-Set ^double [^java.util.Set s] 
  (Diameter/diameter s))
(defn- diameter-Set ^double [^Set s] 
  (.diameter s))
(defn diameter ^double [s]
  (cond (instance? Set s) (diameter-Set s)
        (instance? java.util.Set s) (diameter-java-util-Set s)
        :else (no-method "diameter" s)))
;;----------------------------------------------------------------
;; contains?
;;----------------------------------------------------------------
(defn- contains-so [^java.util.Set s ^Object x] (.contains s x))
;;----------------------------------------------------------------
(defn- contains-bb [^ByteInterval s ^Byte x] (.contains s x))
(defn- contains-bd [^ByteInterval s ^Double x] (.contains s x))
(defn- contains-bf [^ByteInterval s ^Float x] (.contains s x))
(defn- contains-bi [^ByteInterval s ^Integer x] (.contains s x))
(defn- contains-bl [^ByteInterval s ^Long x] (.contains s x))
(defn- contains-bs [^ByteInterval s ^Short x] (.contains s x))
(defn- contains-bo [^ByteInterval s ^Object x] false)
;;----------------------------------------------------------------
(defn- contains-db [^DoubleInterval s ^Byte x] (.contains s x))
(defn- contains-dd [^DoubleInterval s ^Double x] (.contains s x))
(defn- contains-df [^DoubleInterval s ^Float x] (.contains s x))
(defn- contains-di [^DoubleInterval s ^Integer x] (.contains s x))
(defn- contains-dl [^DoubleInterval s ^Long x] (.contains s x))
(defn- contains-ds [^DoubleInterval s ^Short x] (.contains s x))
(defn- contains-do [^DoubleInterval s ^Object x] false)
;;----------------------------------------------------------------
(defn- contains-fb [^FloatInterval s ^Byte x] (.contains s x))
(defn- contains-fd [^FloatInterval s ^Double x] (.contains s x))
(defn- contains-ff [^FloatInterval s ^Float x] (.contains s x))
(defn- contains-fi [^FloatInterval s ^Integer x] (.contains s x))
(defn- contains-fl [^FloatInterval s ^Long x] (.contains s x))
(defn- contains-fs [^FloatInterval s ^Short x] (.contains s x))
(defn- contains-fo [^FloatInterval s ^Object x] false)
;;----------------------------------------------------------------
(defn- contains-ib [^IntegerInterval s ^Byte x] (.contains s x))
(defn- contains-id [^IntegerInterval s ^Double x] (.contains s x))
(defn- contains-if [^IntegerInterval s ^Float x] (.contains s x))
(defn- contains-ii [^IntegerInterval s ^Integer x] (.contains s x))
(defn- contains-il [^IntegerInterval s ^Long x] (.contains s x))
(defn- contains-is [^IntegerInterval s ^Short x] (.contains s x))
(defn- contains-io [^IntegerInterval s ^Object x] false)
;;----------------------------------------------------------------
(defn- contains-lb [^LongInterval s ^Byte x] (.contains s x))
(defn- contains-ld [^LongInterval s ^Double x] (.contains s x))
(defn- contains-lf [^LongInterval s ^Float x] (.contains s x))
(defn- contains-li [^LongInterval s ^Integer x] (.contains s x))
(defn- contains-ll [^LongInterval s ^Long x] (.contains s x))
(defn- contains-ls [^LongInterval s ^Short x] (.contains s x))
(defn- contains-lo [^LongInterval s ^Object x] false)
;;----------------------------------------------------------------
(defn- contains-sb [^ShortInterval s ^Byte x] (.contains s x))
(defn- contains-sd [^ShortInterval s ^Double x] (.contains s x))
(defn- contains-sf [^ShortInterval s ^Float x] (.contains s x))
(defn- contains-si [^ShortInterval s ^Integer x] (.contains s x))
(defn- contains-sl [^ShortInterval s ^Long x] (.contains s x))
(defn- contains-ss [^ShortInterval s ^Short x] (.contains s x))
(defn- contains-so [^ShortInterval s ^Object x] false)
;;----------------------------------------------------------------
;; cond case ordering hand optimize for existing benchmarks
(defn contains? [s x] 
  (cond 
    (instance? IntegerInterval s)
    (cond (instance? Integer x) (contains-ii s x)
          (instance? Double x) (contains-id s x)
          (instance? Byte x) (contains-ib s x)
          (instance? Float x) (contains-if s x)
          (instance? Long x) (contains-il s x)
          (instance? Short x) (contains-is s x)
          :else (contains-io s x))
    
    (instance? DoubleInterval s)
    (cond (instance? Integer x) (contains-di s x)
          (instance? Double x) (contains-dd s x)
          (instance? Byte x) (contains-db s x)
          (instance? Float x) (contains-df s x)
          (instance? Long x) (contains-dl s x)
          (instance? Short x) (contains-ds s x)
          :else (contains-do s x))
    
    (instance? java.util.Set s) (contains-so s x)
    
    (instance? ByteInterval s)
    (cond (instance? Integer x) (contains-bi s x)
          (instance? Double x) (contains-bd s x)
          (instance? Byte x) (contains-bb s x)
          (instance? Float x) (contains-bf s x)
          (instance? Long x) (contains-bl s x)
          (instance? Short x) (contains-bs s x)
          :else (contains-bo s x))
    
    (instance? FloatInterval s)
    (cond (instance? Integer x) (contains-fi s x)
          (instance? Double x) (contains-fd s x)
          (instance? Byte x) (contains-fb s x)
          (instance? Float x) (contains-ff s x)
          (instance? Long x) (contains-fl s x)
          (instance? Short x) (contains-fs s x)
          :else (contains-fo s x))
    
    (instance? LongInterval s)
    (cond (instance? Integer x) (contains-li s x)
          (instance? Double x) (contains-ld s x)
          (instance? Byte x) (contains-lb s x)
          (instance? Float x) (contains-lf s x)
          (instance? Long x) (contains-ll s x)
          (instance? Short x) (contains-ls s x)
          :else (contains-lo s x))
    
    (instance? ShortInterval s)
    (cond (instance? Integer x) (contains-si s x)
          (instance? Double x) (contains-sd s x)
          (instance? Byte x) (contains-sb s x)
          (instance? Float x) (contains-sf s x)
          (instance? Long x) (contains-sl s x)
          (instance? Short x) (contains-ss s x)
          :else (contains-so s x))
    
    :else (no-method "contains?" s x)))
;;----------------------------------------------------------------
;; intersects
;;----------------------------------------------------------------
(defn- intersectsDD? [^DoubleInterval s0 ^DoubleInterval s1] 
  (.intersects s0 s1))
;;----------------------------------------------------------------
(defn- intersectsDI? [^DoubleInterval s0 ^IntegerInterval s1] 
  (.intersects s1 s0))
;;----------------------------------------------------------------
(defn- intersectsDS? [^DoubleInterval s0 ^java.util.Set s1] 
  (.intersects s0 s1))
;;----------------------------------------------------------------
(defn- intersectsID? [^IntegerInterval s0 ^DoubleInterval s1] 
  (.intersects s0 s1))
;;----------------------------------------------------------------
(defn- intersectsII? [^IntegerInterval s0 ^IntegerInterval s1] 
  (.intersects s0 s1))
;;----------------------------------------------------------------
(defn- intersectsIS? [^IntegerInterval s0 ^java.util.Set s1] 
  (.intersects s0 s1))
;;----------------------------------------------------------------
(defn- intersectsSD? [^java.util.Set s0 ^DoubleInterval s1] 
  (.intersects s1 s0))
;;----------------------------------------------------------------
(defn- intersectsSI? [^java.util.Set s0 ^IntegerInterval s1] 
  (.intersects s1 s0))
;;----------------------------------------------------------------
(defn- intersectsSS? [^java.util.Set s0 ^java.util.Set s1] 
  (not (Collections/disjoint s0 s1)))
;;----------------------------------------------------------------
(defn intersects? [s0 s1] 
  (cond 
    (instance? IntegerInterval s0)
    (cond (instance? IntegerInterval s1) (intersectsII? s0 s1)
          (instance? DoubleInterval s1) (intersectsID? s0 s1)
          (instance? java.util.Set s1) (intersectsIS? s0 s1)
          :else (no-method "intersects?" s0 s1))
    
    (instance? DoubleInterval s0)
    (cond (instance? IntegerInterval s1) (intersectsDI? s0 s1)
          (instance? DoubleInterval s1) (intersectsDD? s0 s1)
          (instance? java.util.Set s1) (intersectsDS? s0 s1)
          :else (no-method "intersects?" s0 s1))
    
    (instance? java.util.Set s0)
    (cond (instance? IntegerInterval s1)
          (intersectsSI? s0 s1)
          (instance? DoubleInterval s1)
          (intersectsSD? s0 s1)
          (instance? java.util.Set s1)
          (intersectsSS? s0 s1)
          :else (no-method "intersects?" s0 s1))
    
    :else (no-method "intersects?" s0 s1)))
;;----------------------------------------------------------------
