## Generic Functions

I don't like the word 'multimethod'.

To me it seems to imply that you have normal, regular 'methods',
which are the default, the correct choice in almost all cases.
'Multimethods', on the other hand, are something unusual, obscure,
and special purpose, if not actually weird. 

I hope to show the opposite here: that the
semantics of 'method' in Java ('virtual method` in C++) 
are counter-intuitive at best, while 'multimethod' 
(aka 'generic function' in Common Lisp, Dylan, etc)
semantics are the natural choice.

In over a decade of interviewing SDEs, I've encountered very few
who understood how Java method lookup actually works. 
Perhaps 95% unconsciously assumed it was using 
'multimethod'/'generic function' semantics. 
I started out thinking understanding method lookup semantics
was a minimal requirement for a Java; 
I had to drop that in favor of 
evaluating how quickly the candidate could figure out what was
actually happening, given a code that demonstrated their 
misunderstanding.

### Core idea

I suspect that some of the confusion about method lookup semantics
is due to the asymmetric method invocation syntax in Java and 
similar languages, which itself probably reflects extending
the 'method call' as 'message sending' metaphor 
to contexts where it shouldn't have been.

The actual idea is pretty simple. 

I'm going make an attempt at neutral, terminology, 
to minimize misleading metaphorical baggage.
I'm also going to use a lisp-like syntax, because, after all, we
are talking about Clojure functionality, but also, more important,
because I believe it's better at exposing the intrinsic
symmetries.

Let's say we are assembling a software system 
out of _operations_ (`f`, `g`, ...) which are applied to 
to _operands_ (`a`, `b`, `c`, ...).
I write `(f a b c)` for applying `f` to `a b c`.

I'm not assuming anything about 
whether `a`, `b`, `c` (or `f` for that matter)
are immutable values or containers for mutable state, 
whether `f` returns values or has side effects, 
or whether `f` is a pure function in the sense that it
does the same thing (returns the same value) 
every time it is applied to the same operands.
These are important issues, but orthogonal to what I'm talking 
about here.

(I'm using 3 argument examples to simplify presentation, avoiding
lots of ellipses. 
There's nothing special about 1 vs 2 vs 3 vs ...
for simple argument lists. I'm going to defer dealing with
more complicated argument specification.

I'm also avoiding, for the moment, dealing with generic operations
with multiple arities. The simple approach is to treat each
arity independently. However, it may be useful at times
to define a single method that can handle multiple arities,
via some kind of &rest argument.)

For a _simple operation_, `(f a b c)` means something like:

* push `c b a` on the stack.
* jump to the first instruction of `f` and start executing.

A _generic operation_ is implemented indirectly, via a set of _methods_.
In that case, `(f a b c)` turns into 
`((find-method f a b c) a b c)`, roughly:

* push `c b a f` on the stack.
* jump to the first instruction of `find-method` and start executing,
returning with `c b a method` on the stack
* pop `method` off the stack
* jump to its first instruction and start executing. 

_**TODO**:_ Why 'generic' rather than 'polymorphic'?

**Pro:** Generic operations offer an elegant form of modularity,
permitting us to extend an existing software system
either by adding new operations on existing operands, or new 
operands that can participate in existing operations, 
without touching existing code.

**Con:** In some cases, implementing methods that are efficient 
enough will require breaking the encapsulation of the operands,
exposing what should be private state in scattered locations,
violating standard 'object-oriented' doctrine.

However, the fact is that there are important cases where it is 
impossible to correctly implement an operation on 2 or more 
operands without access to their internal state. 
What happens too often in practice, in languages like Java,
is the private state is made public, losing all control.

Languages with generic functions (multimethods) at least offer
the possibility of tracking and managing multiple operand
encapsulation breaking. And they encourage adding alternate
representations (new operands) rather than  modifying the 
internal representation of an existing operand.

_**TODO**:_ Discuss performance implications on rectangle 
operations with 
`[left top width height]` vs `[left right top bottom]`
as an example for encapsulation breaking.

_**TODO**:_ MultiJava reference for encapsulation breaking.


**Con:** `(find-method f a b c)` is takes extra time compared to 
(somehow) calling the method code directly.

If the time for `find-method` is small compared to to
the time for  `method` itself, then it doesn't matter.

How small is small enough? Of course, smaller is always better,
but I rarely find that speed-up/slow-down less than a factor of 2
has much practical impact on whether an approach is used or not.

A useful goal: be able to add new methods to `(+ a b)`
while retaining the same performance when `a` and `b` are
primitive numbers.


### Examples

* `(handle event handler)`

    Most UI systems are organized to allow adding new handlers for
    existing events. Buy suppose we add a new 3-finger swirly
    gesture and we want existing widgets to respond appropriately?
    
* `(render scene-component graphics-device)`

    The default method for rendering a node in a scene graph is to
    recursively render all its components. The recursion stops 
    when we reach a component the device can handle as a primitive.
    Adding new kinds of scene components is common, but 
    adding support for new kinds 
    devices is important as well. 
    
    Without good support for extending in both operand dimensions,
    we end up with something like [D3js](https://d3js.org/),
    which is excellent at creating new scenes, but only supports
    one output 'device': 
    [SVG](https://en.wikipedia.org/wiki/Scalable_Vector_Graphics)
     --- the primary reason it can't handle
    datasets of even moderate cardinality.

* `(add matrix0 matrix1)`, `(multiply matrix0 matrix1)`, ...

    Practical systems for numerical linear algebra must take   
    advantage of the special structure of the linear functions
    (matrices and more) they manipulate. Otherwise operands that
    should take constant or linear space grow as n<sup>2</sup>
    and operations that should take constant or linear time
    end up n<sup>3</sup> or worse. More importantly, using the 
    wrong representation (eg inverse matrix) can make it impossible 
    to solve many problems, giving answers that have no correct
    significant digits. As a rule, every application area 
    (numerical optimization, statistics, physics, chemistry, ...)
    produces matrices with idiosyncratic special structure
    that need to interoperate with the standard ones: dense, 
    diagonal, banded, general sparse, ...
    
* `(intersects? geometry0 geometry1)`

    See the [benchmarks document](docs/bencharks.md).

### Method lookup

As written above, `(find-method f a b c)` could use arbitrary
logic to determine the method to use --- `find-method` could
in principle be a generic operation itself.

```clojure
(defgeneric foo
  :method-finder bar)
```  

On the other hand, if `(find-method f a b c)` is completely opaque,
that defeats the primary purpose of generic functions,
because there's no way to add methods to an existing operation,
and ensure they are called in the right circumstances.

I believe we can describe the behavior of `find-method` 
in all languages I've seen (at least the default behavior)
by breaking it down into 3 steps:
```clojure
(defn find-method [f a b c]
  (let [k (method-key f a b c)
        a (applicable-methods f k)
        mm (preferred-methods f k a)]
    ...)) 
```

#### `method-key`

##### types and prototypes

One option for `method-key` is just `[a b c]`, 
effectively passing the argument list untouched to the next steps.
This is 
what prototyping languages do
(like [Cecil](https://en.wikipedia.org/wiki/Cecil_(programming_language)).

More commonly, languages restrict what information about `a`, 
`b`, and `c`, (and `f`) can be used to determine the method.
This can be written:
```clojure
(defn method-key [f a b c] \[(q0 a) (q1 b) (q2 c)\])
```
so that methods are defined per
[equivalence class](https://en.wikipedia.org/wiki/Kernel_(set_theory))
of `a b c` under `q0 q1 q2`.
Note that this has a different kernel function for each of the 3
arguments, which may seem needlessly complicated, probably
leading to surprising and difficult to debug behavior.
A better choice is almost surely something like:
```clojure
(defn method-key [f a b c] (mapv q [a b c]))
```
treating all arguments the same.

Note, however, that so-called 'single dispatch 'languages like
Java and C++ use a method-key that looks like:
uses different But this is what Java and C++ do. TO
```clojure
(defn method-key [f a b c] \[(q0 a) (q1 b) (q1 c)\])
```
I'll come back to this below.

In typed languages, something like
`class` is the most common equivalence
kernel function.
(I'm avoiding `type`, because that function in Clojure has 
somewhat complicated behavior, merging Java classes with
explicit `:type` metadata tags, but only for the small subset
of Clojure/Java objects that can carry metadata.)

##### dynamic vs lexical

As written above, `(method-key f a b c)` is called every time
`(f a b c)` is called, and has access only to the values
of `f`, `a`, `b`, and `c`.
This is pure _dynamic method lookup_.

However, to cover all the languages of interest,
we have to consider passing information about the lexical
environment in which `(f a b c)` is evaluated as well.
Consider
```clojure
(defn axpy ^Vector [^LinearFunction a ^Vector x ^Vector y]
  (let [^Vector ax (transform a x)]
    (add ax y)))
``` 
a typical generic operation in a library dealing with
[linear spaces](https://en.wikipedia.org/wiki/Vector_space)
in 1, 2, 3, and higher dimensions.

The lexical environment in which `(transform a x)` is evaluated
tells us the `a` has to be an instance of something implementing
`spaces.linear.LinearFunction` and 
`x` of something implementing `spaces.linear.Vector`.
I'm calling these `(lexical-class a)` and `(lexical-class x)`.

The dynamic environment gives us the values of `a` and `x`,
which, in the languages of most interest here,
carry the actual classes, which, to be painfully clear, I'll call
`(dynamic-class a)` and `(dynamic-class x)`
(Clojure `class` == `dynamic-class`).

In a particular call to `transform` thru `axpy`, 
`a` might be an instance
of `spaces.linear.rn.UniformScaling` 
(represented by a single double)
and `x` might be an instance of 
`spaces.linear.rn.UnitBasisVector`
(a unit vector in one of the canonical coordinate axes, 
represented by a single `int` indicating which axis it is).

Getting the right method in this case (perhaps return a
`BasisVector` represented by one `int` for the axis
and  one `double` for the length, with no arithmetic required) 
requires using the dynamic classes. 

A method defined in terms of the high level 
interfaces `LinearFunction` and `Vector` would require
n<sup>2</sup> multiplications and additions,
and the result would require n doubles to hold
(and subsequent operations that should have been constant time
and space will also scale like n or n<sup>2</sup>.

The problem with static method lookup is that we either:
* specify the exact type of the operands, so that the operation is
no longer generic, or
* risk getting a different method from 
`(get-method f (hinted-class a) (hinted-class b) (hinted-class c))`
than we would have gotten by calling
`(get-method f (class a) (class b) (class c))` at runtime.

Java instance method lookup `a.f(b,c)`: 
```clojure
(defn java-method-key [f a b c]
  [(dynamic-class a) (lexical-class b) (lexical-class c)])
```
Java class (static) method lookup `f.invoke(a,b,c)`: 
```clojure
(defn java-method-key [f a b c]
  [(dynamic-class a) (lexical-class b) (lexical-class c)])
```
   
#### `applicable-methods` (inheritance)

It's generally neither feasible nor desirable to define 
a method for every possible value of `(method-key f a b c)`.
A practical system for generic operations has to be able to 
define methods that are applicable to multiple values of
`method-key`.

Some systems use rules or a `regular expression` associated
with each defined method, to determine which `method-key` values
it matches.

A simpler, and more common, approach is to define methods for
particular values of `method-key` and use a partial order
relation `method-key<=` on `method-key` values to determine which methods 
are applicable.
In other words, a method defined for `k1` is applicable to `k0`
if `(method-key<= k0 k1)`.

In the case where `method-key` looks like `(mapv q [a b c])`,
the ordering on `[(q a) (q b) (q c)]` is derived from
a partial ordering on the values of `q`, `q<=`, by
```clojure
(method-key<= [(q a0) (q b0) (q c0)] `[(q a1) (q b1) (q c1)`])
```
if and only if
```clojure
(and (q<= (q a0) (q a1))
     (q<= (q b0) (q b1))
     (q<= (q c0) (q c1)))
```
The partial ordering `q<=` is usually the transitive closure
of a directed acyclic graph (DAG) over the possible values of `q`.

For dynamic, class-based lookup in a JVM language,
this is
```clojure
(and (.isAssignableFrom (class a1) (class a0))
     (.isAssignableFrom (class b1) (class b0))
     (.isAssignableFrom (class c1) (class c0)))
```
`isAssignableFrom` is the transitive closure of the
union of the Java `extends` and `implements` DAGs.
(Note the change in argument order: `parentClass.isAssignableFrom(childClass)`
vs (class<= childClass parentClass).) 

#### `preferred-methods`

`(applicable-methods f k)` may return 0, 1, or many methods.

When `applicable-methods` is defined by a partial order on method keys
`k`, then it's natural to define the `preferred-methods` as the
minima of `method-key<=` among the applicable methods.

Another way to look at it is to merge `applicable-methods`
and `preferred-methods` into a single step: 
the preferred methods for `k` are the `method-key<=` 
least upper bounds of k` among the methods keys with defined methods.

However you look at it, we end up with a set of preferred methods
that might be empty, have a single elements, or have multiple
elements.

* The single element case is easy --- we're return it.

* The zero element case is also not too hard. 
There are 2 reasonable choices: 
    1. Define a default method, often one that does nothing.
    In the dynamic, class-based JVM case this means defining 
    a method for `f` at `[Object Object Object]`.
    2. Throw an exception. Rely on (automated) unit tests to find 
    missing methods.

* When there are multiple, equally preferred methods, the choice 
is more difficult. Some languages, eg Common Lisp, offer
complex options ordering calls to multiple methods
and combining their results. I think experience showed the
complexity was not worth it.
Clojure multimethods permit adding explicit edges to 
the `method-key<=` DAG to resolve multiple preferred methods.

_**TODO**:_ Dylan et al?

#### Java class (static) methods

`someClass.f(a,b,c)`

```clojure
(defn method-key [f a b c]
  (mapv lexical-class [a b c]))
```
Because it all lexical, `find-method` can be evaluated 
at compile time. A compile error results if there is more than
one preferred method.

#### Java instance methods

`a.f(b,c)`

```clojure
(defn method-key [f a b c]
  [(dynamic-class a) (lexical-class b) (lexical-class c)])
```
Pre-Java 8, the dynamic preference graph  

#### Clojure multimethods

_**TODO**:_ historical references: Common List, Dylan, Cecil,
MultiJava, ...

_**TODO**:_ where does Julia fit?

