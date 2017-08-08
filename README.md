# multimethod-experiments

Experiments with implementation and design variations related to
Clojure's `defmulti`/`defmethod`/`MultiFn`.

This repository is intended as something like reproducible research,
to document in detail what led to more stable library in 
[faster-multimethods](https://github.com/palisades-lakes/faster-multimethods)
(available from [Clojars](https://clojars.org/palisades-lakes/faster-multimethods)).

## Background

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

The motivation for this project was, first, to create some realistic
enough benchmarks (at least for the kinds of problems I work on)
to measure the cost of using 'defmulti`  versus various alternatives.
Second, I wanted to understand the current Clojure implementation.
Third, I was curious whether significant performance improvements
can be achieved with small changes to the existing implementation,
or as a library layered on top of Clojure.

## Main results (so far)

It's fairly easy to modify the Clojure 1.8.0 version of multimethods
to reduce the method lookup overhead by more than a factor of 10.

* The primary change is to replace the use of Clojure collections
with `java.util` equivalents, taking care to act as though those 
collections were immutable. 

* The other significant change is to optimize an important special
case: dispatch on Java classes only, foregoing the flexibility
of general hierarchies.

![faster-multimethods vs Clojure 1.8.0](docs/figs/bench-plus-defmulti.overhead.png)

For more details, see [benchmarks](docs/benchmarks.md).


For more background on generic functions (aka multimethods), 
from my perspective, 
see [generic_functions](docs/generic_functions.md).


## Usage

The [benchmark scripts](src/scripts/clojure) are intended to be 
edited to address the issue you are investigating.

You can run them however you like. 

One way is to use (modifying for your environment)
[clj.bat](clj.bat), a sample Windows `cmd` script that sets up
the class path and invokes `clojure.main` on it's first argument.
For example:
```
clj src\scripts\clojure\defmultix\intersects\bench.clj
```

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

Copyright © 2017 John Alan McDonald <palisades dot lakes at gmail dot com>

[Apache 2.0](LICENSE)

## Acknowledgements

### ![Yourkit](https://www.yourkit.com/images/yklogo.png)

YourKit is kindly supporting open source projects with its full-featured Java
Profiler.

YourKit, LLC is the creator of innovative and intelligent tools for profiling
Java and .NET applications. Take a look at YourKit's leading software products:

* <a href="http://www.yourkit.com/java/profiler/index.jsp">YourKit Java Profiler</a> and
* <a href="http://www.yourkit.com/.net/profiler/index.jsp">YourKit .NET Profiler</a>.




