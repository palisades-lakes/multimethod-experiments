package palisades.lakes.dynest.java;

import java.util.Arrays;

import clojure.lang.IFn;

/** minimal immutable lookup table with linear search.
 *
 * @author palisades dot lakes at gmail dot com
 * @since 2017-08-31
 * @version 2017-09-03
 */

@SuppressWarnings("unchecked")
public final class MethodCache1 {

  private final Class[] classes;
  private final IFn[] methods;

  //--------------------------------------------------------------

  public final IFn get (final Class c) {
    for (int i=0;i<classes.length;i++) {
      if (c.equals(classes[i])) { return methods[i]; } }
    return null; }

  //--------------------------------------------------------------
  // construction
  //--------------------------------------------------------------

  private MethodCache1 (final Class[] ks, 
                       final IFn[] vs) {
    classes = ks;
    methods = vs; }
  
  public static final MethodCache1 empty () {
    return new MethodCache1(new Class[0], new IFn[0]); }

  //--------------------------------------------------------------

  private final int index (final Class c) {
    for (int i=0;i<classes.length;i++) {
      if (c.equals(classes[i])) { return i; } }
    return -1; }

  public final MethodCache1 assoc (final Class c,
                                  final IFn m) {
    final int n = classes.length;
    final int i = index(c);
    if (0 <= i) {
      final Class[] cs = Arrays.copyOf(classes,n);
      final IFn[] ms = Arrays.copyOf(methods,n);
      cs[i] = c;
      ms[i] = m;
      return new MethodCache1(cs,ms); }
    final Class[] cs = Arrays.copyOf(classes,n+1);
    final IFn[] ms = Arrays.copyOf(methods,n+1);
    cs[n] = c;
    ms[n] = m;
    System.out.println(c.getSimpleName() + " " + m + " " + n);
    return new MethodCache1(cs,ms); }

  //--------------------------------------------------------------
}
