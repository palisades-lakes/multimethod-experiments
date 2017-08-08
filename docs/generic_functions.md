## The general generic function problem

__TODO:__ Add Common List, Dylan, Cecil, Multijava, Julia, ... references.
[Multijava](http://multijava.sourceforge.net/)

__TODO:__ [Expression problem](https://en.wikipedia.org/wiki/Expression_problem) --- why it isn't quite right.

__TODO:__ [Verbs and nouns](http://steve-yegge.blogspot.com/2006/03/execution-in-kingdom-of-nouns.html)
Better, but still a metaphor that obscures almost as much as helps.

Attempt neutral terminology: operations (f) and operands (a,b).<br>
Traditional mathematics notation:
<code>a <i>f</i> b</code> or <code><i>f</i>(a,b)</code><br>
Lisp-like languages:
<code>(<i>f</i> a b)</code><br>
java-like languages:
<code>a.<i>f</i>(b)</code> or, rarely, 
<code><i>f</i>.invoke(a,b)</code><br>

Classic examples:

- `(handle event handler)`<br>

Most handlers ignore most events. If dispatch overhead is small 
enough, can let method definition take care of this.
Otherwise behavior is split between event dispatcher logic and 
method lookup logic, leading to bugs and surprises.

- `(render scene-component graphics-device)`

Recursive scene graph rendering stops when the component is 
a primitive the device can handle directly (perhaps the usual case).
But important cases require rendering down to the leaves of the 
graph (eg exporting a scene to [GeoJSON](http://geojson.org/)
or [KML](https://en.wikipedia.org/wiki/Keyhole_Markup_Language)).

- `(add matrix0 matrix1)`

Dense, diagonal, banded, sparse, block structure, ...

- `(intersects? geometry0 geometry1)`



Main issues:

1. The meaning of the operation may depend on the operands. 

For example, adding an affine vector
to an affine vector returns
an affine vector, adding an affine vector to an affine point
returns a point, adding an affine point to an affine point is an
error.

2. The correct implementation depends on the 
implementation of all the arguments. That usually means something 
like the actual, runtime type of each of the arguments, but 
sometimes means the state or even the identity
of the argument (e.g. Cecil, Multijava (__TODO:__ add reference)).

For example, an affine point in __E__<sup>3</sup> might be 
represented with the minimal 3 floating point coordinates, 
or with 4 [homogeneous](https://en.wikipedia.org/wiki/Homogeneous_coordinates)
coordinates. The 3-dimensional representation is a natural first 
choice, with obvious space and time advantages. 
Despite that, the homogeneous representation, where equivalence classes
of 4d vectors are used to represent 3d points,
pretty much dominates applications in computer graphics and vision.
The primary reason is that affine and projective transformations of
the 3d euclidean space correspond to division-free linear 
transformations on the 4d linear (vector) space.

3. Adequate performance often requires breaking encapsulation
barriers surrounding the arguments. 



4. It should be possible to add new types that participate in existing
operations without modifying existing code, and likewise for adding
new operations on existing types.

### Natural (dynamic) method lookup

At runtime
```clojure
(f a b) -> ((get-method f a b) a b)
```
...unless there are hints on `f`, `a` and `b` that make it possible 
to evaluate `(get-method f a b)` at compile time,
which would have to include freezing of the methods of `f`.

Some languages allow methods to be defined depending on the 
state or identity of the operands. More commonly, 
methods are defined for something like the types of the arguments:
```clojure
(f a b) -> ((get-method f (class a) (class b)) a b)
```


### Bizarre semi-static method lookup used by Java, C++, ...

In a bit over a decade of interviewing SDEs, I found only somewhere
between 1/10 to 1/100 who understood how the unnatural way java/C++/... 
method lookup works:

```java
InterfaceA a = ...;
InterfaceB b = ...;
a.f(b) -> a.getClass().getMethod(f,InterfaceB)) .invoke(a,b)
```
Or, bit more clearly in pseudo-clojure syntax:
```clojure
(let [^InterfaceA a ...
      ^InterfaceB b ...]
  (f a b) -> ((get-method f (class a) InterfaceB) a b))
```
exposing the unnecessary asymmetry, which, I believe, is the root
cause for the ridiculously verbose interface spaghetti 
boilerplate of both java and C++.

Questions: what do the distributions of number of instances per class/interface look like in common java programs?
How about implementing classes per interface? 
Methods per interface?

```java
interface Matrix {
  int nrows ();
  int ncols ();
  double get (int i, int j);
  default public Matrix add (Matrix m) {
    // add the nrows*ncols entries
    // call get(i,j) twice for each.
  ... }
```
```java
class DenseMatrix implements Matrix {
  public DenseMatrix add (DiagonalMatrix m) { 
    // add the min(nrows,ncols) diagonal entries
    // copy the (nrows*ncols - min(nrows,ncols)) non-diagonal... }
  public DenseMatrix add (DenseMatrix m) { 
    // add all nrows*ncols entries... }
  public Matrix add (Matrix m) { 
    // add all nrows*ncols entries 
    // call get(i,j) once for each... }
  }
```
```java
class DiagonalMatrix implements Matrix {
  public DenseMatrix add (DenseMatrix m) { 
    // add the min(nrows,ncols) diagonal entries
    // copy the (nrows*ncols - min(nrows,ncols)) non-diagonal... }
  public DiagonalMatrix add (DiagonalMatrix m) { 
    // just add the min(nrows,ncols)diagonal entries... }
  public Matrix add (Matrix m) { 
    // add the min(nrows,ncols) diagonal entries
    // copy the (nrows*ncols - min(nrows,ncols)) non-diagonal
    // call get(i,j) once for each... }
  }
```

```java
class ProblemSolver {
  ...
  Matrix a = foo(...);
  Matrix b = bar(...);
  Matrix c = a.add(b);
}
```
## A benchmark problem: general set intersection

### Assumptions

- A typical operation has 10s of methods, less often 100s, rarely
much more.

- Operations are invoked much more often than methods are defined,
at least 10<sup>6</sup> times.

- The correct method is usually the same as the one used in the
most recent call, or at least the most recent call in the same thread.

- Methods can be added/redefined dynamically, in multiple threads,
concurrent with method lookup and invocation.

- We want to use dynamic method lookup for everything. 
An ideal language would permit defining new methods for 
`(+ a b)` without losing any performance when `a` and `b` are 
primitive `int`. 

The amount of method lookup overhead we can tolerate depends on 
the cost of the operation --- millisecond lookup overhead doesn't 
matter if the method takes seconds to evaluate.

I've chosen a reduced (1d) version of a common computational 
geometry / geospatial data task: testing for the intersection
of 2 geometric objects. 

In the reduced 1d benchmark version, three kinds of 
'geometric' sets are used: 
half-open intervals on the real line, specified with 
[`int` valued endpoints](src/main/java/defmultix/java/sets/IntegerInterval.java)
and 
[`double` valued endpoints](src/main/java/defmultix/java/sets/DoubleInterval.java),
and instances of `java.util.Set` that happen to contain only
instances of `Number`.

With 3 kinds of set, there are 9 methods to implement.

The functions in 
[defmultix.generators](src/main/clojure/defmultix/sets/generators.clj)
create random sets of the 3 kinds, and arrays of random sets
with differing probability that succeeding elements are the same kind,
from 1/1 thru 1/9 to 0 (so that I can evaluate the value of caching
the last method called under various usage patterns).

Independent seeds for the pseudo-random number generators ensure
the same data is generated on each run, as long as all the data is generated in a single thread, in the same order.

### Baselines

In order to measure the overhead of invoking a Clojure `MultiFn`,
we need a baseline to compare it to.
The [baselines.clj](src/scripts/clojure/defmultix/intersects/baselines.clj)
benchmark runs a `defmulti`/`defmethod` implementation 
against several impractical hand-crafted solutions.
See [defs.clj](src/scripts/clojure/defmultix/intersects/defs.clj):

- `invoke-static` directly calls class methods in 
[`Intersects`](src/main/java/defmultix/java/sets/Intersects.java),
assuming the exact class of all operands is known at compile time.

- `invoke-virtual` directly calls instance methods in 
[`IntegerInterval`](src/main/java/defmultix/java/sets/IntegerInterval.java),
assuming the exact class of all operands is known at compile time.

- `invoke-interface` calls methods from the interface [`Set`](src/main/java/defmultix/java/sets/Set.java),
assuming the target object is an instance of `Set` 
and the exact class of the other operand is known at compile time.

- `manual-java` calls the `manual` method from 
[`Intersects`](src/main/java/defmultix/java/sets/Intersects.java),
which does an if-then-else lookup of the correct class method.

- `manual-clj` is an equivalent clojure manual method lookup.

- `multi` uses a `defmulti`/`defmethod` implementation 
(see [multi.clj](src/main/clojure/defmultix/sets/multi.clj).


Testing intersection for 8 x 2<sup>22</sup> pairs of independent
pseudo-random instances of `IntegerInterval`, 
running 2<sup>22</sup> pairs in each of 8 threads
concurrrently, takes:

implementation | &mu;sec
---------------|----------
`invoke-static`    | 77.72
`invoke-virtual`   | 77.34
`invoke-interface` | 77.42
`manual-java` | 77.71
`manual-clj` | 86.80
`multi` | 1471.50

See [clj.bat](clj.bat) for the JVM options used. 

The baselines using static or semi-static method lookup all take
about 77.5 &mu;sec
Manually implemented dynamic lookup (in Java) adds almost no additional
overhead; manual dynamic lookup in clojure adds about 10% overhead.
Method lookup using `defmulti`/`defmethod` takes about 20 times as
long as any of the other approaches, suggesting about 95% of the time
is overhead.

### Profiling

Why does `defmulti`/`defmethod` take so long?

Running just `multi` 
(see [multi.clj](src/scripts/clojure/defmultix/intersects/mult.clj))
with `-Xrunhprof:cpu=samples,depth=128,thread=y,doe=y`
(see (see [cljp.bat](cljp.bat))
gives:
```
Method Times by Line Number (times exclusive): 139614 ticks
  1: clojure.lang.APersistentVector.hasheq: 50.11% (69958 exclusive)
    2: (APersistentVector.java:160): 50.11% (69958 exclusive)
  1: clojure.lang.APersistentVector.doEquiv: 23.57% (32906 exclusive)
    2: (APersistentVector.java:91): 23.57% (32906 exclusive)
  1: clojure.lang.Var.deref: 10.76% (15028 exclusive)
    2: (Var.java:195): 10.76% (15028 exclusive)
  1: clojure.lang.MultiFn.invoke: 9.67% (13504 exclusive)
    2: (MultiFn.java:234): 9.67% (13504 exclusive)
    2: (MultiFn.java:233): 0% (0 exclusive)
```

This shows at least 85% of the time in method lookup and
a little less than 10% in the method call.

## Notes on Clojure 1.8.0 `defmulti`/`defmethod`

Clojure provides functionality similar to the generic functions of 
Common Lisp, Dylan, and other languages, thru 
[Multimethods and Hierarchies](https://clojure.org/reference/multimethods).

Briefly, a _multimethod_ is a function which, when called, first 
applies a _dispatch function_ to its arguments, returning the 
_dispatch value_. (Somewhat arbitrarily, dispatch values must be
either java classes or Clojure symbols/keywords.)

The set of possible dispatch values is partially
ordered by a relation Clojure calls (in an abuse of language) a _hierarchy_. 
Clojure provides a default global hierarchy which incorporates
java class/interface inheritance ordering, in the obvious way, 
for dispatch values that are Classes or vectors (sequences?) of Classes.
Any hierarchy may be used by multiple multimethods.
Clojure provides functions for adding pairs to a hierarchy's ordering relation
in general (`derive`) and for a specific multimethod (`prefer-method`).

A subset of the possible dispatch values
have associated _methods_, themselves functions. 

The second step
in calling a multimethod is to find the least upper bound(s) of the 
arguments' dispatch value in the set of dispatch values with methods.
If there is no applicable method, or more than one least upper bound, 
an exception is thrown.

Finally, the least upper bound method function is applied to the arguments.

The bulk of the implementation is in 
[MultiFn.java](https://github.com/clojure/clojure/blob/master/src/jvm/clojure/lang/MultiFn.java).


MultiFn depends on the state of a shared mutable object, the 
`hierarchy' (implemented as a mutable reference to an immutable object).

It also maintains 3 internal (mutable) tables:
<dl>
<dt>methodTable</dt>
<dd>maps dispatch values to defined methods.</dd>
<dt>preferTable</dt>
<dd>holds multimethod-specific refinements to the hierarchy's partial order.</dd>
<dt>methodCache</dt>
<dd>caches the result of the least upper bound computation
for the dispatch values from actual multimethod calls.</dd>
</dl>
(The tables are implemented using immutable Clojure persistent maps.
It's the instance variable holding the reference to the map which
is mutable.)

Calling a multimethod consists of
1. Compute the dispatch value.
2. If the hierarchy has changed, clear the `methodCache`.
3. Lookup the dispatch value in the `methodCache`.
3. If not cached, use the `hierarchy` and the `preferTable` to find
  the least upper bound in the `methodTable`, and cache that.
4. Apply the cached method function to the arguments.

The `methodTable`, `hierarchy`, and `preferTable` may be updated
at any time, in any thread, so access has to be synchronized.

## Opportunities

1. Cache the last dispatch value and method, eliminating the
`methodCache` table lookup. This hurts if the calling pattern is 
such that the same method is rarely called twice in a row.

2. Specialize dispatch values to only support java class/interface
inheritance ordering, eliminating the dispatch function and the need 
(in most cases) to instantiate a dispatch value.

3. Use unsynchronized thread local lookup tables. This gives up the ability to redefine methods or their partial ordering at runtime, 
like the [Direct Linking](https://clojure.org/reference/compilation#directlinking) in Clojure 1.8 

4. Use faster, more primitive, probably mutable, data structures internally (eg HashMap rather than PersistentMap).

5. Modify the Clojure compiler to emit a single dynamically reloaded
class for the multimethod. The (multi)methods would then be implemented
as java methods of the single class, rather than as Clojure functions
(which generate a few java classes each).
Unambiguous type-hinted multimethod calls would compile to a direct call to the corresponding java methods.


## Usage

The benchmark scripts are intended to be edited to address the issue
you are investigating.

You can run them however you like. 
One way is to use (probably modifying for your environment)
[clj.bat](clj.bat), a sample Windows `cmd` script that sets up
the class path and invokes `clojure.main` on it's first argument:
```
clj src\scripts\clojure\defmultix\intersects\concurrent.clj
```
Two benchmarks of interest:

- [serial.clj](src/scripts/clojure/defmultix/intersects/serial.clj)
 runs some simple intersection tests in a single thread.
- [concurrent.clj](src/scripts/clojure/defmultix/intersects/concurrent.clj)
 runs the same intersection tests, but in multiple threads.

Both benchmarks permit measuring the overhead of using `defmulti`
with repeated calls to the same method, calls to a random choice
out of a small number of methods, and calls that alternate the matching
method.

Also note [table.clj](src/scripts/clojure/defmultix/intersects/table.clj),
which
can be used to summarize the benchmark results in a table written to a tab-separated text file.

## Installation

Clone the repository and build from source using Maven, 
for example: 
```
mvn package
```

## License

Copyright © 2017 John Alan McDonald <github email account TBD>

Distributed under the Eclipse Public License, the same as Clojure.

 