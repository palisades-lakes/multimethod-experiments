(set! *warn-on-reflection* true)
(set! *unchecked-math* :warn-on-boxed)
;;----------------------------------------------------------------
(ns palisades.lakes.dynalin.core
  
  {:doc "Dynamic functions aka generic functions aka multimethods. 
         Less flexible than Clojure multimethods
         (no hierarchies, class-based only), but much faster. "
   :author "palisades dot lakes at gmail dot com"
   :version "2017-12-13"}
  
  (:refer-clojure :exclude [defmulti defmethod prefer-method])
  
  (:require [clojure.reflect :as r]
            [clojure.string :as s])
  
  (:import [clojure.lang IFn IMeta]
           [palisades.lakes.dynalin.java DynaFun]
           [palisades.lakes.dynafun.java Classes Signature
            Signature2 Signature3 SignatureN]))
;;----------------------------------------------------------------
;; signatures
;;----------------------------------------------------------------
(defmacro to-signature 
  
  "Return an appropriate implementation of `Signature` for the
   `Class` arguments..

   `palisades.lakes.dynafun.core/signature` can only be used 
   as a dispatch function with dynafun
   defined with `palisades.lakes.dynafun.core/defmulti`."
  
  ([c0] (with-meta c0 {:tag 'Class}))
  ([c0 c1] 
    `(Signature2.
       ~(with-meta c0 {:tag 'Class})
       ~(with-meta c1 {:tag 'Class})))
  ([c0 c1 c2] 
    `(Signature3.
       ~(with-meta c0 {:tag 'Class})
       ~(with-meta c1 {:tag 'Class})
       ~(with-meta c2 {:tag 'Class})))
  ([c0 c1 c2 & cs] 
    `(SignatureN.
       ~(with-meta c0 {:tag 'Class})
       ~(with-meta c1 {:tag 'Class})
       ~(with-meta c2 {:tag 'Class})
       ~(with-meta cs {:tag 'clojure.lang.ArraySeq}))))

(defmacro signature 
  
  "Return an appropriate implementation of `Signature` for the
   arguments, by applying `getClass` as needed.

   `palisades.lakes.dynafun.core/signature` can only be used 
   as a dispatch function with dynafun
   defined with `palisades.lakes.dynafun.core/defmulti`."
  
  ([x0] `(.getClass ~(with-meta x0 {:tag 'Object})))
  ([x0 x1] 
    `(Signature2.
       (.getClass ~(with-meta x0 {:tag 'Object}))
       (.getClass ~(with-meta x1 {:tag 'Object}))))
  ([x0 x1 x2] 
    `(Signature3.
       (.getClass ~(with-meta x0 {:tag 'Object}))
       (.getClass ~(with-meta x1 {:tag 'Object}))
       (.getClass ~(with-meta x2 {:tag 'Object}))))
  ([x0 x1 x2 & xs] 
    `(SignatureN/extract 
       ~x0 ~x1 ~x2 ~with-meta xs {:tag 'clojure.lang.ArraySeq})))
;;----------------------------------------------------------------
;; dynamic functions
;;----------------------------------------------------------------
(defmacro dynafun [mm-name options]
  `(def ~(with-meta mm-name options)
     (DynaFun/empty ~(name mm-name))))
;;----------------------------------------------------------------
;; methods
;;----------------------------------------------------------------
(defn- valid-signature? [signature]
  (or (nil? signature) ;; no arg case
      (class? signature) ;; 1 arg case
      (instance? Signature signature)))

(defn add-method ^DynaFun [^DynaFun f  
                           ^Object sig
                           ^IFn method]
  (assert (valid-signature? sig))
  ;; TODO: check arglist of f for consistency
  ;; TODO: (function-signature IFn) --- probably not possible
  ;; for vanilla IFn
  (.addMethod f sig method))

(defmacro defmethod [f args & body]
  (let [classes (mapv #(:tag (meta %) 'Object) args)
        names (mapv #(s/replace % "." "") classes)
        m (symbol (str f "_" (s/join "_" names)))]
    `(alter-var-root 
       (var ~f) 
       add-method 
       (to-signature ~@classes)
       (fn ~m ~args ~@body))))
;;----------------------------------------------------------------
;; preferences
;;----------------------------------------------------------------
(defn- valid-preference? [signature0 signature1]
  (cond
    (= signature0 signature1) 
    false
    
    (and (class? signature0) 
         (class? signature1)
         (not (Classes/isAssignableFrom ^Class signature0 ^Class signature1))
         (not (Classes/isAssignableFrom ^Class signature1 ^Class signature0))) 
    true
    
    (and (instance? Signature signature0)
         (instance? Signature signature1)
         (not (.isAssignableFrom ^Signature signature0 ^Signature signature1))
         (not (.isAssignableFrom ^Signature signature1 ^Signature signature0)))
    true
    
    :else false))

(defn prefer-method ^DynaFun [^DynaFun f 
                              ^Object signature0 
                              ^Object signature1]
  ;; TODO: check arglist of f for consistency
  ;; TODO: (function-signature IFn) --- probably not possible
  ;; for vanilla IFn
  (assert (valid-preference? signature0 signature1))
  (.preferMethod f signature0 signature1))

(defmacro defpreference [f signature0 signature1]
  `(alter-var-root 
     (var ~f) prefer-method ~signature0 ~signature1))
;----------------------------------------------------------------
