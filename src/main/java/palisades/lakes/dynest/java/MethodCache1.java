package palisades.lakes.dynest.java;

import java.util.Objects;

import clojure.lang.IFn;

/** minimal immutable lookup table with linear search.
 *
 * @author palisades dot lakes at gmail dot com
 * @since 2017-08-31
 * @version 2017-09-04
 */

@SuppressWarnings("unchecked")
public final class MethodCache1 {

  private final Class[] classes0;
  private final IFn[] methods;

  //--------------------------------------------------------------
  // construction
  //--------------------------------------------------------------

  private MethodCache1 (final Class[] ks, 
                        final IFn[] vs) {
    classes0 = ks;
    methods = vs; }

  public static final MethodCache1 empty () {
    return new MethodCache1(new Class[0], new IFn[0]); }

  //--------------------------------------------------------------

  public final IFn get (final Class c) {
    for (int i=0;i<classes0.length;i++) {
      if (Objects.equals(c,classes0[i])) { return methods[i]; } }
    return null; }

  //--------------------------------------------------------------

  private final int index (final Class c) {
    for (int i=0;i<classes0.length;i++) {
      if (Objects.equals(c,classes0[i])) { return i; } }
    return -1; }

  private final MethodCache1 set (final int i0,
                                  final Class c,
                                  final IFn m) {
    final Class[] cs = Util.copy(classes0);
    final IFn[] ms = Util.copy(methods);
    cs[i0] = c;
    ms[i0] = m;
    return new MethodCache1(cs,ms); }

  //--------------------------------------------------------------

  public final MethodCache1 assoc (final Class c,
                                   final IFn m) {
    final int i0 = index(c);
    if (0 <= i0) { return set(i0,c,m); }

    return new MethodCache1(
      Util.append(classes0,c),
      Util.append(methods,m)); }

  //--------------------------------------------------------------
}
