# Generic Functions

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

## Core idea

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

## Examples

The extra cost of `(find-method f a b c)` is justified to the
extent that it simplifies extending existing software system
either by adding new operations on existing operands, or new 
operands that can participate in existing operations.

- `(handle event handler)`

    Most UI systems are organized to allow adding new handlers for
    existing events. Buy suppose we add a new 3-finger swirly
    gesture and we want existing widgets to respond appropriately?
    
- `(render scene-component graphics-device)`

    The default method for rendering a node in a scene graph is to
    recursively render all its components. The recursion stops 
    when we reach a component the device can handle as a primitive.
    Adding new kinds of scene components is common, but new kinds 
    devices is important as well. 
    
    Without good support for extending in both operand dimensions,
    we end up with something like [D3js](https://d3js.org/),
    which is excellent at creating new scenes, but only supports
    one output 'device': 
    [SVG](https://en.wikipedia.org/wiki/Scalable_Vector_Graphics)
     --- the primary reason it can't handle
    datasets of even moderate cardinality.

- `(add matrix0 matrix1)`, `(multiply matrix0 matrix1)`, ...

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
    
- `(intersects? geometry0 geometry1)`

   See the [benchmarks document](docs/bencharks.md).

## Method lookup

### types vs values

As written above, `(find-method f a b c)` could use arbitrary
logic to determine the method to use --- `find-method` could
in principle be a generic operation itself.

In practice, languages restrict what information about `f`, `a`, 
`b`, and `c`, can be used to determine the method.

The  is to use equivalence releation defined on the
possible operation/operand values. 

Some notion of `type` or `class` is the most common equivalence
relat.
In other words, `(find-method f a0 b c)` is guaranteed to 
return the same method as `(find-method f a1 b c)` if
`(= (class a0) (class a1))` (in pseudo-Clojure).

In this case, `(find-method f a b c)` is equivalent to<br>
`(get-method f (class a) (class b) (class c))`.

General value equivalence is also possible: 
the same method is returned if `(= a0 a1)`.

### signatures

It's convenient for discussion to view method lookup as though
it were implemented thru 
`(find-method f a b c)` expanding to <br>
`(get-method f (signature a b c))`,
 where
`(signature a b c)` is <br>
`(map operand-key [a b c])`
(in pseudo-Clojure).

In class-based lookup, `operand-key` is  `class`;
For value, or identity lookup, it's `identity`.

### dynamic vs static

As written above, `(find-method f a b c)` is called every time
`(f a b c)` is called. This is pure _dynamic method lookup_.

Depending on what information is used by `find-method`, 
and what is known ahead of time, the call to
`find-method` can be eliminated, or at least optimized significantly.

Many languages permit compile-time type hints that constrain the
possible values for `(class a)`, which enables some optimization
of the runtime call to `find-method`.

Many languages, unfortunately, require compile-time type specification, and then evaluate 
`(find-method f a b c)` at compile time, rather than at runtime.
This is _static method lookup_. 

The problem with static method lookup is that we either:
* specify the exact type of the operands, so that the operation is
no longer generic, or
* risk getting a different method from 
`(get-method f (hinted-class a) (hinted-class b) (hinted-class c))`
than we would have gotten by calling
`(get-method f (class a) (class b) (class c))` at runtime.

### inheritance

It's not possible to define methods for all possible operands,
definitely not in the case of identity-based method lookup,
and almost always in the case of class-based lookup.

`(find-method f a b c)` has to return a method that is applicable
to `a b c`.

dispatch value

partial ordering

least upper bound

### Java class (static) methods

### Java instance methods

### Clojure multimethods

_**TODO**:_ historical references: Common List, Dylan, Cecil,
MultiJava, ...

_**TODO**:_ where does Julia fit?

