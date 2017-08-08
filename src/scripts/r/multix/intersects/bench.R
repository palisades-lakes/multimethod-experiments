# multimethod-experiments
# intersects/bench.R
# palisades dot lakes at gmail dot com
# since 2017-07-30
# version 2017-08-07
#-----------------------------------------------------------------
setwd('c:/porta/projects/multimethod-experiments')
print(getwd())
source('src/scripts/r/functions.R')
#-----------------------------------------------------------------
#model <- '20HRCTO1WW' # X1
model <- '20ERCTO1WW' # P70
baseline.generator <- 'integer_interval'
bench.generator <- 'nested_uniform_generator'
#nelements <- 8388608
nelements <- 4194304
folder <- 'bench'
#-----------------------------------------------------------------
baselines <- read.data(folder,model,baseline.generator,baseline.generator,nelements)
bench <- read.data(folder,model,bench.generator,bench.generator,nelements)
#-----------------------------------------------------------------
plot.folder <- file.path('docs','figs')
dir.create(plot.folder, showWarnings=FALSE, recursive=TRUE, 
  mode='0777')
#-----------------------------------------------------------------
baseline.algs <- c(
  'invokestatic',
  'invokevirtual',
  'invokeinterface',
  'instanceof if-then-else',
  'Signature if-then-else')

baseline.multi.algs <- c('clojure 1.8.0',baseline.algs)

bench.algs <- c(
  'instanceof if-then-else',
  'hashmap tables',
  'non-volatile cache',
  'Signature dispatch-value',
  'no hierarchy')

bench.multi.algs <- c('clojure 1.8.0',bench.algs)

quantile.plot(
  data=baselines[(baselines$algorithm %in% baseline.algs),],
  fname='baselines',
  palette='Dark2')
quantile.plot(
  data=baselines[(baselines$algorithm %in% baseline.multi.algs),],
  fname='baselines-plus-defmulti',
  palette='Dark2')
quantile.plot(
  data=bench[(bench$algorithm %in% bench.algs),],
  fname='bench',
  palette='Set1')
quantile.plot(
  data=bench[(bench$algorithm %in% bench.multi.algs),],
  fname='bench-plus-defmulti',
  palette='Set1')

overhead.algs <- c(
  'hashmap tables',
  'non-volatile cache',
  'Signature dispatch-value',
  'no hierarchy')

overhead.multi.algs <- c('clojure 1.8.0',overhead.algs)

overhead.plot(
  data=bench[(bench$algorithm %in% overhead.algs),],
  fname='bench',
  palette='Set1')

overhead.plot(
  data=bench[(bench$algorithm %in% overhead.multi.algs),],
  fname='bench-plus-defmulti',
  palette='Set1')
