package palisades.lakes.dynest.java;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import clojure.lang.IFn;

/** Utilities for 'immutable' maps.
 *
 * @author palisades dot lakes at gmail dot com
 * @since 2017-08-30
 * @version 2017-09-04
 */

@SuppressWarnings("unchecked")
public final class Util {

  //--------------------------------------------------------------
  // TODO: move to minimal immutable map and multimap classes

  public static final Map assoc (final Map m, 
                                 final Object k,
                                 final Object v) {
    final HashMap b = new HashMap(m);
    b.put(k,v);
    return b; }

  //  public static final Map dissoc (final Map m, 
  //                                   final Object k) {
  //    final Map b = new HashMap(m);
  //    b.remove(k);
  //    return b; }

  public static final Set add (final Set s, 
                               final Object v) {
    if (null == s) { return Collections.singleton(v); }
    final Set b = new HashSet(s);
    b.add(v);
    return b; }

  public static final Map add (final Map m, 
                               final Object k,
                               final Object v) {
    final Map b = new HashMap(m);
    b.put(k,add((Set) b.get(k),v));
    return b; }

  //--------------------------------------------------------------
  // for code symmetry

  public static final Class[] copy (final Class[] a) {
    return Arrays.copyOf(a,a.length); }

  public static final IFn[] copy (final IFn[] a) {
    return Arrays.copyOf(a,a.length); }

  public static final Class[][] copy (final Class[][] a) {
    final int n0 = a.length;
    final Class[][] copy = new Class[n0][];
    for (int j0=0;j0<n0;j0++) {
      copy[j0] = Arrays.copyOf(a[j0],a[j0].length); }
    return copy; }

  public static final IFn[][] copy (final IFn[][] a) {
    final int n0 = a.length;
    final IFn[][] copy = new IFn[n0][];
    for (int j0=0;j0<n0;j0++) {
      copy[j0] = Arrays.copyOf(a[j0],a[j0].length); }
    return copy; }

  public static final Class[][][] copy (final Class[][][] a) {
    final int n0 = a.length;
    final Class[][][] copy = new Class[n0][][];
    for (int j0=0;j0<n0;j0++) { copy[j0] = copy(a[j0]); }
    return copy; }

  public static final IFn[][][] copy (final IFn[][][] a) {
    final int n0 = a.length;
    final IFn[][][] copy = new IFn[n0][][];
    for (int j0=0;j0<n0;j0++) { copy[j0] = copy(a[j0]); }
    return copy; }

  //--------------------------------------------------------------
  // append to the leading dimension

  public static final Class[] append (final Class[] a,
                                      final Class c) {
    final int n = a.length;
    final Class[] b = Arrays.copyOf(a,n+1); 
    b[n] = c;
    return b; }

  public static final IFn[] append (final IFn[] a,
                                    final IFn c) {
    final int n = a.length;
    final IFn[] b = Arrays.copyOf(a,n+1); 
    b[n] = c;
    return b; }

  public static final Class[][] append (final Class[][] a,
                                        final Class[] c) {
    final int n = a.length;
    final Class[][] b = Arrays.copyOf(a,n+1); 
    b[n] = c;
    return b; }

  public static final IFn[][] append (final IFn[][] a,
                                      final IFn[] c) {
    final int n = a.length;
    final IFn[][] b = Arrays.copyOf(a,n+1); 
    b[n] = c;
    return b; }

  public static final Class[][][] append (final Class[][][] a,
                                          final Class[][] c) {
    final int n = a.length;
    final Class[][][] b = Arrays.copyOf(a,n+1); 
    b[n] = c;
    return b; }

  public static final IFn[][][] append (final IFn[][] []a,
                                        final IFn[][] c) {
    final int n = a.length;
    final IFn[][][] b = Arrays.copyOf(a,n+1); 
    b[n] = c;
    return b; }

  //--------------------------------------------------------------
  // append to the 2nd dimension at the 1st dim index
  
  public static final Class[][] append (final Class[][] a,
                                        final int i0,
                                        final Class c) {
    final Class[][] b = copy(a); 
    b[i0] = append(b[i0],c);
    return b; }

  public static final IFn[][] append (final IFn[][] a,
                                      final int i0,
                                      final IFn c) {
    final IFn[][] b = copy(a); 
    b[i0] = append(b[i0],c);
    return b; }

  public static final Class[][][] append (final Class[][][] a,
                                          final int i0,
                                          final Class[] c) {
    final Class[][][] b = copy(a); 
    b[i0] = append(b[i0],c);
    return b; }

  public static final IFn[][][] append (final IFn[][][] a,
                                        final int i0,
                                        final IFn[] c) {
    final IFn[][][] b = copy(a); 
    b[i0] = append(b[i0],c);
    return b; }

  //--------------------------------------------------------------
  // append to the 3rd dimension at the 1st and 2nd dim indexes
  
  public static final Class[][][] append (final Class[][][] a,
                                          final int i0,
                                          final int i1,
                                          final Class c) {
    final Class[][][] b = copy(a); 
    b[i0][i1] = append(b[i0][i1],c);
    return b; }

  public static final IFn[][][] append (final IFn[][][] a,
                                        final int i0,
                                        final int i1,
                                        final IFn c) {
    final IFn[][][] b = copy(a); 
    b[i0][i1] = append(b[i0][i1],c);
    return b; }

  //--------------------------------------------------------------
  // disabled construction
  //--------------------------------------------------------------

  private Util () {
    throw new
    UnsupportedOperationException(
      "can't instantiate " + getClass()); }

  //--------------------------------------------------------------
}
