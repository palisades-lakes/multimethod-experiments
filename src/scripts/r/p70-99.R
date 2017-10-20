# multimethod-experiments
# intersects/bench.R
# palisades dot lakes at gmail dot com
# since 2017-10-20
# version 2017-10-20
#-----------------------------------------------------------------
#setwd('c:/porta/projects/multimethod-experiments')
setwd('e:/porta/projects/multimethod-experiments')
source('src/scripts/r/functions.R')
#-----------------------------------------------------------------
#model <- '20HRCTO1WW' # X1
model <- '20ERCTO1WW' # P70
nelements <- 4194304
nelements <- 1048576
theday = '201710[12][90]-[0-9]{4}'
benchmarks <- c('diameter','contains','intersects','axpy')
#-----------------------------------------------------------------
data <- NULL
for (b in benchmarks) {
  tmp <- read.data(benchmark=b, model=model, nelements=nelements,
    theday=theday,parentFolder='data-9.0.1--1.9.0-beta2')
  #tmp$benchmark <- b
  data <- rbind(data,tmp) }
data$benchmark <- factor(data$benchmark,levels=benchmarks)
data$generators <- as.factor(data$generators)
nmethods$generators <- as.factor(nmethods$generators)
data$manufacturerModel <- as.factor(data$manufacturerModel)
data <- merge(x=data,y=nmethods,by='generators')
#-----------------------------------------------------------------
plot.folder <- file.path('docs','p70-99')
dir.create(plot.folder, showWarnings=FALSE, recursive=TRUE, 
  mode='0777')
#-----------------------------------------------------------------
cols <- c('benchmark','algorithm','nmethods',
  'lower.q','median', 'upper.q','millisec',
  'overhead.lower.q','overhead.median', 'overhead.upper.q',
  'overhead.millisec',
  'nanosec','overhead.nanosec')
#-----------------------------------------------------------------
quantile.plot(data=data,fname='all')
md.table(data=data[,cols],fname='all',n=nelements)
fast <- data[data$algorithm!='defmulti',]
quantile.plot(data=fast,fname='fast')
md.table(data=fast[,cols],fname='fast',n=nelements)
dyn <- data[grep('dyn',data$algorithm),cols]
md.table(data=dyn,fname='dynafun',n=nelements)
#-----------------------------------------------------------------
baseline.algs <- c(
  'invokestaticPrimitive',
  'invokevirtualPrimitive',
  'invokeinterfacePrimitive',
  'invokestatic',
  'invokevirtual',
  'invokeinterface',
  'instanceof',
  'instancefn',
  'protocols')
#-----------------------------------------------------------------
baselines <- data[(data$algorithm %in% baseline.algs),]
quantile.plot(data=baselines,fname='baselines')
md.table(data=baselines[,cols],fname='baselines',n=nelements)
#-----------------------------------------------------------------
dynamic.algs <- c(
  'instanceof',
  'instancefn',
  'dynest',
  'dynalin',
  'dynafun',
  'nohierarchy',
  'signatures',
  'hashmaps',
  'protocols')
#-----------------------------------------------------------------
dynamic <- data[(data$algorithm %in% dynamic.algs),]
quantile.plot(data=dynamic,fname='dynamic')
md.table(data=dynamic[,cols],fname='dynamic',n=nelements)
quantile.plot(data=dynamic,fname='dynamic-overhead',
  suffix='overhead relative to instanceof as fraction of defmulti',
  scales='free',
  ylabel='fraction of Clojure 1.8.0 defmulti',
  ymin='overhead.lower.q',
  y='overhead.median',
  ymax='overhead.upper.q')
#-----------------------------------------------------------------
dynamic.multi.algs <- c(dynamic.algs,'defmulti')
#-----------------------------------------------------------------
dynamic.multi <- data[(data$algorithm %in% dynamic.multi.algs),]
quantile.plot(data=dynamic.multi,fname='dynamic-multi')
md.table(data=dynamic.multi[,cols],fname='dynamic-multi',n=nelements)
quantile.plot(data=dynamic.multi,fname='dynamic-multi-overhead',
  suffix='overhead relative to instanceof as fraction of defmulti',
  scales='free',
  ylabel='fraction of Clojure 1.8.0 defmulti',
  ymin='overhead.lower.q',
  y='overhead.median',
  ymax='overhead.upper.q')
#-----------------------------------------------------------------
