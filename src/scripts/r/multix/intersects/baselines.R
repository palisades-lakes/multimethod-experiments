# multimethod-experiments
# intersects/baselines.R
# palisades dot lakes at gmail dot com
# since 2017-07-30
# version 2017-07-31
#-----------------------------------------------------------------
setwd('c:/porta/projects/multimethod-experiments')
print(getwd())
source('src/scripts/r/functions.R')
#-----------------------------------------------------------------
script <- 'baselines'
nelements <- 8388608
data.folder <- paste('data/intersects/',script,sep='')
bench.files <- list.files(
  path=data.folder, 
  pattern=paste('LENOVO.*.*.*',nelements,'*.tsv',sep='.'), 
  full.names=TRUE)
print(bench.files)
#-----------------------------------------------------------------
bench <- NULL
for (bench.file in bench.files) {
  tmp <- read.csv(bench.file,sep='\t',as.is=TRUE)
  bench <- rbind(bench,tmp) }
bench$algorithm <- factor(bench$algorithm,
  levels=c(
    'invokestatic',
    'invokevirtual',
    'invokeinterface',
    'manual_java',
    'nested_lookup',
    'signature_lookup',
    'multi'))
#-----------------------------------------------------------------
plot.folder <- file.path('docs','figs')
dir.create(plot.folder, showWarnings=FALSE, recursive=TRUE, 
  mode='0777')
#-----------------------------------------------------------------
quantile.plot(data=bench[(bench$algorithm!='multi'),],
  fname=script)
quantile.plot(
  data=bench,
  fname=paste(script,'plus','defmulti',sep='-'))
