# Clojure 1.8.0 multimethods

## Concepts

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

## Clojure API

### Primary functions

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

### Less frequently used 

Examining the multimethod:

- __[`prefers`](https://clojure.github.io/clojure/clojure.core-api.html#clojure.core/prefers)__

- __[`methods`](https://clojure.github.io/clojure/clojure.core-api.html#clojure.core/prefer-method)__

- __[`get-method`](https://clojure.github.io/clojure/clojure.core-api.html#clojure.core/get-method)__

Mutating:

- __[`remove-method`](https://clojure.github.io/clojure/clojure.core-api.html#clojure.core/remove-method)__

- __[`remove-all-methods`](https://clojure.github.io/clojure/clojure.core-api.html#clojure.core/remove-method)__

### Hierarchy related

- __[`make-hierarchy`](https://clojure.github.io/clojure/clojure.core-api.html#clojure.core/make-hierarchy)__

Mutating:

- __[`derive`](https://clojure.github.io/clojure/clojure.core-api.html#clojure.core/derive)__: add an edge to the hierarchy DAG.

- __[`underive`](https://clojure.github.io/clojure/clojure.core-api.html#clojure.core/underive)__: remove an edge from the hierarchy DAG.

Examining a hierarchy

- __[`isa?`](https://clojure.github.io/clojure/clojure.core-api.html#clojure.core/isa?):__ Evaluates a hierarchy's partial order. 

- __[`parents`](https://clojure.github.io/clojure/clojure.core-api.html#clojure.core/parents)__

- __[`ancestors`](https://clojure.github.io/clojure/clojure.core-api.html#clojure.core/ancestors)__

- __[`descendants`](https://clojure.github.io/clojure/clojure.core-api.html#clojure.core/descendants)__


## Implementation

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

**TODO:** consistency between hierarchy and prefers table?
can be changed independently, introducing cycles?




