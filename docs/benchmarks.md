# Multimethod benchmark results

A benchmark for multimethods (or anything else) 
only matters to you
if the pattern of use it embodies is similar to yours.
 
The amount of method lookup overhead you can tolerate depends on 
the cost of the operation.
So far, I'm getting roughly 500ns for 
Clojure 1.8.0 multimethod lookup;  
if you are using Clojure's multimethods to invoke methods
that take milliseconds to run, the Clojure 1.8.0 implementation
is fine.

I'm interested in using multimethods for basic basic geometric
computation. Many important methods will just be a few floating
point operations, costing perhaps 10-100 nanoseconds. 

The first benchmark (see below) is designed to reflect this.

_I'd very much appreciate suggestions/code for benchmarks
reflecting other patterns of use._

[Criterium](https://github.com/hugoduncan/criterium)
is used to run the benchmarks, producing quantile intervals
as well as the mean.

Despite the work criterium does to stabilize the results,
I've found typically about a 10% variation in the mean
from run to run, more more than would be expected if the
criterium samples were close to independent and identically 
distributed. _I don't know why this is, and would appreciate 
suggestions._ One possibility is that it is related to
temperature dependent cpu clock throttling,
but I haven't had the chance to explore this in depth.

The bottom line is that differences in the results less than 10%
or so shouldn't be taken seriously, which is no doubt true in any case.

## general assumptions

Current assumptions underlying my choice of benchmarks.

_Are any of these wrong? Am I missing something, 
especially, am I missing some assumption I'm making
unconsciously? Let me know._

- I want to use generic operations for everything. 
An ideal language would permit defining new methods for 
`(+ a b)` without losing any performance when `a` and `b` are 
primitive `int`. (Lexical type hints might be required, but should
have minimal scope and affect only performance and not semantics,
except that it might be useful if type hints generate runtime
assertions in some cases, causing different exceptions to be 
thrown when an assumption is violated.)

- Methods can be added/redefined dynamically, in multiple threads,
concurrent with method lookup and invocation.

- Operations are invoked much more often than methods are defined,
at least 10<sup>6</sup> times.

- A typical operation has 10s of methods, less often 100s, rarely
much more.

- Concurrent performance is critical. Multi-threaded map-reduce
operations are ubiquitous.

- The correct method is usually the same as the one used in the
most recent call, or at least the most recent call in the same thread.

- Simple argument lists (no destructuring) are good enough, or, 
at least, performance is more important in that case.

- Performance for small arities (1,2,3,..) are more important than
larger ones.

- Pure class-based method definition and lookup is more important
than general hierarchies, or, at least, performance is more
critical in that case.

## Set intersection benchmark 

I've chosen a reduced (1d) version of a common computational 
geometry / geospatial data task: testing for the intersection
of 2 geometric objects. This is reasonably representative of work
I do, at the low end of individual method runtime.

The basic benchmark takes as input 2 arrays of sets,
and counts the number of pairs of corresponding elements that
intersect. The individual benchmarks differ in whether all
elements of the array are the same class, 
the element class is chosen in some deterministic pattern,
or the element type is chosen at random,
giving different probabilities for invoking the same method
repeatedly, checking the performance of various 
method caching strategies
with differing probabilities of cache hit/miss.

The basic benchmark actually runs on `nthreads` pairs of arrays,
concurrently in `nthreads` threads, 
because it's not realistic to assume a multimethod will be called 
in a single thread, and
contention for shared mutable method tables might have a significant
effect, depending on implementation.

(The [default](https://github.com/palisades-lakes/benchtools/blob/master/src/main/clojure/benchtools/core.clj#L42)
 for `nthreads` is 
```clojure
(max 1 (- (.availableProcessors (Runtime/getRuntime)) 2))
```
the idea being to leave one physical cpu free for system tasks,
gc, etc., perhaps reducing the variation in time between runs.

Three kinds of 'geometric' sets 
(from [benchtools](https://github.com/palisades-lakes/benchtools))
are used: 

1. half-open intervals on the real line, specified with 
[`int` valued endpoints](https://github.com/palisades-lakes/benchtools/blob/master/src/main/java/benchtools/java/sets/IntegerInterval.java)

2. half-open intervals with 
[`double` valued endpoints](https://github.com/palisades-lakes/benchtools/blob/master/src/main/java/benchtools/java/sets/DoubleInterval.java)

3. instances of [java.util.Set](https://docs.oracle.com/javase/8/docs/api/index.html?java/util/Set.html)
that happen to contain only instances of `Number`.

With 3 kinds of sets, there are 9 methods to define, as in
[multix.sets.multi/intersects?](https://github.com/palisades-lakes/multimethod-experiments/blob/master/src/main/clojure/multix/sets/multi.clj)

The main benchmark script is 
[multix.scripts.bench](https://github.com/palisades-lakes/multimethod-experiments/blob/master/src/scripts/clojure/multix/intersects/bench.clj).
It has 3 parts:

1. Baseline evaluation, including implementations
with pure static (compile-time) method lookup,
with 100% repeated calls to the same 'method'.

2. All dynamic lookup; first argument is always `IntegerInterval`;
2nd chosen at random from `IntegerInterval` and `DoubleInterval`,
so the same method is repeated 50% of the time.

3. All dynamic lookup; 1st and 2nd arguments are chosen at random
from `IntegerInterval`, `DoubleInterval`, and `SingletonSet`,
so there is 1/9 chance of repeating the same method.

`bench` calls functions defined in 
[multix.scripts.defs](https://github.com/palisades-lakes/multimethod-experiments/blob/master/src/scripts/clojure/multix/intersects/defs.clj).
The other scripts come and go; they are used to profile specific
multimethod implementations to determine where the time is going.

Random sets are created by repeated calls to zero argument set generator functions.
The set generators are constructed by calling functions from 
[benchtools.random.generators](https://github.com/palisades-lakes/benchtools/blob/master/src/main/clojure/benchtools/random/generators.clj).
The constructors take a number generator and return a set generator.
A number generator is a zero argument function that returns a
(usually different) number every time it is called.
Functions that create pseudo-random number generators are found in 
[benchtools.random.prng](https://github.com/palisades-lakes/benchtools/blob/master/src/main/clojure/benchtools/random/prng.clj).
The number generators are explicitly seeded so that repeated runs
of the same benchmark are reproducible --- as long as the order
of data generation odesn't change.

## Baselines

The purpose of the benchmarks here is to measure method lookup
overhead. So we have to decide 'overhead relative to what'?

One possibility is to compare a multimethod implementation
to regular Java method invocation, which requires the type of at
least one of the sets to be known at compile time.

The baseline benchmark is evaluated on 
pairs of `IntegerInterval[]` arrays, so both the types 
of both arguments are known at compile time, so it's possible
to compare static and dynamic method lookup.

The JVM has 3 instructions for calling method 
(Java 8 can't directly generate code using `invokedynamic`):
<dl>
<dt>`invokestatic`</dt>
<dd>
Uses static methods from 
[benchtools.java.sets.Intersects](https://github.com/palisades-lakes/benchtools/blob/master/src/main/java/benchtools/java/sets/Intersects.java).
Assumes both set classes are known (`IntergerInterval`)at compile time.
</dd>
<dt>`invokevirtual`</dt>
<dd>
Uses instance methods called from 
[benchtools.java.sets.Sets](https://github.com/palisades-lakes/benchtools/blob/master/src/main/java/benchtools/java/sets/Sets.java).
Assumes both set classes are known (`IntergerInterval`)at compile time.
</dd>
</dd>
<dt>`invokeinterface`</dt>
<dd>
Uses instance methods called from 
[benchtools.java.sets.Sets](https://github.com/palisades-lakes/benchtools/blob/master/src/main/java/benchtools/java/sets/Sets.java).
Assumes the first argument is an instance of the 
[Set](https://github.com/palisades-lakes/benchtools/blob/master/src/main/java/benchtools/java/sets/Set.java)
interface and the second is`IntergerInterval`.
both set classes are known (`IntergerInterval`)at compile time.
</dd>
</dl>

If we believe that we need dynamic method lookup,
the above functions aren't options 
(though it's useful to get an idea whether the restrictions
of static method lookup have much value at all).

Probably the fastest way to provide dynamic lookup in Java
is a hand-coded if-then-else using `instanceof` and casting
to `invokevirtual` or `invokestatic` the right method.
`manual-java`, which calls
[Intersects/manual](https://github.com/palisades-lakes/benchtools/blob/master/src/main/java/benchtools/java/sets/Intersects.java#L82),
does just this.

The results that follow are from a Thinkpad P70 (Xeon E3-1505M v5), 
90% intervals for the runtimes for 
all 6 concurrent runs of intersection counting over
`IntegerInterval[]` arrays of length 2<sup>22</sup> (4194304)
the baseline methods vs Clojure 1.8.0 multimethods are:

<table>
<caption>runtime in ms</caption>
 <thead>
  <tr>
   <th style="text-align:left;"> algorithm </th>
   <th style="text-align:right;"> 5% </th>
   <th style="text-align:right;"> 50% </th>
   <th style="text-align:right;"> 95% </th>
   <th style="text-align:right;"> mean </th>
  </tr>
 </thead>
<tbody>
  <tr>
   <td style="text-align:left;"> invokestatic </td>
   <td style="text-align:right;"> 68.0 </td>
   <td style="text-align:right;"> 68.5 </td>
   <td style="text-align:right;"> 69.5 </td>
   <td style="text-align:right;"> 68.8 </td>
  </tr>
  <tr>
   <td style="text-align:left;"> invokevirtual </td>
   <td style="text-align:right;"> 67.8 </td>
   <td style="text-align:right;"> 68.2 </td>
   <td style="text-align:right;"> 69.0 </td>
   <td style="text-align:right;"> 68.5 </td>
  </tr>
  <tr>
   <td style="text-align:left;"> invokeinterface </td>
   <td style="text-align:right;"> 68.2 </td>
   <td style="text-align:right;"> 68.3 </td>
   <td style="text-align:right;"> 70.3 </td>
   <td style="text-align:right;"> 69.3 </td>
  </tr>
  <tr>
   <td style="text-align:left;"> instanceof if-then-else </td>
   <td style="text-align:right;"> 71.3 </td>
   <td style="text-align:right;"> 72.3 </td>
   <td style="text-align:right;"> 72.8 </td>
   <td style="text-align:right;"> 72.2 </td>
  </tr>
</tbody>
</table>

![baselines vs Clojure 1.8.0](docs/figs/baselines-plus-defmulti.quantiles.png)

![baselines](docs/figs/baselines.quantiles.png)



