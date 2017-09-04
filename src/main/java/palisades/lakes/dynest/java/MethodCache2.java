package palisades.lakes.dynest.java;

import java.util.Arrays;

import clojure.lang.IFn;

/** minimal immutable lookup table with nested linear search.
 *
 * @author palisades dot lakes at gmail dot com
 * @since 2017-09-03
 * @version 2017-09-03
 */

@SuppressWarnings("unchecked")
public final class MethodCache2 {

  private final Class[] classes0;
  private final Class[][] classes1;
  private final IFn[][] methods;

  //--------------------------------------------------------------

  public final IFn get (final Class c0,
                        final Class c1) {
    for (int i=0;i<classes0.length;i++) {
      if (c0.equals(classes0[i])) { 
        final Class[] cs1 = classes1[i];
        for (int j=0;i<cs1.length;j++) {
          if (c1.equals(cs1[j])) {
            return methods[i][j]; } } } } 
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
  // don't really need to copy the Class arrays that don't change
  // method arrays do need copying in any case, because new
  // methods might be inserted 
  //--------------------------------------------------------------

  private final int index0 (final Class c0) {
    for (int i0=0;i0<classes0.length;i0++) {
      if (c0.equals(classes0[i0])) { return i0; } }
    return -1; }

  private final int index1 (final int i0,
                            final Class c1) {
    final Class[] cs1 = classes1[i0];
    for (int i1=0;i1<cs1.length;i1++) {
      if (c1.equals(cs1[i1])) { return i1; } }
    return -1; }

  //--------------------------------------------------------------

  private final MethodCache2 existing0 (final int i0,
                                        final Class c1,
                                        final IFn m) {
    final int n0 = classes0.length;
    final Class[][] cs1 = new Class[n0][];
    final IFn[][] ms = new IFn[n0][];

    // copy other rows
    for (int j0=0;j0<n0;j0++) {
      if (i0 != j0) {
        cs1[j0] = Arrays.copyOf(classes1[j0],classes1[j0].length);
        ms[j0] = Arrays.copyOf(methods[j0],methods[j0].length); } }

    final int i1 = index1(i0,c1);
    if (0 <= i1) { // insert new method
      cs1[i0] = Arrays.copyOf(classes1[i0],classes1[i0].length);
      ms[i0] = Arrays.copyOf(methods[i0],methods[i0].length);
      ms[i0][i1] = m; }
    else { // extend row
      final int n1 = classes1[i0].length;
      cs1[i0] = Arrays.copyOf(classes1[i0],n1+1);
      ms[i0] = Arrays.copyOf(methods[i0],n1+1);
      cs1[i0][n1] = c1;
      ms[i0][n1] = m; }
    return new MethodCache2(
      Arrays.copyOf(classes0,classes0.length), cs1, ms); }

  //--------------------------------------------------------------

  private final MethodCache2 new0 (final Class c0,
                                   final Class c1,
                                   final IFn m) {
    final int n0 = classes0.length;
    final Class[] cs0 = Arrays.copyOf(classes0,n0+1);
    final Class[][] cs1 = new Class[n0+1][];
    final IFn[][] ms = new IFn[n0+1][];
    for (int j0=0;j0<n0;j0++) {
      cs1[j0] = Arrays.copyOf(classes1[j0],classes1[j0].length);
      ms[j0] = Arrays.copyOf(methods[j0],methods[j0].length); }
    cs0[n0] = c0;
    cs1[n0] = new Class[] { c1 };
    ms[n0] = new IFn[] { m };
    return new MethodCache2(cs0,cs1,ms); }

  //--------------------------------------------------------------

  public final MethodCache2 assoc (final Class c0,
                                   final Class c1,
                                   final IFn m) {
    final int i0 = index0(c0);
    if (0 > i0) { return new0(c0,c1,m); }
    return existing0(i0,c1,m); }

  //--------------------------------------------------------------
}