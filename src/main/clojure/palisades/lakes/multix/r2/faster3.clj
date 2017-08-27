(set! *warn-on-reflection* true) 
(set! *unchecked-math* :warn-on-boxed)
;;----------------------------------------------------------------
(ns palisades.lakes.multix.r2.faster3
  
  {:doc  "benchmarking generic function implementations
         testing 3 arg dispatch and too many methods"
   :author "palisades dot lakes at gmail dot com"
   :since "2017-08-22"
   :version "2017-08-26"}
    
  (:require [palisades.lakes.multimethods.core :as d])

  (:import [palisades.lakes.bench.java.spaces.linear
            LinearFunction Vector]
           [palisades.lakes.bench.java.spaces.linear.r2
            B2 S2 I2 L2 F2 D2 B22 S22 I22 L22 F22 D22]))
;;----------------------------------------------------------------
(d/defmulti ^Vector axpy
  "a*x + y."
  {}
  (fn axpy-dispatch [a x y] (d/extract-signature a x y)))
;;----------------------------------------------------------------
(defmacro defmethods
  [fname arglist & body]
  (let [m (meta arglist)
        [atags a xtags x ytags y] arglist]
    `(let []
       ~@(mapv
           (fn emit-defmethod [[atag xtag ytag]]
             #_(println atag xtag ytag)
             (let [args [(with-meta a {:tag atag})
                         (with-meta x {:tag xtag})
                         (with-meta y {:tag ytag})]
                   d `(d/defmethod 
                        ~fname 
                        (d/signature ~atag ~xtag ~ytag)
                        ~(with-meta args m)
                        ~@body)]
               #_(pp/pprint args)
               #_(pp/pprint d)
               d))
           (for [atag atags
                 xtag xtags
                 ytag ytags]
             [atag xtag ytag])))))
;;----------------------------------------------------------------
(defmethods axpy ^L2 [[B22 S22 I22 L22] a 
                      [B2 S2 I2 L2] x 
                      [B2 S2 I2 L2] y]
  (let [x0 (long (.get0 x))
        x1 (long (.get1 x))]
    (L2/make 
      (+ (* (.get00 a) x0) (* (.get01 a) x1) (.get0 y))
      (+ (* (.get10 a) x0) (* (.get11 a) x1) (.get1 y)))))
;;----------------------------------------------------------------
(defmethods axpy ^D2 [[D22] a 
                      [B2 S2 I2 L2 F2 D2] x 
                      [B2 S2 I2 L2 F2 D2] y]
  (let [x0 (double (.get0 x))
        x1 (double (.get1 x))]
    (D2/make 
      (+ (* (.get00 a) x0) (* (.get01 a) x1) (.get0 y))
      (+ (* (.get10 a) x0) (* (.get11 a) x1) (.get1 y)))))
;;----------------------------------------------------------------
(defmethods axpy ^D2 [[B22 S22 I22 L22 F22 D22] a 
                      [D2] x 
                      [B2 S2 I2 L2 F2 D2] y]
  (let [x0 (double (.get0 x))
        x1 (double (.get1 x))]
    (D2/make 
      (+ (* (.get00 a) x0) (* (.get01 a) x1) (.get0 y))
      (+ (* (.get10 a) x0) (* (.get11 a) x1) (.get1 y)))))
;;----------------------------------------------------------------
(defmethods axpy ^D2 [[B22 S22 I22 L22 F22 D22] a 
                      [B2 S2 I2 L2 F2 D2] x 
                      [D2] y]
  (let [x0 (double (.get0 x))
        x1 (double (.get1 x))]
    (D2/make 
      (+ (* (.get00 a) x0) (* (.get01 a) x1) (.get0 y))
      (+ (* (.get10 a) x0) (* (.get11 a) x1) (.get1 y)))))
;;----------------------------------------------------------------
(defmethods axpy ^F2 [[F22] a 
                      [B2 S2 I2 L2 F2] x 
                      [B2 S2 I2 L2 F2] y]
  (let [x0 (double (.get0 x))
        x1 (double (.get1 x))]
    (D2/make 
      (+ (* (.get00 a) x0) (* (.get01 a) x1) (.get0 y))
      (+ (* (.get10 a) x0) (* (.get11 a) x1) (.get1 y)))))
;;----------------------------------------------------------------
(defmethods axpy ^F2 [[B22 S22 I22 L22 F22] a 
                      [F2] x 
                      [B2 S2 I2 L2 F2] y]
  (let [x0 (double (.get0 x))
        x1 (double (.get1 x))]
    (D2/make 
      (+ (* (.get00 a) x0) (* (.get01 a) x1) (.get0 y))
      (+ (* (.get10 a) x0) (* (.get11 a) x1) (.get1 y)))))
;;----------------------------------------------------------------
(defmethods axpy ^F2 [[B22 S22 I22 L22 F22] a 
                      [B2 S2 I2 L2 F2] x 
                      [F2] y]
  (let [x0 (double (.get0 x))
        x1 (double (.get1 x))]
    (D2/make 
      (+ (* (.get00 a) x0) (* (.get01 a) x1) (.get0 y))
      (+ (* (.get10 a) x0) (* (.get11 a) x1) (.get1 y)))))
;;----------------------------------------------------------------
#_(binding [*print-meta* true] 
    (pp/pprint
      (macroexpand
        '(defmethods axpy [[B22 S22] a 
                           [B2 S2] x 
                           [B2 S2] y]
           (let [x0 (int (.get0 x))
                 x1 (int (.get1 x))]
             (I2/make 
               (+ (* (.get00 a) x0) (* (.get01 a) x1) (.get0 y))
               (+ (* (.get10 a) x0) (* (.get11 a) x1) (.get1 y))))))))
;;----------------------------------------------------------------
