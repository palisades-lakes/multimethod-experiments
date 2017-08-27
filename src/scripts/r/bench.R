# multimethod-experiments
# intersects/bench.R
# palisades dot lakes at gmail dot com
# since 2017-07-30
# version 2017-08-27
#-----------------------------------------------------------------
setwd('c:/porta/projects/multimethod-experiments')
#setwd('e:/porta/projects/multimethod-experiments')
source('src/scripts/r/functions.R')
#-----------------------------------------------------------------
#model <- '20HRCTO1WW' # X1
model <- '20ERCTO1WW' # P70
nelements <- 4194304
theday = '2017082[67]-[0-9]{4}'
benchmarks <- c('axpy','diameter','contains','intersects')
#-----------------------------------------------------------------
data <- NULL
for (b in benchmarks) {
  tmp <- read.data(b,model,nelements,theday)
  tmp$benchmark <- b
  data <- rbind(data,tmp) }
data$benchmark <- as.factor(data$benchmark)
data$algorithm <- as.factor(data$algorithm)
data$generators <- as.factor(data$generators)
data$manufacturerModel <- as.factor(data$manufacturerModel)
#-----------------------------------------------------------------
plot.folder <- file.path('docs','figs')
dir.create(plot.folder, showWarnings=FALSE, recursive=TRUE, 
  mode='0777')
#-----------------------------------------------------------------
cols <- c('algorithm','lower.q','median', 'upper.q', 'millisec')
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
  'dynafun',
  'if-then-else instanceof')
bench.multi.algs <- c('clojure 1.8.0',bench.algs)

overhead.algs <- c(
  'dynafun',
  'hashmap tables',
  'non-volatile cache',
  'Signature dispatch-value',
  'no hierarchy')
overhead.multi.algs <- c('clojure 1.8.0',overhead.algs)

bench1.algs <- unique(c(baseline.algs,bench.algs))
bench1.multi.algs <-  c('clojure 1.8.0',bench1.algs)

baselines.plus.defmulti <- bench1[(bench1$algorithm %in% baseline.multi.algs),cols]
baselines.only <- bench1[(bench1$algorithm %in% baseline.algs),cols]
html.table(data=baselines.only,fname='baselines',n=nelements)
html.table(data=baselines.plus.defmulti,fname='baselines-plus-defmulti',n=nelements)
md.table(data=baselines.only,fname='baselines',n=nelements)
md.table(data=baselines.plus.defmulti,fname='baselines-plus-defmulti',n=nelements)
quantile.plot(data=baselines.only,fname='baselines',palette='Dark2')
quantile.plot(data=baselines.plus.defmulti,fname='baselines-plus-defmulti',palette='Dark2')
overhead.plot(
  data=bench1[(bench1$algorithm %in% overhead.algs),],
  fname='bench1',
  palette='Set1')

bench1.plus.defmulti <- bench1[(bench1$algorithm %in% bench1.multi.algs),cols]
bench1.only <- bench1[(bench1$algorithm %in% bench1.algs),cols]
html.table(data=bench1.only,fname='bench1',n=nelements)
html.table(data=bench1.plus.defmulti,fname='bench1-plus-defmulti',n=nelements)
md.table(data=bench1.only,fname='bench1',n=nelements)
md.table(data=bench1.plus.defmulti,fname='bench1-plus-defmulti',n=nelements)
quantile.plot(data=bench1.only,fname='bench1',palette='Dark2')
quantile.plot(data=bench1.plus.defmulti,fname='bench1-plus-defmulti',palette='Dark2')

bench2.plus.defmulti <- bench2[(bench2$algorithm %in% bench.multi.algs),cols]
bench2.only <- bench2[(bench2$algorithm %in% bench.algs),cols]
html.table(data=bench2.only,fname='bench2',n=nelements)
html.table(data=bench2.plus.defmulti,fname='bench2-plus-defmulti',n=nelements)
md.table(data=bench2.only,fname='bench2',n=nelements)
md.table(data=bench2.plus.defmulti,fname='bench2-plus-defmulti',n=nelements)
quantile.plot(data=bench2.only,fname='bench2',palette='Set1')
quantile.plot(data=bench2.plus.defmulti,fname='bench2-plus-defmulti',palette='Set1')
overhead.plot(
  data=bench2[(bench2$algorithm %in% overhead.algs),],
  fname='bench2',
  palette='Set1')
overhead.plot(
  data=bench2[(bench2$algorithm %in% overhead.multi.algs),],
  fname='bench2-plus-defmulti',
  palette='Set1')

bench9.plus.defmulti <- bench9[(bench9$algorithm %in% bench.multi.algs),cols]
bench9.only <- bench9[(bench9$algorithm %in% bench.algs),cols]
html.table(data=bench9.only,fname='bench9',n=nelements)
html.table(data=bench9.plus.defmulti,fname='bench9-plus-defmulti',n=nelements)
md.table(data=bench9.only,fname='bench9',n=nelements)
md.table(data=bench9.plus.defmulti,fname='bench9-plus-defmulti',n=nelements)
quantile.plot(data=bench9.only,fname='bench9',palette='Set1')
quantile.plot(data=bench9.plus.defmulti,fname='bench9-plus-defmulti',palette='Set1')
overhead.plot(
  data=bench9[(bench9$algorithm %in% overhead.algs),],
  fname='bench9',
  palette='Set1')
overhead.plot(
  data=bench9[(bench9$algorithm %in% overhead.multi.algs),],
  fname='bench9-plus-defmulti',
  palette='Set1')

