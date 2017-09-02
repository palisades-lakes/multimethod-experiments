package palisades.lakes.dynalin.java;

import java.util.Arrays;

import clojure.lang.IFn;

/** minimal immutable lookup table with linear search.
 *
 * @author palisades dot lakes at gmail dot com
 * @since 2017-08-31
 * @version 2017-09-02
 */

@SuppressWarnings("unchecked")
public final class MethodCache {

  private final Object[] signatures;
  private final IFn[] methods;

  //--------------------------------------------------------------

  private final int index (final Object k) {
    for (int i=0;i<signatures.length;i++) {
      if (k.equals(signatures[i])) { return i; } }
    return -1; }

  public final IFn get (final Object k) {
    for (int i=0;i<signatures.length;i++) {
      if (k.equals(signatures[i])) { return methods[i]; } }
    return null; }

  //--------------------------------------------------------------
  /** private because keeps unsafe reference to mutable args.
   */
  
  private MethodCache (final Object[] ks, 
                       final IFn[] vs) {
    signatures = ks;
    methods = vs; }

  public static final MethodCache empty () {
    return new MethodCache(new Object[0], new IFn[0]); }

  public final MethodCache assoc (final Object k,
                                  final IFn m) {
    final int n = signatures.length;
    final int i = index(k);
    if (0 <= i) {
      final Object[] ks = Arrays.copyOf(signatures,n);
      final IFn[] ms = Arrays.copyOf(methods,n);
      ks[i] = k;
      ms[i] = m;
      return new MethodCache(ks,ms); }
    
    final Object[] ks = Arrays.copyOf(signatures,n+1);
    final IFn[] ms = Arrays.copyOf(methods,n+1);
    ks[n] = k;
    ms[n] = m;
    System.out.println(k.toString() + " " + m + " " + n);
    return new MethodCache(ks,ms); }

  //--------------------------------------------------------------
}
