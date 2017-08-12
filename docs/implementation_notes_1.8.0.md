## Clojure 1.8.0 multimethods

This is a set of notes on the Clojure 1.8.0 implementation of multimethods, combining my interpretation of the official
documentation 
with what I think I understand from an examination of the source.

**TODO:** make it clear what parts of this are in the
Clojure documentation, and can therefore be considered
a more-or-less committed API, and which come from examining the 
implementation, and therefore might change with a new release.

**TODO:** I mention a few possible bugs below; I need to 
create test cases to verify the current implementation is in fact incorrect.

### Concepts

Clojure provides functionality similar to the generic functions of 
Common Lisp, Dylan, Cecil, MultiJava, and other languages, thru 
[Multimethods and Hierarchies](https://clojure.org/reference/multimethods).

A _multimethod_ is a Clojure function which, when called;

1. First applies a _dispatch function_ to the arguments, 
returning a _dispatch value_. 

2. Then finds a _method_ from a table mapping dispatch values 
to functions, using a _hierarchy_ to determine which
methods are _applicable,_ and, among those, which is _preferred._

3. Finally, applies the method function to the arguments.
 
#### Legal dispatch values

[Clojure Runtime Polymorphism](https://clojure.org/about/runtime_polymorphism)
states
'Clojure multimethods go further still [than CLOS] to allow the dispatch value to be the result of an arbitrary function of the arguments.'

However, this is not quite true.
Although the logic by which the dispatch value is calculated is
arbitrary, there are significant
restrictions on what a dispatch value can be.

Legal dispatch values are one of (my terminology):

- _atomic_ 

    - instance of `Class`
    - namespace-qualified instance
    of `Named` (`Symbol` or `Keyword`).

- _recursive_  

    instance of `IPersistentVector` 
whose elements are legal dispatch values, 
either atomic or recursive.

#### Dispatch value ordering

The set of atomic dispatch values is partially
ordered by a relation which is the transitive closure
of a directed acyclic graph (DAG) derived, in several steps, from
the multimethod's hierarchy. 

A hierarchy's _atomic DAG_ contains _implicit edges_ 
corresponding to the `parent.isAssignableFrom(child)` 
relation between Java classes. 

Explicit edges, relating pairs of atomic dispatch 
values, may be added to and removed from a hierarchy 
via `derive`/`underive`.
Explicit edges are restricted to child-parent pairs
where the parent is an instance of `Named`
and the child is `Named` or `Class`.

A hierarchy's atomic DAG is implies an _extended DAG_ on 
all dispatch values, both atomic and recursive. 
The extended DAG has all the edges of the atomic DAG,
plus implied edges connecting pairs of recursive dispatch values.
A pair of recursive dispatch values has an child-parent edge 
if they have the same shape 
(the same number of elements at every level of nesting), 
and there is a child-parent edge in the original DAG for every
ordered pair of corresponding leaf atomic elements.

The _multimethod DAG_ is its hierarchy's extended DAG
plus additional explicit edges, created by calling `prefer-method`.
These additional edges affect only the particular multimethod.

Note that the multimethod DAG's edges are not restricted to 
pairs of atomic dispatch values or recursive values with the same shape.
In other words, you can use `prefer-method` to make a method
defined for an atomic dispatch value applicable to a recursive
one, and vice versa.

Finally, every multimethod has a special _default dispatch value_
(defaulting to `:default`) which is an implied root for the 
multimethod DAG.

If we write the multimethod DAG's (transitive closure)
child-parent partial ordering as `DAG<=`,
then a method defined for dispatch value `k1` 
_is applicable_ to dispatch value `k0` if `(DAG<= k0 k1)`.
If there is a method defined for the default dispatch value,
then that is always applicable.

If `k0` and `k1` are distinct and `(DAG<= k0 k1)`,
`k0` is _prefered_ to `k1` .

**Note:** A hierarchy may be shared by many multimethods
and may be modified (via `derive`/`underive`) at any time,
in any thread. Similarly, the multimethod specific preferences may
change at any time (via `prefer-method`).

**Note:** The ordering of atomic dispatch values can be modified
thru the hierarchy, affecting multiple multimethods.
The ordering of recursive dispatch values can only be changed for
individual multimethods.

### Method lookup

Each multimethod contains a table mapping
certain dispatch values to the _defined methods_, 
themselves Clojure functions. 

The second step in calling a multimethod has 3 parts:

1. determine which of the defined methods are
`DAG<=` applicable to the arguments' dispatch value.

    If there are no applicable methods, throw an exception.

2. find the `DAG<=` minima (most preferred) 
among the applicable methods.

    If there is more than one minimal method, throw an exception.

3. apply the chosen method to the arguments.

**Note:** Methods can be added to and removed from the method table
at any time (via `defmethod`/`remove-method` below).

### Implementation

The implementation is divided between 
[core.clj](https://github.com/clojure/clojure/blob/master/src/clj/clojure/core.clj)
(for the Clojure API)
and 
[MultiFn.java](https://github.com/clojure/clojure/blob/master/src/jvm/clojure/lang/MultiFn.java)
(the multimethod object).

#### Clojure API

##### Primary functions/macros

- [(defmulti name docstring? attr-map? dispatch-fn :hierarchy :default)](https://clojure.github.io/clojure/clojure.core-api.html#clojure.core/defmulti)

    - `name`: if `name` resolves to a `Var` whose value is a multimethod,
    nothing happens. In particular, the `dispatch-function`, hierarchy, etc., 
    of the existing multimethod are unchanged, which I expect most
    would find surprising behavior, and has no doubt caused a number
    of wasted hours.  
    
        Otherwise a new `var` is created whose value is the multimethod.
    
    - `docstring?` optional documentation string.
    
    - `attr-map?` optional meta data merged any existing meta data
    on the `name` `Var`.  
    Undocumented how this is used, if at all.
    
    - `dispatch-function`: The flexibility this permits has to be
    balanced against the difficulty of debugging. The returned
    values have to be legal dispatch values, and they have to be
    consistent in some sense with the hierarchy and the multimethod's
    `prefer-method` graph. 
    
        Errors in dispatch 
    value generation don't turn up until method lookup, if then.
    At that point, it's hard to tell if the problem is in the
    `dispatch-function`, the `hierarchy`, or the `prefer-method` graph.
    It's usually best to stick with something equivalent to
    `(mapv type args)`.
    
        **Note:** There's no Clojure API for getting the 
        `dispatch-function` of an existing multimethod.
        I believe it's common to want to guarantee that the same 
        method lookup logic is used used for a collection of 
        related multimethods. This is 
        particularly true when extending an existing library 
        by adding new multimethods, where it is important the added
        functionality is consistent with what is already there.
        Though not sufficient, using the same `dispatch-function`
        is a good start.
    
    - `:hierarchy`: A `Var` whose value is a hierarchy.
    This defaults to a globally shared hierarchy,
    which might be modified any anyone at any time. The requirement
    that `Named` nodes be namespace-qualified provides a little
    protection, but sharing a hierarchy is almost surely a bad idea. 
    For one thing, the larger the hierarchy, the worse the performance.
    
        **Note:** As with the `dispatch-function`, it's very difficult
        to ensure consistent method lookup behavior over a 
        collection of related multimethods if they have different
        hierarchies. However, there's no Clojure
        API for determining what hierarchy a given multimethod
        uses, which makes it very difficult to extend a library
        with new multimethods that behave as expected.
         
   - `:default`: the default dispatch value. 

    Missing is any (documented) way to hint argument types and 
return values, essential for performance when either is 
primitive. This forces boxing of inputs and outputs. 
The overhead of method lookup in Clojure 1.8.0 is large enough
that the additional boxing cost won't matter.
If method lookup overhead were reduced enough, that might no longer
be true.

- [(defmethod name dispatch-value & fn-tail)](https://clojure.github.io/clojure/clojure.core-api.html#clojure.core/defmethod)

    Creates an anonymous function, the equivalent of expanding and 
    evaluating `` `(fn ~@fn-tail)``. Adds it to the method table
    of the multimethod at that is the value of `#'name`
    with the key `dispatch-value`. 
    
    There's is no check that `dispatch-value` is legal.
    
    Unlike `defmulti`, evaluating `defmethod` will replace the
    method associated with `dispatch-value` every time it is called, 
    without warning, which can cause problem difficult to debug.
  
    Again missing any (documented) way to hint the 
  return value, essential for performance when either is 
  primitive.

- [(prefer-method name dispatch0 dispatch1)](https://clojure.github.io/clojure/clojure.core-api.html#clojure.core/prefer-method):
    Used to resolve multiple most preferred method problems.
    
    Design issue: the fact that this affects only a single 
    multimethod is inconsistent makes it harder to get
    consistent behavior from a collection of related multimethods.
    
    **Possible bug:** the 1.8.0 release implementation appears 
    to be incorrect.
    It checks whether the new child-parent pair would introduce 
    a cycle by calling `MultiFn.prefers(dispatch0 dispatch1)`, 
    which appears to have a bug, using the global hierarchy rather 
    than the multimethod's.
    In addition, it doesn't appear to treat recursive dispatch values
    transitively. 

##### Less frequently used 

Examining the multimethod:

- [(prefers name)](https://clojure.github.io/clojure/clojure.core-api.html#clojure.core/prefers)

    Just returns the multimethod's `preferTable`. Not really 
    useful as a public API. Might allow copying the prefers
    behavior of a library multimethod to new multimethods,
    which could be useful if there were a way to get the same 
    hierarchy as well. As is, likely to result in hard to understand
    behavior.
  
- [(methods name)](https://clojure.github.io/clojure/clojure.core-api.html#clojure.core/prefer-method)

    Just returns the multimethod's `methodTable`. Not really useful
  as a public API.

- [(get-method name dispatch)](https://clojure.github.io/clojure/clojure.core-api.html#clojure.core/get-method)

    Could possibly be used to test if there is a method for a 
    given dispatch value, or if 2 dispatch values have the same
    method, but there's not much useful you can do with a method
    function.
    
Mutating:

- [(remove-method name dispatch)](https://clojure.github.io/clojure/clojure.core-api.html#clojure.core/remove-method)

    This seems like something that would rarely be needed, and difficult
    to use correctly.
    
- [(remove-all-methods name)](https://clojure.github.io/clojure/clojure.core-api.html#clojure.core/remove-method)

    I have difficulty imagining a scenario in which I would want to
    call this. 
    
##### Hierarchy related

- [(make-hierarchy)](https://clojure.github.io/clojure/clojure.core-api.html#clojure.core/make-hierarchy)

Mutating:

- [(derive hierarchy child parent)](https://clojure.github.io/clojure/clojure.core-api.html#clojure.core/derive) 

    Only atomic dispatch values, affects all multimethods that use the hierarchy.

- [(underive hierarchy child parent)](https://clojure.github.io/clojure/clojure.core-api.html#clojure.core/underive)

    Only atomic dispatch values, affects all multimethods that use the hierarchy.

Examining a hierarchy:

- [(isa? hierarchy dispatch0 dispatch1)](https://clojure.github.io/clojure/clojure.core-api.html#clojure.core/isa?): 

    Only atomic dispatch values.

- [(parents hierarchy dispatch)](https://clojure.github.io/clojure/clojure.core-api.html#clojure.core/parents)

    Only atomic dispatch values. No equivalent for recursive dispatch values.

- [(ancestors hierarchy dispatch)](https://clojure.github.io/clojure/clojure.core-api.html#clojure.core/ancestors)

    Only atomic dispatch values. No equivalent for recursive dispatch values.

- [(descendants hierarchy dispatch)](https://clojure.github.io/clojure/clojure.core-api.html#clojure.core/descendants)

    Only atomic dispatch values. No equivalent for recursive dispatch values.


#### Java implementation (`clojure.lang.MultiFn`)

The bulk of Clojure 1.8.0's multimethod implementation is in
`clojure.lang.MultiFn`. 

According to [Calling Clojure from Java](https://clojure.org/reference/java_interop#_calling_clojure_from_java) 
the only public API is `clojure.java.api.Clojure` and
`clojure.lang.IFn`, so `clojure.lang.MultiFn` 
may change in future releases.

`clojure.lang.MultiFn` implements `clojure.lang.IFn`,
which means it can be invoked on `Object` arguments and
and return `Object` values.
It doesn't implement any of the primitive-in primitive-out
interfaces (eg `clojure.lang.IFn$DDD`) so numerical operations
will box and unbox.

##### State and mutability

Instances of `MultiFn` are shared mutable objects.

The primary mutable state consists of 3 instances of `IPersistentMap`:
<dl>
<dt><code>methodTable</code></dt>
<dd>maps dispatch values to defined methods.</dd>
<dt><code>preferTable</code></dt>
<dd>holds multimethod-specific refinements to the hierarchy's partial order.</dd>
<dt><code>methodCache</code></dt>
<dd>caches the result of the most-preferred computation
for the dispatch values from actual multimethod calls.</dd>
</dl>
Although the tables are (almost surely) immutable maps,
the fields referring to them are mutable (in fact `volatile`).

In addition, each `MultiFn` depends on the state of 
the shared mutable `hierarchy`.
The `hierarchy` field is `final`;
its value is an `IRef` (actually a `Var`), whose value is
immutable. Mutability comes from changing which immutable map the 
`IRef` points to.

`MultiFn` also uses a mutable `ReentrantReadWriteLock` 
in a final field for synchronizing the internal tables, 
and a `volatile` `cachedHierarchy` field for synchronizing
table updates with external hierarchy changes.

The strictly immutable state in a `MultiFn` is its `final String name`
and its `defaultDispatchVal` (but only if it's a legal dispatch
value, which isn't checked).

The usually immutable state consists of a `final` field holding
the `dispatchFn`. 

It's only 'usually' immutable because it's
easy to create closure `IFn` that are mutable.
For example, although perverse, one might imagine using,
perhaps for testing, a dispatch function that closes 
over a pseudo-random number generator to choose a different
dispatch value each time it's called.

##### Method call outline

Calling a multimethod consists of

1. Compute the dispatch value.

2. If the hierarchy has changed, since the last update to
`methodCache`, clear it.

3. Lookup the dispatch value in the `methodCache` 
(unsynchronized except that `methodCache` is volatile).

4. If not cached, use the `hierarchy` and the `preferTable` to find
  the preferred method in the `methodTable`, and cache that.

5. Apply the cached method function to the arguments (unsynchronized).

##### Step 4. `findAndCacheBestMethod(dispatchVal)`

This happens in 2 parts:

1. Find the best method based on the current state:
    1. Acquire a read lock.
    1. Create local references to `methodTable`, `preferTable`,
    and the `cachedHierarchy`. 
    2. Select the applicable entries from `methodTable` (applicable 
    determined by a call to the Clojure `isa?` function, using the 
    local copy of the cached hierarchy.). 
    3. Find the most preferred of the applicable entries 
    by a partial ordering minima reduction. 
    **Possible bug:** the reduction accumulates a single minimum,
    rather than a set of minima. It throws an exception if it 
    encounters an entry that neither dominates nor is dominated by
    the current best entry in the reduction. However, there may be 
    another entry that dominates both, so the incomparability
    may be irrelevant.
    4. Release the read lock.
    
2. Update the cache if nothing has changed.
    1. Acquire a write lock.
    2. Check if the local references to `methodTable`, `preferTable`,
    and the `cachedHierarchy` match the corresponding fields,
    and that the `cachedHierarchy` matches the value of the
    `hierarchy` `Var`.
    3. If OK, update `methodCache` and return the best value.
    If not, clear the caches, and call `findAndCacheBestMethod`
    again.
    4. Release the write lock and return the best method.
    
    
