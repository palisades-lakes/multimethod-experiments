# multimethod-experiments
# intersects/baselines.R
# palisades dot lakes at gmail dot com
# since 2017-07-30
# version 2017-07-31
#-----------------------------------------------------------------
# libraries
#-----------------------------------------------------------------
require("ggplot2")
#-----------------------------------------------------------------
# global vars
#-----------------------------------------------------------------
plot.file <- function (folder,u,v,x,y,z,ext) {
  fname <- gsub('(-)+','-',paste(u,v,x,y,z,sep='-'))
  file.path(folder,paste(fname,ext,sep='.')) } 
#-----------------------------------------------------------------
ribbons <- function (data,u,v,x,y,z,ymin,ymax,folder) {
  p <- ggplot(
      data=data, 
      aes_string(x=x, y=y, fill=z, color=z, linetype=z, 
        ymin=ymin, ymax=ymax)) + 
    geom_point() + 
    geom_line() + 
    geom_ribbon(alpha=0.5) + 
    scale_x_log10() + 
    scale_y_log10() + 
    facet_grid(paste(v,'~',u)); 
  ggsave(p + theme_bw(), 
    device='png', 
    file=plot.file(folder,u,v,x,y,z,'ribbons.png'), 
    width=24, 
    height=6, 
    units='cm', 
    dpi=300)}
#-----------------------------------------------------------------
curves <- function (data,u,v,x,y,z,folder) {
  p <- ggplot(
      data=data, 
      aes_string(x=x, y=y, fill=z, color=z, linetype=z)) + 
    geom_point() + 
    geom_line() + 
    scale_x_log10() + 
    scale_y_continuous(trans='log1p')
  if (! (is.null(u) | is.null(v))) {
    p <- p + facet_grid(paste(v,'~',u)) 
    ggsave(p + theme_bw(), 
      device='png', 
      file=plot.file(folder,u,v,x,y,z,'curves.png'), 
      width=24, 
      height=6, 
      units='cm', 
      dpi=300) 
  } else {
    ggsave(p + theme_bw(), 
      device='png', 
      file=plot.file(folder,v,v,x,y,z,'curves.png'), 
      width=24, 
      height=12, 
      units='cm', 
      dpi=300) } }
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
      'multi',
      'hashmap_tables',
      'non_volatile_cache',
      'signature_dispatch_value',
      'no_hierarchy'),
    labels=c(
      'invokestatic',
      'invokevirtual',
      'invokeinterface',
      'instanceof if-then-else',
      'Signature if-then-else',
      'clojure 1.8.0',
      'hashmap tables',
      'non-volatile cache',
      'Signature dispatch-value',
      'no hierarchy'))
 baseline <- data$millisec[(data$algorithm=='instanceof if-then-else')]
 data$overhead <- data$millisec - baseline
 baseline <- data$overhead[data$algorithm=='clojure 1.8.0']
 data$overhead <- data$overhead / baseline
  
  data }
#-----------------------------------------------------------------
algorithm.colors <- c(
  'invokestatic'='#666666',
  'invokevirtual'='#666666',
  'invokeinterface'='#666666',
  'instanceof if-then-else'='#1b9e77',
  'Signature if-then-else'='#1b9e77',
  'clojure 1.8.0'='#e41a1c',
  'hashmap tables'='#377eb8',
  'non-volatile cache'='#377eb8',
  'Signature dispatch-value'='#377eb8',
  'no hierarchy'='#377eb8')
#-----------------------------------------------------------------
quantile.plot <- function(data,fname,palette='Dark2') {
  plot.file <- file.path(plot.folder,paste(fname,'quartiles','png',sep='.'))
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
    ggtitle("[0.5,0.95] interval for runtimes") +
    expand_limits(y=0); 
  ggsave(p , 
    device='png', 
    file=plot.file, 
    width=16, 
    height=8.5, 
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
