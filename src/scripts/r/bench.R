# multimethod-experiments
# intersects/bench.R
# palisades dot lakes at gmail dot com
# since 2017-07-30
# version 2017-08-29
#-----------------------------------------------------------------
setwd('c:/porta/projects/multimethod-experiments')
#setwd('e:/porta/projects/multimethod-experiments')
source('src/scripts/r/functions.R')
#-----------------------------------------------------------------
#model <- '20HRCTO1WW' # X1
model <- '20ERCTO1WW' # P70
nelements <- 4194304
#theday = '2017082[89]-[0-9]{4}'
theday = '20170901-[0-9]{4}'
benchmarks <- c('diameter','contains','intersects','axpy')
#-----------------------------------------------------------------
data <- NULL
for (b in benchmarks) {
  tmp <- read.data(b,model,nelements,theday)
  #tmp$benchmark <- b
  data <- rbind(data,tmp) }
data$benchmark <- factor(data$benchmark,levels=benchmarks)
data$generators <- as.factor(data$generators)
nmethods$generators <- as.factor(nmethods$generators)
data$manufacturerModel <- as.factor(data$manufacturerModel)
data <- merge(x=data,y=nmethods,by='generators')
#-----------------------------------------------------------------
plot.folder <- file.path('docs','figs')
dir.create(plot.folder, showWarnings=FALSE, recursive=TRUE, 
  mode='0777')
#-----------------------------------------------------------------
cols <- c('benchmark','algorithm','nmethods',
  'lower.q','median', 'upper.q','millisec',
  'overhead.lower.q','overhead.median', 'overhead.upper.q',
  'overhead.millisec')
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
  'instanceof')
#-----------------------------------------------------------------
baselines <- data[(data$algorithm %in% baseline.algs),]
quantile.plot(data=baselines,fname='baselines')
md.table(data=baselines[,cols],fname='baselines',n=nelements)
#-----------------------------------------------------------------
dynamic.algs <- c(
  'instanceof',
  'dynaarity',
  'dynafun',
  'nohierarchy',
  'signatures',
  'hashmaps')
#-----------------------------------------------------------------
dynamic <- data[(data$algorithm %in% dynamic.algs),]
quantile.plot(data=dynamic,fname='dynamic')
md.table(data=dynamic[,cols],fname='dynamic',n=nelements)
quantile.plot(data=dynamic,fname='dynamic-overhead',
  suffix='overhead relative to instanceof as fraction of defmulti',
  scales='fixed',
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
  scales='fixed',
  ymin='overhead.lower.q',
  y='overhead.median',
  ymax='overhead.upper.q')
#-----------------------------------------------------------------
