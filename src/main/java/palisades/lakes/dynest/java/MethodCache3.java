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
      if (c0.equals(classes0[i0])) { 
        final Class[] cs1 = classes1[i0];
        for (int i1=0;i1<cs1.length;i1++) {
          if (c1.equals(cs1[i1])) {
            final Class[] cs2 = classes2[i0][i1];
            for (int i2=0;i2<cs1.length;i2++) {
              if (c2.equals(cs2[i2])) {
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
  // don't really need to copy the Class arrays that don't change
  // method arrays do need copying in any case, because new
  // methods might be inserted 
  //--------------------------------------------------------------

  private final int index (final Class c0) {
    for (int i0=0;i0<classes0.length;i0++) {
      if (c0.equals(classes0[i0])) { return i0; } }
    return -1; }

  private final int index (final int i0,
                           final Class c1) {
    final Class[] cs1 = classes1[i0];
    for (int i1=0;i1<cs1.length;i1++) {
      if (c1.equals(cs1[i1])) { return i1; } }
    return -1; }

  private final int index (final int i0,
                           final int i1,
                           final Class c2) {
    final Class[] cs2 = classes2[i0][i1];
    for (int i2=0;i2<cs2.length;i2++) {
      if (c2.equals(cs2[i2])) { return i2; } }
    return -1; }

  //--------------------------------------------------------------

  private static final Class[][] deepCopy (final Class[][] a) {
    final int n0 = a.length;
    final Class[][] copy = new Class[n0][];
    for (int j0=0;j0<n0;j0++) {
      copy[j0] = Arrays.copyOf(a[j0],a[j0].length); }
    return copy; }

  private static final IFn[][] deepCopy (final IFn[][] a) {
    final int n0 = a.length;
    final IFn[][] copy = new IFn[n0][];
    for (int j0=0;j0<n0;j0++) {
      copy[j0] = Arrays.copyOf(a[j0],a[j0].length); }
    return copy; }

  private static final Class[][][] deepCopy (final Class[][][] a) {
    final int n0 = a.length;
    final Class[][][] copy = new Class[n0][][];
    for (int j0=0;j0<n0;j0++) { copy[j0] = deepCopy(a[j0]); }
    return copy; }

  private static final IFn[][][] deepCopy (final IFn[][][] a) {
    final int n0 = a.length;
    final IFn[][][] copy = new IFn[n0][][];
    for (int j0=0;j0<n0;j0++) { copy[j0] = deepCopy(a[j0]); }
    return copy; }

  //--------------------------------------------------------------

  private final MethodCache3 existing2 (final int i0,
                                        final int i1,
                                        final int i2,
                                        final IFn m) {
    final Class[] cs0 = Arrays.copyOf(classes0,classes0.length);
    final Class[][] cs1 = deepCopy(classes1);
    final Class[][][] cs2 = deepCopy(classes2);
    final IFn[][][] ms = deepCopy(methods);
    ms[i0][i1][i2] = m;
    return new MethodCache3(cs0,cs1,cs2,ms); }

  //--------------------------------------------------------------

  private final MethodCache3 new2 (final int i0,
                                   final int i1,
                                   final Class c2,
                                   final IFn m) {
    final int n0 = classes0.length;
    final Class[] cs0 = Arrays.copyOf(classes0,n0);
    final Class[][] cs1 = deepCopy(classes1);
    final Class[][][] cs2 = deepCopy(classes2);
    final IFn[][][] ms = deepCopy(methods);

    final int n2 = cs2[i0][i1].length;
    cs2[i0][i1] = Arrays.copyOf(cs2[i0][i1],n2+1);
    cs2[i0][i1][n2] = c2;
    ms[i0][i1] = Arrays.copyOf(ms[i0][i1],n2+1);
    ms[i0][i1][n2] = m; 

    return new MethodCache3(cs0,cs1,cs2,ms); }

  //--------------------------------------------------------------

  private final MethodCache3 existing1 (final int i0,
                                        final int i1,
                                        final Class c2,
                                        final IFn m) {
    final int i2 = index(i0,i1,c2); 
    if (0 > i2) { return new2(i0,i1,c2,m); }
    return existing2(i0,i1,i2,m); }

  //--------------------------------------------------------------

  private final MethodCache3 new1 (final int i0,
                                   final Class c1,
                                   final Class c2,
                                   final IFn m) {
    final int n0 = classes0.length;
    final Class[] cs0 = Arrays.copyOf(classes0,n0);
    final Class[][] cs1 = deepCopy(classes1);
    final Class[][][] cs2 = deepCopy(classes2);
    final IFn[][][] ms = deepCopy(methods);

    final int n1 = classes1[i0].length;
    cs1[i0] = Arrays.copyOf(classes1[i0],n1+1);
    cs2[i0] = new Class[n1+1][];
    ms[i0] = new IFn[n1+1][];
    cs1[i0][n1] = c1;
    cs2[i0][n1] = new Class[] { c2 };
    ms[i0][n1] = new IFn[] { m };
    for (int j1=0;j1<n1;j1++) { 
      final int n2 = cs2[i0][j1].length;
      cs2[i0][j1] = Arrays.copyOf(classes2[i0][j1],n2);
      ms[i0][j1] = Arrays.copyOf(methods[i0][j1],n2); } 

    return new MethodCache3(cs0,cs1,cs2,ms); }

  //--------------------------------------------------------------

  private final MethodCache3 existing0 (final int i0,
                                        final Class c1,
                                        final Class c2,
                                        final IFn m) {
    final int i1 = index(i0,c1);
    if (0 > i1) { return new1(i0,c1,c2,m); }
    return existing1(i0,i1,c2,m); }

  //--------------------------------------------------------------

  private final MethodCache3 new0 (final Class c0,
                                   final Class c1,
                                   final Class c2,
                                   final IFn m) {
    final int n0 = classes0.length;
    final Class[] cs0 = Arrays.copyOf(classes0,n0+1);
    final Class[][] cs1 = new Class[n0+1][];
    final Class[][][] cs2 = new Class[n0+1][][];
    final IFn[][][] ms = new IFn[n0+1][][];
    for (int i0=0;i0<n0;i0++) {
      final int n1 = classes1[i0].length;
      cs1[i0] = Arrays.copyOf(classes1[i0],n1);
      for (int i1=0;i1<n1;i1++) {
        final int n2 = classes2[i0][i1].length;
        cs2[i0][i1] = Arrays.copyOf(classes2[i0][i1],n2); 
        ms[i0][i1] = Arrays.copyOf(methods[i0][i1],n2); } }
    cs0[n0] = c0;
    cs1[n0] = new Class[] { c1 };
    cs2[n0] = new Class[][] { { c2 } };
    ms[n0] = new IFn[][] { { m } };
    return new MethodCache3(cs0,cs1,cs2,ms); }

  //--------------------------------------------------------------

  public final MethodCache3 assoc (final Class c0,
                                   final Class c1,
                                   final Class c2,
                                   final IFn m) {
    final int i0 = index(c0);
    if (0 > i0) { return new0(c0,c1,c2,m); }
    return existing0(i0,c1,c2,m); }

  //--------------------------------------------------------------
}
