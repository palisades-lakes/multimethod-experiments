package palisades.lakes.dynest.java;

import java.util.Objects;

import clojure.lang.IFn;

/** minimal immutable lookup table with nested linear search.
 *
 * @author palisades dot lakes at gmail dot com
 * @version 2017-12-13
 */

@SuppressWarnings("unchecked")
public final class MethodCache3 {

  private final Class[] classes0;
  private final Class[][] classes1;
  private final Class[][][] classes2;
  private final IFn[][][] methods;

  //--------------------------------------------------------------

  public final IFn get (final Class c0,
                        final Class c1,
                        final Class c2) {
    for (int i0=0;i0<classes0.length;i0++) {
      if (Objects.equals(c0,classes0[i0])) { 
        final Class[] cs1 = classes1[i0];
        for (int i1=0;i1<cs1.length;i1++) {
          if (Objects.equals(c1,cs1[i1])) {
            final Class[] cs2 = classes2[i0][i1];
            for (int i2=0;i2<cs2.length;i2++) {
              if (Objects.equals(c2,cs2[i2])) {
                return methods[i0][i1][i2]; } } } } } } 
    return null; }

  //--------------------------------------------------------------

  private MethodCache3 (final Class[] ks0, 
                        final Class[][] ks1, 
                        final Class[][][] ks2, 
                        final IFn[][][] ms) {
    classes0 = ks0;
    classes1 = ks1;
    classes2 = ks2;
    methods = ms; }

  public static final MethodCache3 empty () {
    return new MethodCache3(
      new Class[0], 
      new Class[0][], 
      new Class[0][][], 
      new IFn[0][][]); }

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

  private final int index (final int i0,
                           final int i1,
                           final Class c2) {
    final Class[] cs2 = classes2[i0][i1];
    for (int i2=0;i2<cs2.length;i2++) {
      if (Objects.equals(c2,cs2[i2])) { return i2; } }
    return -1; }

  //--------------------------------------------------------------

  private final MethodCache3 addCell (final int i0,
                                      final int i1,
                                      final Class c2) {
    return new MethodCache3(
      Util.copy(classes0),
      Util.copy(classes1),
      Util.append(classes2,i0,i1,c2),
      Util.append(methods,i0,i1,null)); }

  private final MethodCache3 addCell (final int i0,
                                      final Class c1,
                                      final Class c2) {
    return new MethodCache3(
      Util.copy(classes0),
      Util.append(classes1,i0,c1),
      Util.append(classes2,i0,new Class[] { c2 }),
      Util.append(methods,i0,new IFn[] { null })); }

  private final MethodCache3 addCell (final Class c0,
                                      final Class c1,
                                      final Class c2) {
    return new MethodCache3(
      Util.append(classes0,c0),
      Util.append(classes1,new Class[] { c1 }),
      Util.append(classes2,new Class[][] { { c2 } }),
      Util.append(methods,new IFn[][] { { null } })); }

  private final MethodCache3 ensureCell (final Class c0,
                                         final Class c1,
                                         final Class c2) {
    final int i0 = index(c0);
    if (0 > i0) { return addCell(c0,c1,c2); }
    final int i1 = index(i0,c1);
    if (0 > i1) { return addCell(i0,c1,c2); }
    final int i2 = index(i0,i1,c2);
    if (0 > i2) { return addCell(i0,i1,c2); }
    return this; }

  //--------------------------------------------------------------

  private final MethodCache3 set (final Class c0,
                                  final Class c1,
                                  final Class c2,
                                  final IFn m) {
    final int i0 = index(c0);
    assert (0 <= i0);
    final int i1 = index(i0,c1);
    assert (0 <= i1);
    final int i2 = index(i0,i1,c2);
    assert (0 <= i2);

    final IFn[][][] ms = Util.copy(methods);
    ms[i0][i1][i2] = m;
    // defensive copying; not really necessary
    return new MethodCache3(
      Util.copy(classes0),
      Util.copy(classes1),
      Util.copy(classes2),
      ms); }

  //--------------------------------------------------------------
  // unnecessary copying this way, but code is cleaner.
  // Shouldn't be called frequently.
  // re-implement if that isn't true.

  public final MethodCache3 assoc (final Class c0,
                                   final Class c1,
                                   final Class c2,
                                   final IFn m) {
    return ensureCell(c0,c1,c2).set(c0,c1,c2,m); }

  //--------------------------------------------------------------
}
