# multimethod-experiments
# intersects/bench.R
# palisades dot lakes at gmail dot com
# since 2017-07-30
# version 2017-08-16
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
  'if-then-else instanceof')

baseline.multi.algs <- c(baseline.algs,'clojure 1.8.0')

bench.algs <- c(
  'hashmap tables',
  'non-volatile cache',
  'Signature dispatch-value',
  'no hierarchy',
  'if-then-else instanceof')

bench.multi.algs <- c('clojure 1.8.0',bench.algs)

cols <- c('algorithm','lower.q','median', 'upper.q', 'millisec')
baselines.plus.defmulti <- baselines[(baselines$algorithm %in% baseline.multi.algs),cols]
baselines.only <- baselines[(baselines$algorithm %in% baseline.algs),cols]
bench.plus.defmulti <- bench[(bench$algorithm %in% bench.multi.algs),cols]
bench.only <- bench[(bench$algorithm %in% bench.algs),cols]

html.table(data=baselines.only,fname='baselines',n=nelements)
html.table(data=baselines.plus.defmulti,fname='baselines-plus-defmulti',n=nelements)
html.table(data=bench.only,fname='bench',n=nelements)
html.table(data=bench.plus.defmulti,fname='bench-plus-defmulti',n=nelements)

quantile.plot(data=baselines.only,fname='baselines',palette='Dark2')
quantile.plot(data=baselines.plus.defmulti,fname='baselines-plus-defmulti',palette='Dark2')
quantile.plot(data=bench.only,fname='bench',palette='Set1')
quantile.plot(data=bench.plus.defmulti,fname='bench-plus-defmulti',palette='Set1')

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

options(knitr.table.format='html') 

