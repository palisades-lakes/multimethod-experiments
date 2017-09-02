package palisades.lakes.dynarity.java;

import java.util.Arrays;

import clojure.lang.IFn;
import palisades.lakes.dynarity.java.IdentityMethodCache;

/** minimal immutable lookup table with linear search.
 *
 * @author palisades dot lakes at gmail dot com
 * @since 2017-08-31
 * @version 2017-08-31
 */

@SuppressWarnings("unchecked")
public final class IdentityMethodCache {

  private final Class[] classes;
  private final IFn[] methods;

  //--------------------------------------------------------------

  private IdentityMethodCache (final Class[] ks, final IFn[] vs) {
    classes = ks;
    methods = vs; }

  private final int index (final Class c) {
    for (int i=0;i<classes.length;i++) {
      if (c == classes[i]) { return i; } }
    return -1; }

  public final IFn get (final Class c) {
    for (int i=0;i<classes.length;i++) {
      if (c == classes[i]) { return methods[i]; } }
    return null; }

  //--------------------------------------------------------------

  public static final IdentityMethodCache empty () {
    return new IdentityMethodCache(new Class[0], new IFn[0]); }

  public final IdentityMethodCache assoc (final Class c,
                                          final IFn m) {
    final int n = classes.length;
    final int i = index(c);
    if (0 <= i) {
      final Class[] cs = Arrays.copyOf(classes,n);
      final IFn[] ms = Arrays.copyOf(methods,n);
      cs[i] = c;
      ms[i] = m;
      return new IdentityMethodCache(cs,ms); }
    final Class[] cs = Arrays.copyOf(classes,n+1);
    final IFn[] ms = Arrays.copyOf(methods,n+1);
    cs[n] = c;
    ms[n] = m;
    return new IdentityMethodCache(cs,ms); }

  //--------------------------------------------------------------
}
