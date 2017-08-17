# multimethod-experiments
# intersects/baselines.R
# palisades dot lakes at gmail dot com
# since 2017-07-30
# version 2017-08-16
#-----------------------------------------------------------------
# libraries
#-----------------------------------------------------------------
require('ggplot2')
require('knitr')
require('kableExtra')
#-----------------------------------------------------------------
data.files <- function (
  folder='bench',
  model='20ERCTO1WW',
  generator0='nested_uniform_generator',
  generator1='nested_uniform_generator',
  nelements=4194304) {
  
  data.folder <- paste('data/intersects/',folder,sep='')
  list.files(
    path=data.folder, 
    pattern=paste('LENOVO',model,generator0,generator1,nelements,'*.tsv',sep='.'), 
    full.names=TRUE) }
#-----------------------------------------------------------------
read.data <- function (
  folder='bench',
  model='20ERCTO1WW',
  generator0='nested_uniform_generator',
  generator1='nested_uniform_generator',
  nelements=4194304) {
  
  data <- NULL
  for (f in data.files(folder,model,generator0,generator1,nelements)) {
    print(f)
    tmp <- read.csv(f,sep='\t',as.is=TRUE)
    data <- rbind(data,tmp) }
  
  data$algorithm <- factor(
    data$algorithm,
    levels=c(
      'invokestatic',
      'invokevirtual',
      'invokeinterface',
      'manual_java',
      'signature_lookup',
      'no_hierarchy',
      'signature_dispatch_value',
      'non_volatile_cache',
      'hashmap_tables',
      'multi'
    ),
    labels=c(
      'invokestatic',
      'invokevirtual',
      'invokeinterface',
      'if-then-else instanceof',
      'if-then-else Signature',
      'no hierarchy',
      'Signature dispatch-value',
      'non-volatile cache',
      'hashmap tables',
      'clojure 1.8.0'))
  baseline <- data$millisec[(data$algorithm=='if-then-else instanceof')]
  data$overhead <- data$millisec - baseline
  baseline <- data$overhead[data$algorithm=='clojure 1.8.0']
  data$overhead <- data$overhead / baseline
  
  data }
#-----------------------------------------------------------------
html.table <- function(data,fname,n) {
  html.file <- file.path(
    plot.folder,
    paste(fname,'html',sep='.'))
  cat(
    kable(
      data[order(data$algorithm),],
      format='html',
      digits=1,
      caption=paste('milliseconds for',n,'intersection tests'),
      row.names=FALSE,
      col.names = c('algorithm','5%','50%','95%','mean')),
    file=html.file) }
#-----------------------------------------------------------------
algorithm.colors <- c(
  'invokestatic'='#666666',
  'invokevirtual'='#666666',
  'invokeinterface'='#666666',
  'if-then-else instanceof'='#1b9e77',
  'if-then-else Signature'='#1b9e77',
  'clojure 1.8.0'='#e41a1c',
  'hashmap tables'='#377eb8',
  'non-volatile cache'='#377eb8',
  'Signature dispatch-value'='#377eb8',
  'no hierarchy'='#377eb8')
#-----------------------------------------------------------------
quantile.plot <- function(data,fname,palette='Dark2') {
  plot.file <- file.path(plot.folder,paste(fname,'quantiles','png',sep='.'))
  p <- ggplot(
      data=data,
      aes(x=algorithm, y=median, 
        #fill=algorithm, 
        color=algorithm,  
        ymin=lower.q, 
        ymax=upper.q))  +
    theme_bw() +
    theme(plot.title = element_text(hjust = 0.5)) +
    theme(axis.text.x=element_text(angle=-90,hjust=0,vjust=0.5),
      axis.title.x=element_blank()) + 
    theme(legend.position="none") +
    scale_fill_manual(values=algorithm.colors) +
    scale_color_manual(values=algorithm.colors) +
    #scale_fill_brewer(palette=palette) +
    #scale_color_brewer(palette=palette) +
    ylab('milliseconds') +
    geom_crossbar(width=0.25) +
    ggtitle("[0.5,0.95] quantiles for runtimes") +
    expand_limits(y=0); 
  ggsave(p , 
    device='png', 
    file=plot.file, 
    width=20, 
    height=10.75, 
    units='cm', 
    dpi=300) }
#-----------------------------------------------------------------
overhead.plot <- function(data,fname,palette='Dark2') {
  plot.file <- file.path(plot.folder,paste(fname,'overhead','png',sep='.'))
  p <- ggplot(
      data=data,
      aes(
        x=algorithm, 
        y=overhead, 
      #fill='grey', 
      #color=algorithm,
      ))  +
    theme_bw() +
    theme(plot.title = element_text(hjust = 0.5)) +
    theme(axis.text.x=element_text(angle=-90,hjust=0,vjust=0.5),
      axis.title.x=element_blank()) + 
    theme(legend.position="none") +
    #scale_fill_manual(values=algorithm.colors) +
    #scale_color_manual(values=algorithm.colors) +
    #scale_fill_brewer(palette=palette) +
    #scale_color_brewer(palette=palette) +
    ylab('fraction of clojure 1.8.0') +
    geom_col(width=0.25,fill='gray') +
    ggtitle("method lookup overhead") +
    expand_limits(y=0); 
  ggsave(p , 
    device='png', 
    file=plot.file, 
    width=16, 
    height=8.5, 
    units='cm', 
    dpi=300) }
#-----------------------------------------------------------------
