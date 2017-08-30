# multimethod-experiments [![Clojars Project](https://img.shields.io/clojars/v/palisades-lakes/multimethod-experiments.svg)](https://clojars.org/palisades-lakes/multimethod-experiments)

_This repository is intended as something like reproducible research,
to document in detail what led to 2 libraries:
- [faster-multimethods](https://github.com/palisades-lakes/faster-multimethods):
faster backwards-compatible alternative to the Clojure 1.8.0
implementation
- [dynamic-functions](https://github.com/palisades-lakes/dynamic-functions):
not backwards compatible, more restricted than Clojure multimethods, but faster still.

Clojure provides about a dozen competing 
variations on 'object-oriented' or 'polymorphic' functionality, 
including:
`definterface`, `defmulti`/`defmethod`, multi-arity `defn`,
`defprotocol`, `defrecord`, `defstruct`, `deftype`, 
`gen-class`, `reify`, and `proxy`.
See Chas Emerick's [Flowchart for choosing the right Clojure type definition form](https://cemerick.com/2011/07/05/flowchart-for-choosing-the-right-clojure-type-definition-form/) 
for a comparison and evaluation of when to use which.
(Note that it doesn't include `defmulti`/`defmethod`.)

Of the non-deprecated ones, `defmulti`/`defmethod` seems to one of 
the least used (though I have no hard data for that). 
This may in part be due to the general advice on the web, 
which is to use `defprotocol` rather than `defmulti`, because
`defmutli` is 'slow' by comparison.

The motivation for this project was, first, to create some realistic
enough benchmarks (at least for the kinds of problems I work on)
to measure the cost of using `defmulti`  versus various alternatives.
Second, I wanted to understand the current Clojure implementation.
Third, I was curious whether significant performance improvements
can be achieved with small changes to the existing implementation,
provided as a library layered on top of Clojure,
or would compiler level changes be required.

For general background on generic functions (aka multimethods), 
from my perspective, 
see [generic_functions](docs/generic_functions.md).

For notes on the Clojure 1.8.0 implementation,
see [Clojure 1.8.0 multimethods](docs/implementation_notes_1.8.0.md).

For descriptions of the current benchmarks, and results, 
see [benchmarks](docs/benchmarks.md).


## Installation

Clone the repository and build from source using Maven, 
for example: 
```
mvn package
```

## License

Copyright © 2017 John Alan McDonald <palisades dot lakes at gmail dot com>

[Apache 2.0](LICENSE)

## Acknowledgments

### ![Yourkit](https://www.yourkit.com/images/yklogo.png)

YourKit is kindly supporting open source projects with its full-featured Java
Profiler.

YourKit, LLC is the creator of innovative and intelligent tools for profiling
Java and .NET applications. Take a look at YourKit's leading software products:

* <a href="http://www.yourkit.com/java/profiler/index.jsp">YourKit Java Profiler</a> and
* <a href="http://www.yourkit.com/.net/profiler/index.jsp">YourKit .NET Profiler</a>.




