package palisades.lakes.dynest.java;

import java.util.Objects;

import clojure.lang.IFn;

/** minimal immutable lookup table with nested linear search.
 *
 * @author palisades dot lakes at gmail dot com
 * @since 2017-09-03
 * @version 2017-09-04
 */

@SuppressWarnings("unchecked")
public final class MethodCache2 {

  private final Class[] classes0;
  private final Class[][] classes1;
  private final IFn[][] methods;

  //--------------------------------------------------------------

  public final IFn get (final Class c0,
                        final Class c1) {
    for (int i0=0;i0<classes0.length;i0++) {
      if (Objects.equals(c0,classes0[i0])) { 
        final Class[] cs1 = classes1[i0];
        for (int i1=0;i1<cs1.length;i1++) {
          if (Objects.equals(c1,cs1[i1])) {
            return methods[i0][i1]; } } } } 
    return null; }

  //--------------------------------------------------------------

  private MethodCache2 (final Class[] ks0, 
                        final Class[][] ks1, 
                        final IFn[][] ms) {
    classes0 = ks0;
    classes1 = ks1;
    methods = ms; }

  public static final MethodCache2 empty () {
    return new MethodCache2(
      new Class[0],
      new Class[0][],
      new IFn[0][]); }

  //--------------------------------------------------------------

  private final int index (final Class c0) {
    for (int i0=0;i0<classes0.length;i0++) {
      if (Objects.equals(c0,classes0[i0])) { return i0; } }
    return -1; }

  private final int index (final int i0,
                           final Class c1) {
    final Class[] cs1 = classes1[i0];
    for (int i1=0;i1<cs1.length;i1++) {
      if (Objects.equals(c1,cs1[i1])) { return i1; } }
    return -1; }

  //--------------------------------------------------------------

  private final MethodCache2 addCell (final Class c0,
                                      final Class c1) {
    return new MethodCache2(
      Util.append(classes0,c0),
      Util.append(classes1,new Class[] { c1 }),
      Util.append(methods,new IFn[] { null })); }

  private final MethodCache2 addCell (final int i0,
                                      final Class c1) {
    return new MethodCache2(
      Util.copy(classes0),
      Util.append(classes1,i0,c1),
      Util.append(methods,i0,null)); }

  private final MethodCache2 ensureCell (final Class c0,
                                         final Class c1) {
    final int i0 = index(c0);
    if (0 > i0) { return addCell(c0,c1); }
    final int i1 = index(i0,c1);
    if (0 > i1) { return addCell(i0,c1); }
    return this; }

  //--------------------------------------------------------------

  private final MethodCache2 set (final Class c0,
                                  final Class c1,
                                  final IFn m) {
    final int i0 = index(c0);
    assert (0 <= i0);
    final int i1 = index(i0,c1);
    assert (0 <= i1);

    final IFn[][] ms = Util.copy(methods);
    ms[i0][i1] = m;
    // defensive copying; not really necessary
    return new MethodCache2(
      Util.copy(classes0),
      Util.copy(classes1),
      ms); }

  //--------------------------------------------------------------

  public final MethodCache2 assoc (final Class c0,
                                   final Class c1,
                                   final IFn m) {
    return ensureCell(c0,c1).set(c0,c1,m); }

  //--------------------------------------------------------------
}
