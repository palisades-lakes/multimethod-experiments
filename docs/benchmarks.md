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



## Choosing a baseline

## Results

1. `invokestatic` calls `Intersects.(s0,s1)`, assuming the 
exact types 
of `s0` and `s1` are known at compile time, which invokes the 
desired class method via the `invokestatic` JVM instruction.
 
2. `invokevirtual` calls `s0.intersects.(s1)`, assuming the types 
of `s0` and `s1` are known at compile time,
which invokes the desired instance method 
via the `invokevirtual` JVM instruction.

3. `invokeinterface` calls `s0.intersects.(s1)`, assuming the types 
`s0` implements the `Set` interface and the exact type of `s1` is
known at compile time,
which invokes the desired instance method 
via the `invokeinterface` JVM instruction.

4. `manual_java` evaluates a hand-written nested if-then-else to 
determine which method to call using the exact runtime types
of `s0` and `s1`.

5. `signature_lookup` computes a `Signature2` (a specialized
dispatch value) and then uses that to invoke the correct method
via a simple if-the-else.

6. `faster0` is the same as Clojure 1.8.0, except:
   
   1. `methodTable` and `preferTable` are implemented 
   with `java.util.HashMap` instead of `clojure.lang.PersistentHashMap`. 
   Both `HashMap`s are treated as though they were immutable.
   This was done for convenience. Long term, it's probably too
   optimistic to assume future developers will all continue to
   exercise the necessary discipline to keep this correct.
   Wrapping the simple hashmaps as unmodifiable produces a small
   performance hit.
   A simple immutable map implementation would be preferable,
   especially one that specialized for small tables. 
   Using Guava immutable hashmaps has the same performance
   as bare mutable `HashMap`, but adds a fairly heavy dependency.
   `clj-tuple` and `cambrian-collections` point the way to 
   
   Generally, the large performance improvement suggests that there's 
   potential for for improving Clojure's collections more generally.
   
   
   2. The `methodCache` is not `volatile`. This gives a small
   performance improvement. I believe this is safe as long the
   implementing `HashMap` not modified, but replaced. If my 
   reasoning is wrong, I'd appreciate hearing about it.

![Baselines](docs/figs/baselines.png)


![Baselines compared to Clojure 1.8.0](docs/figs/baselines-plus-defmulti.png)

![faster-multimethods 3 options](docs/figs/bench.png)

![faster-multimethods vs Clojure 1.8.0](docs/figs/bench-plus-defmulti.png)



#[faster collections](https://www.factual.com/blog/using-clojure-to-generate-java-to-reimplement-clojure)

[cambrian collections](https://github.com/ztellman/cambrian-collections)

[clj-tuple](https://github.com/ztellman/clj-tuple)

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

implementation | msec
---------------|----------:
`invoke-static`    | 77.72
`invoke-virtual`   | 77.34
`invoke-interface` | 77.42
`manual-java` | 77.71
`manual-clj` | 86.80
`multi` | 1471.50

See [clj.bat](clj.bat) for the JVM options used. 

The baselines using static or semi-static method lookup all take
about 77.5 msec
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

implementation | msec
---------------|----------:
`manual-java` | 92.00
`multi` | 1481.94

Both implementations increase by about 10 msec.
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

implementation | msec
---------------|----------:
`multi010` (unmodifiable)| 372.60
`multi011` (bare) | 345.50
`multi012` (Guava) | 368.55
`manual-java` | 92.35

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

implementation | msec
---------------|----------:
`multi` (original)| 1521.99
`multi010` (unmodifiable + `APersistentVector`)| 372.60
`multi011` (bare + `APersistentVector`) | 345.50
`multi012` (Guava + `APersistentVector`) | 368.55
`multi020` (unmodifiable + unmodifiable `ArrayList`)| 561.51
`multi021` (bare + `ArrayList`) | 365.25
`multi022` (Guava + `ImmutableList`) | 345.75
`multi023` (Guava + `Signature`) | 192.075
`manual-java` | 92.35

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

implementation | msec
---------------|----------:
`multi` (original)| 1521.99
`multi010` (unmodifiable + `APersistentVector`)| 372.60
`multi011` (bare + `APersistentVector`) | 345.50
`multi012` (Guava + `APersistentVector`) | 368.55
`multi020` (unmodifiable + unmodifiable `ArrayList`)| 561.51
`multi021` (bare + `ArrayList`) | 365.25
`multi022` (Guava + `ImmutableList`) | 345.75
`multi023` (Guava + `Signature`) | 192.075
`multi033` (Guava + `Signature` - hierarchy) | 187.49
`multi043` (Guava + `Signature` - hierarchy + thread local cache) | 187.93
`multi053` (Guava + `Signature` - hierarchy + non-volatile cache) | 
`manual-java` | 92.35

### Nested 2 class lookup

implementation | mean msec | q025 msec| q975 msec
---------------|----------:|----------:|----------:
`multi` (original)| 1522 | 1508 | 1537
`multi010` (unmodifiable + `APersistentVector`)| 373 | 363 | 374
`multi011` (bare + `APersistentVector`) | 346 | 339 | 351
`multi012` (Guava + `APersistentVector`) | 369 | 368 | 377
`multi020` (unmodifiable + unmodifiable `ArrayList`)| 562 | 555 | 566
`multi021` (bare + `ArrayList`) | 365 | 351 | 452
`multi022` (Guava + `ImmutableList`) | 346 | 341 | 349
`multi023` (Guava + `Signature`) | 192 | 189 | 195
`multi033` (Guava + `Signature` - hierarchy) | 187 | 184 | 194
`multi043` (Guava + `Signature` - hierarchy + thread local cache) |188|184|191
`multi053` (Guava + `Signature` - hierarchy + non-volatile cache) | | |
`dynamic00` (Immutable dynamic function, nested 2 class cache) |192|189|197
`dynamic01` (Immutable dynamic function, non-volatile cache) | | |
`manual-lookup` | 92 | 91 | 93
`manual-java` | 92 | 91 | 93

### Cache the last dispatch value and method.

eliminating the
`methodCache` table lookup.
 This hurts if the calling pattern is 
such that the same method is rarely called twice in a row.



### Modify the Clojure compiler 
to emit a single dynamically reloaded
class for the multimethod. The (multi)methods would then be implemented
as java methods of the single class, rather than as Clojure functions
(which generate a few java classes each).
Unambiguous type-hinted multimethod calls would compile to a 
direct call to the corresponding java methods.





