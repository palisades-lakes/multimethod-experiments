# defmultix

Experiments with implementation and design variations related to
Clojure's `defmulti`/`defmethod`/`MultiFn`.

Clojure provides about a dozen competing 
variations on 'object-oriented' or 'polymorphic' functionality, 
including:
`definterface`, `defmulti`/`defmethod`, multi-arity `defn`,
`defprotocol`, `defrecord`, `defstruct`, `gen-class`, `reify`, and 
`proxy`.
See Chas Emerick's [Flowchart for choosing the right Clojure type definition form](https://cemerick.com/2011/07/05/flowchart-for-choosing-the-right-clojure-type-definition-form/) 
for a comparison and evaluation of when to use which.
(Note that it doesn't include `defmulti`/`defmethod`.)

Of the non-deprecated ones, `defmulti`/`defmethod` seems to one of 
the least used (though I have no hard data for that). 
This may in part be due to the general advice on the web, 
which is to use `defprotocol` rather than `defmulti`, because
`defmutli` is 'slow' by comparison.

The motivation for this project was. first, to create some realistic
enough benchmarks (at least for the kinds of problems I work on)
to measure the cost of using 'defmulti`  versus various alternatives.
Second, I wanted to understand the current Clojure implementation.
Third, I was curious whether significant performance improvements
can be achieved with small changes to the existing implementation,
or as a library layered on top of Clojure.

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
of 2 geometric objects. This is reasonably representative of work
I do --- suggestions for benchmarks that better reflect usage 
patterns would be appreciated.

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

Independent seeds for the pseudo-random number generators 
(see [seeds](src/main/resources/seeds) ensure
the same data is generated on each run, as long as all the data is 
generated in a single thread, in the same order.

## Baselines

### Static single method case --- 100% repeat of the same method call

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
(see [multi.clj](src/main/clojure/defmultix/sets/multi.clj)).

The benchmark counts the number of intersections in
8 x 2<sup>22</sup> pairs of independent
pseudo-random instances of `IntegerInterval`, 
running 2<sup>22</sup> pairs in each of 8 threads
concurrently. 
The reason for running 8 copies of the basic benchmark concurrently
is to check that caches are synchronized correctly, especially given
that any caches may be invalidated at any time by changes to the 
available methods.

implementation | &mu;sec
---------------|----------:
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

### Dynamic 2 method case --- 50% repeats

Running the same benchmark where the first element of each pair
is an instance of `IntegerInterval`and the second is either 
`IntegerInterval` or `DoubleInterval`, at random, is a somewhat more
realistic benchmark.
It requires fully dynamic method lookup.
It has probability 0.5 that the same method is called twice 
in a row, which is likely much less often than in real code.

implementation | &mu;sec
---------------|----------:
`manual-java` | 92.00
`multi` | 1481.94

Both implementations increase by about 10 &mu;sec.
The multimethod version is now 15 times slower, so 'only' 94%
excess overhead from using `defmulti`.

Why does `defmulti`/`defmethod` take so long?

There are good reasons to doubt `hprof` profiling
(see http://dl.acm.org/citation.cfm?id=1806618
and http://www.brendangregg.com/blog/2014-06-09/java-cpu-sampling-using-hprof.html),
but it still can suggest things to try and verify with benchmarks.

Running just `multi` 
(see [profile000.clj](src/scripts/clojure/defmultix/intersects/profile000.clj))
with `-Xrunhprof:cpu=samples,depth=128,thread=y,doe=y`
(see (see [cljp.bat](cljp.bat))
gives:
```
Method Times by Line Number (times exclusive): 220107 ticks
  1: clojure.lang.APersistentVector.hasheq: 54.43% (119807 exclusive)
    2: (APersistentVector.java:160): 54.43% (119807 exclusive)
  1: clojure.lang.APersistentVector.doEquiv: 26.06% (57360 exclusive)
    2: (APersistentVector.java:91): 26.06% (57360 exclusive)
  1: clojure.lang.MultiFn.invoke: 11.2% (24654 exclusive)
    2: (MultiFn.java:233): 7.44% (16378 exclusive)
    2: (MultiFn.java:234): 3.76% (8276 exclusive)
  1: clojure.lang.Var.deref: 2.51% (5516 exclusive)
    2: (Var.java:195): 2.51% (5516 exclusive)
  1: clojure.lang.Util.dohasheq: 2.22% (4893 exclusive)
    2: (Util.java:177): 2.22% (4893 exclusive)
  1: clojure.lang.MultiFn.getFn: 1.24% (2731 exclusive)
    2: (MultiFn.java:154): 1.24% (2731 exclusive)
```
This also shows at least 94% of the time in method lookup,
everything but clojure.lang.MultiFn.invoke line 234.

To go further, we next need to look into the current (1.8.0)
multimethod implementation.

## Clojure 1.8.0 multimethods

### Concepts

Clojure provides functionality similar to the generic functions of 
Common Lisp, Dylan, and other languages, thru 
[Multimethods and Hierarchies](https://clojure.org/reference/multimethods).

Briefly, a _multimethod_ is a function which, when called, first 
applies a _dispatch function_ to its arguments, returning the 
_dispatch value_. 

The set of possible dispatch values is partially
ordered by a relation which is the transitive closure
of a directed acyclic graph, called (in an abuse of English) 
a _hierarchy_. 

A subset of the possible dispatch values
have associated _methods_, themselves functions. 

The second step in calling a multimethod is to find 
the least upper bound(s) of the arguments' dispatch value 
in the set of dispatch values with methods.
If there is no applicable method, or more than one least upper bound, 
an exception is thrown.

Finally, the least upper bound method function is applied 
to the arguments.

Notes: 

- Methods can be added to and removed from a multimethod at any time,
so it is a shared mutable object.

- Edges may be added to and removed from a hierarchy's DAG 
at any time, so it is also a shared mutable object.

- Any approach to cached method lookup requires checking for changes
to the hierarchy on every call, even though those happen rarely.

### Clojure API

#### Primary functions

- __[`defmulti`](https://clojure.github.io/clojure/clojure.core-api.html#clojure.core/defmulti)__:
The key arguments are 

   - _dispatch function:_ Although not documented here,
dispatch functions must return either Java classes, 
Clojure symbols/keywords, or instances
of `IPersistentVector` holding classes or symbols/keywords.
This is documented and enforced only by `clojure.core/derive`.
Using something else will work as long as the dispatch value
matches a method exactly, but will fail otherwise with a not-very-helpful no method found exception.

  - _hierarchy:_ Hierarchies extend the 
Java class/interface inheritance ordering, in the obvious way, 
for dispatch values that are Classes or vectors of Classes.
Clojure provides functions for adding pairs to a hierarchy's ordering relation
in general (`derive`) and for a specific multimethod (`prefer-method`).

  Missing is any (documented) way to hint argument types and 
  return values, essential for performance when either is 
  primitive. Will force boxing of inputs and outputs.

- __[`defmethod`](https://clojure.github.io/clojure/clojure.core-api.html#clojure.core/defmethod)__

  Again missing any (documented) way to hint the 
  return value, essential for performance when either is 
  primitive.

- __[`prefer-method`](https://clojure.github.io/clojure/clojure.core-api.html#clojure.core/prefer-method):__ Resolves multiple least upper bounds without modifying the hierarchy if --- possible conflict if hierarchy is also modified?

#### Less frequently used 

Examining the multimethod:

- __[`prefers`](https://clojure.github.io/clojure/clojure.core-api.html#clojure.core/prefers)__

- __[`methods`](https://clojure.github.io/clojure/clojure.core-api.html#clojure.core/prefer-method)__

- __[`get-method`](https://clojure.github.io/clojure/clojure.core-api.html#clojure.core/get-method)__

Mutating:

- __[`remove-method`](https://clojure.github.io/clojure/clojure.core-api.html#clojure.core/remove-method)__

- __[`remove-all-methods`](https://clojure.github.io/clojure/clojure.core-api.html#clojure.core/remove-method)__

#### Hierarchy related

- __[`make-hierarchy`](https://clojure.github.io/clojure/clojure.core-api.html#clojure.core/make-hierarchy)__

Mutating:

- __[`derive`](https://clojure.github.io/clojure/clojure.core-api.html#clojure.core/derive)__: add an edge to the hierarchy DAG.

- __[`underive`](https://clojure.github.io/clojure/clojure.core-api.html#clojure.core/underive)__: remove an edge from the hierarchy DAG.

Examining a hierarchy

- __[`isa?`](https://clojure.github.io/clojure/clojure.core-api.html#clojure.core/isa?):__ Evaluates a hierarchy's partial order. 

- __[`parents`](https://clojure.github.io/clojure/clojure.core-api.html#clojure.core/parents)__

- __[`ancestors`](https://clojure.github.io/clojure/clojure.core-api.html#clojure.core/ancestors)__

- __[`descendants`](https://clojure.github.io/clojure/clojure.core-api.html#clojure.core/descendants)__


### Implementation

The implementation is divided between 
[core.clj](https://github.com/clojure/clojure/blob/master/src/clj/clojure/core.clj)
(for the Clojure API)
and 
[MultiFn.java](https://github.com/clojure/clojure/blob/master/src/jvm/clojure/lang/MultiFn.java)
(the multimethod object).


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

TODO: consistency between hierarchy and prefers table?
can be changed independently, introducing cycles?

## 01x: Faster method lookup tables

The idea is to plug in alternate implementations for the
method lookup tables: `methodTable`, `preferTable`, and `methodCache`.
These changes are fully forward/backward compatible, 
internal to `MultiFn`, and invisible to the user.

[MultiFn010.java](src/main/java/defmultix/MultiFn010.java)) uses
unmodifiable `HashMap`s in place of the 
`IPersistentMap`/`PersistentHashMap` of the original code.
'Unmodifiable` data structures require some discipline to use safely, because they just wrap mutable objects.

[MultiFn011.java](src/main/java/defmultix/MultiFn011.java)) uses
bare `HashMap` for the tables, but treating them as though they 
were immutable. This removes overhead due to the wrapped 
unmodifiable tables, but requires very strong assumptions about the
discipline of future developers.

[MultiFn012.java](src/main/java/defmultix/MultiFn012.java)) uses
[Guava](https://github.com/google/guava) `ImmutableMap`
and `ImmutableSetMultimap`. This turns out to be the fastest
choice, and also the most convenient 
(especially using a `Multimap` for the preferTable).

Timing for the dynamic 2 method case with 50% repeats:

implementation | &mu;sec
---------------|----------:
`multi010` (unmodifiable)| 370.59
`multi011` (bare) | 353.76
`multi012` (Guava) | 275.08
`manual-java` | 91.04

All 3 choices are roughly 1/5 the time of the original,
but still 3-4 times as long as manual lookup: 2/3 -- 3/4 overhead.
The large performance difference between other table 
implementations and 
and `IPersistentMap` suggests there's an opportunity for 
increased efficiencies in Clojure map classes, which would have wider benefits, but is outside the scope here.

Guava is almost surely too heavy a dependency to add to
Clojure, but creating simple immutable table classes with similar
performance should be easy enough.

[Profiling `multi012`](src/scripts/clojure/defmultix/intersects/profile012.clj) shows:
```
Method Times by Line Number (times exclusive): 82935 ticks
  1: clojure.lang.Var.deref: 38.63% (32036 exclusive)
    2: (Var.java:195): 38.63% (32036 exclusive)
  1: clojure.lang.APersistentVector.doEquals: 18.21% (15104 exclusive)
    2: (APersistentVector.java:47): 18.21% (15104 exclusive)
    2: (APersistentVector.java:49): 0% (0 exclusive)
  1: clojure.lang.APersistentVector.hashCode: 17.36% (14399 exclusive)
    2: (APersistentVector.java:145): 17.36% (14399 exclusive)
  1: clojure.lang.Util.equals: 10.42% (8642 exclusive)
    2: (Util.java:131): 10.42% (8642 exclusive)
```
`APersistentVector.hashCode`, `APersistentVector.doEquals`, and
`Util.equals` (46%) are called because the keys
in the `methodCache` are instances of `IPersistentVector`.

`Var.deref` (39% of the time) is primarily called 
when checking whether the 
hierarchy has changed since the last call, invalidating the 
cache table.

### 02x: Faster method table keys (dispatch values).

This will be backwards compatible, as long as the code continues 
to support `IPersistentVector` dispatch values.
It's not invisible to the user, because taking advantage of it
will require changing dispatch functions.
A potential benefit is that it might force clarification
and better documentation of what is and isn't a legal dispatch value.

To make the code backwards compatible, all the benchmarked implementations here permit the use of any `List` containing
classes or symbols/keywords. The original code is safe because it
requires `IPersistentVector`,
which is immutable and trusted to implement `hashcode` and `equals`
correctly.
Allowing any `List` is **dangerous**, 
because it permits the user to pass mutable objects
as table keys. It is only done here
as a temporary expedient, and **should not** be allowed in 
production code.

The benchmarked variations echo the alternatives tested in the 
previous section:

[MultiFn020.java](src/main/java/defmultix/MultiFn020.java)) uses
unmodifiable `List`s for dispatch values. This is more dangerous
than in the internal table case, because the user may have
a reference to the underlying mutable table.

[MultiFn021.java](src/main/java/defmultix/MultiFn021.java)) uses
bare `ArrayList` for dispatch values. As mentioned above,
unacceptable for production code, but included for timing
purposes.

[MultiFn022.java](src/main/java/defmultix/MultiFn022.java)) uses
[Guava](https://github.com/google/guava) `ImmutableList`.
This is an acceptably safe choice.

[MultiFn023.java](src/main/java/defmultix/MultiFn023.java)) uses
custom immutable [Signature](src/main/java/defmultix/Signature.java)
objects as dispatch values. The motivation for this is explained below.

implementation | &mu;sec
---------------|----------:
`multi` (original)| 1515.71
`multi010` (unmodifiable + `APersistentVector`)| 380.28
`multi011` (bare + `APersistentVector`) | 334.92
`multi012` (Guava + `APersistentVector`) | 274.33
`multi020` (unmodifiable + unmodifiable `ArrayList`)| 567.68
`multi021` (bare + `ArrayList`) | 421.21
`multi022` (Guava + `ImmutableList`) | 326.60
`multi023` (Guava + `Signature`) | 188.80
`manual-java` | 94.10

`APersistentVector` outperforms the Java and Guava lists, as
the table key; only the custom Signature objects are better.

[Profiling `multi022`](src/scripts/clojure/defmultix/intersects/profile022.clj) shows:
```
Method Times by Line Number (times exclusive): 102752 ticks
  1: defmultix.intersects.defs$multi022.invokeStatic: 51.31% (52719 exclusive)
    2: (defs.clj:366): 49% (50347 exclusive)
    2: (defs.clj:368): 1.25% (1280 exclusive)
    2: (defs.clj:367): 1.06% (1092 exclusive)
  1: java.util.AbstractCollection.<init>: 35.08% (36043 exclusive)
    2: (AbstractCollection.java:66): 35.08% (36043 exclusive)
  1: clojure.lang.Var.deref: 11.94% (12268 exclusive)
    2: (Var.java:195): 11.94% (12268 exclusive)
```
THe first entry is not helpful (see comments about hprof issues
above).
The second shows at least 35% of the time taken by construction of
of the dispatch values. 
This suggested looking into 
custom [signature](src/main/java/defmultix/java/Signature2.java) objects: 
essentially fast implementations
for immutable lists of 2, or perhaps a few, elements.
Looking ahead, the implementations used only permit Class entries,
but extending to allow symbols/keywords as well would be easy enough.

[Profiling `multi023`](src/scripts/clojure/defmultix/intersects/profile023.clj) shows:
Method Times by Line Number (times exclusive): 105581 ticks
  1: defmultix.intersects.defs$multi023.invokeStatic: 52.8% (55746 exclusive)
    2: (defs.clj:376): 50.02% (52809 exclusive)
    2: (defs.clj:378): 1.51% (1594 exclusive)
    2: (defs.clj:377): 1.27% (1343 exclusive)
  1: clojure.lang.Var.deref: 44.76% (47254 exclusive)
    2: (Var.java:195): 44.76% (47254 exclusive)

Now about 45% of the time comes from `Var.deref` which is called
to check whether the hierarchy has changed since the last method 
lookup.

### No hierarchy


Specialize dispatch values to only support Java class/interface
inheritance ordering, eliminating the dispatch function and the need 
(in most cases) to instantiate a dispatch value.


### Cache the last dispatch value and method.
eliminating the
`methodCache` table lookup.
 This hurts if the calling pattern is 
such that the same method is rarely called twice in a row.


### Use unsynchronized thread local lookup tables. 
This gives up the ability to redefine methods or their partial ordering at runtime, 
like the [Direct Linking](https://clojure.org/reference/compilation#directlinking) in Clojure 1.8 

5. Modify the Clojure compiler to emit a single dynamically reloaded
class for the multimethod. The (multi)methods would then be implemented
as java methods of the single class, rather than as Clojure functions
(which generate a few java classes each).
Unambiguous type-hinted multimethod calls would compile to a direct call to the corresponding java methods.





