package palisades.lakes.dynamap.java;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import clojure.lang.AFn;
import clojure.lang.IFn;
import clojure.lang.ISeq;
import palisades.lakes.dynafun.java.Classes;
import palisades.lakes.dynafun.java.Signature2;
import palisades.lakes.dynafun.java.Signature3;
import palisades.lakes.dynafun.java.SignatureN;

/** Less flexible, but faster alternative to 
 * <code>clojure.lang.MultiFn</code>
 *
 * @author palisades dot lakes at gmail dot com
 * @version 2017-12-13
 */

@SuppressWarnings("unchecked")
public final class DynaFun implements IFn {

  private final String name;

  // TODO: minimal immutable map implementation.
  // Only need get(), maybe size(), add and remove entry
  // constructors
  
  private final Map<Object,IFn> methodTable;

  // TODO: minimal immutable Multimap implementation.
  
  private final Map<Object,Set> preferTable;

  // TODO: minimal immutable map implementation.
  // Only need get(), maybe size(), add and remove entry
  // constructors
  
  private volatile Map<Object,IFn> methodCache;

  //--------------------------------------------------------------
  // construction
  //--------------------------------------------------------------
  // private because it doesn't copy the input maps.

  private DynaFun (final String n, 
                   final Map mTable,
                   final Map pTable) {
    assert (null != n) && (! n.isEmpty());
    name = n;
    methodTable = mTable;
    preferTable = pTable;
    methodCache = new HashMap(mTable); }

  public static final DynaFun empty (final String name) {
    return new DynaFun(
      name,
      Collections.emptyMap(),
      Collections.emptyMap()); }

  //--------------------------------------------------------------
  // TODO: move to minimal immutable map and multimap classes

  private static final Map assoc (final Map m, 
                                  final Object k,
                                  final Object v) {
    final HashMap b = new HashMap(m);
    b.put(k,v);
    return b; }

//  private static final Map dissoc (final Map m, 
//                                   final Object k) {
//    final Map b = new HashMap(m);
//    b.remove(k);
//    return b; }

  private static final Set add (final Set s, 
                                final Object v) {
    if (null == s) { return Collections.singleton(v); }
    final Set b = new HashSet(s);
    b.add(v);
    return b; }

  private static final Map add (final Map m, 
                                final Object k,
                                final Object v) {
    final Map b = new HashMap(m);
    b.put(k,add((Set) b.get(k),v));
    return b; }

  //--------------------------------------------------------------

  public final DynaFun addMethod (final Object signature,
                                  final IFn method) {
    return 
      new DynaFun(
      name,
      assoc(methodTable,signature,method),
      preferTable); }

//  public final DynaFun removeMethod (final Object signature) {
//    return 
//      new DynaFun(
//      name,
//      dissoc(methodTable,signature),
//      preferTable); }

  //--------------------------------------------------------------

  private static final boolean isAssignableFrom (final Class[] c0,
                                                final Class[] c1) {
    if (c0.length != c1.length) { return false; }
    for (int i=0;i<c0.length;i++) {
      if (! Classes.isAssignableFrom(c0[i],c1[i])) { 
        return false; } }
    return true; }

  private static final boolean isAssignableFrom (final Object s0,
                                                final Object s1) {
    if ((s0 instanceof Class) && (s1 instanceof Class)) {
      return Classes.isAssignableFrom((Class) s0,(Class) s1); }
    if ((s0 instanceof Class[]) && (s1 instanceof Class[])) {
      return isAssignableFrom((Class[]) s0, (Class[]) s1); }
    if ((s0 instanceof Signature2) && (s1 instanceof Signature2)) {
      return ((Signature2) s0).isAssignableFrom((Signature2) s1); }
    if ((s0 instanceof Signature3) && (s1 instanceof Signature3)) {
      return ((Signature3) s0).isAssignableFrom((Signature3) s1); }
    if ((s0 instanceof SignatureN) && (s1 instanceof SignatureN)) {
      return ((SignatureN) s0).isAssignableFrom((SignatureN) s1); }
    return false; }

  private final boolean prefers (final Object x, 
                                 final Object y) {

    final Set xprefs = preferTable.get(x);

    if (xprefs != null) {
      // is there an explicit prefer-method entry for (x,y)?
      if (xprefs.contains(y)) { return true; }
      // transitive closure of prefer-method relation
      // is x preferred to anything that is preferred to y?
      for (final Object xx : xprefs) {
        if (prefers(xx,y)) { return true; } } }

    // For multi-arity dispatch functions, we need to check the
    // keys of the preferTable.
    for (final Object k : preferTable.keySet()) {
      if ((!Objects.equals(x,k)) 
        && isAssignableFrom(k,x) 
        && prefers(k,y)) { 
        return true; } }

    return false; }

  //--------------------------------------------------------------

  public final DynaFun preferMethod (final Object x,
                                     final Object y) {
    if (prefers(y,x)) { 
      throw new IllegalStateException(
        String.format(
          "Preference conflict in multimethod '%s':" + 
            "%s is already preferred to %s",
            name,y,x)); }
    return 
      new DynaFun(
      name,
      methodTable,
      add(preferTable,x,y)); }

  //--------------------------------------------------------------

  private final boolean dominates (final Object x,
                                   final Object y) {
    return prefers(x,y) || isAssignableFrom(y,x); }

  //--------------------------------------------------------------

  private final IFn findAndCacheBestMethod (final Object signature) {
    Map.Entry bestEntry = null;
    for (final Object o : methodTable.entrySet()) {
      final Map.Entry e = (Map.Entry) o;
      if (isAssignableFrom(e.getKey(),signature)) {
        if ((bestEntry == null)
          || dominates(e.getKey(),bestEntry.getKey())) {
          bestEntry = e; }
        if (!dominates(bestEntry.getKey(),e.getKey())) { 
          throw new IllegalArgumentException(
            String.format(
              "Multiple methods in multimethod '%s' "
                + "match signature value: %s -> %s and %s, "
                + "and neither is preferred",
                name,
                signature,
                e.getKey(),
                bestEntry.getKey())); } } }
    if (null == bestEntry) { return null; }
    final IFn method = (IFn) bestEntry.getValue();
    methodCache = assoc(methodCache,signature,method);
    return method; }

  //--------------------------------------------------------------

  private final IFn getMethod (final Object signature) {
    final IFn cached = methodCache.get(signature);
    if (null != cached) { return cached; }
    final IFn method = findAndCacheBestMethod(signature); 
    if (method == null) { 
      throw new IllegalArgumentException(
        String.format(
          "No method in multimethod '%s' for signature: %s",name,
          signature)); }
    return method; }
    
  //--------------------------------------------------------------
  // IFn interface
  //--------------------------------------------------------------

  @Override
  public final Object invoke () {
    return 
      getMethod(null)
      .invoke(); }

  @Override
  public final Object invoke (final Object arg1) {
    final Class k = Classes.classOf(arg1);
    final IFn f = getMethod(k);
    return f.invoke(arg1); }

  @Override
  public final Object invoke (final Object arg1,
                              final Object arg2) {
    final Object k = new Signature2(
      Classes.classOf(arg1),
      Classes.classOf(arg2));
    final IFn f = getMethod(k);
    return f.invoke(arg1,arg2); }

  @Override
  public final Object invoke (final Object arg1,
                              final Object arg2,
                              final Object arg3) {
    final Object k = new Signature3(
      Classes.classOf(arg1),
      Classes.classOf(arg2),
      Classes.classOf(arg3));
    final IFn f = getMethod(k); 
    return 
      f.invoke(arg1,arg2,arg3); }
  
  @Override
  public final Object invoke (final Object arg1,
                              final Object arg2,
                              final Object arg3,
                              final Object arg4) {
    return 
      getMethod(
        SignatureN.extract(arg1,arg2,arg3,arg4))
      .invoke(
        arg1,arg2,arg3,arg4); }

  @Override
  public final Object invoke (final Object arg1,
                              final Object arg2,
                              final Object arg3,
                              final Object arg4,
                              final Object arg5) {
    return getMethod(SignatureN.extract(arg1,arg2,arg3,arg4,arg5))
      .invoke(arg1,arg2,arg3,arg4,arg5); }

  @Override
  public final Object invoke (final Object arg1,
                              final Object arg2,
                              final Object arg3,
                              final Object arg4,
                              final Object arg5,
                              final Object arg6) {
    return getMethod(
      SignatureN.extract(arg1,arg2,arg3,arg4,arg5,arg6))
      .invoke(arg1,arg2,arg3,arg4,arg5,arg6); }

  @Override
  public final Object invoke (final Object arg1,
                              final Object arg2,
                              final Object arg3,
                              final Object arg4,
                              final Object arg5,
                              final Object arg6,
                              final Object arg7) {
    return getMethod(
      SignatureN.extract(arg1,arg2,arg3,arg4,arg5,arg6,arg7))
      .invoke(arg1,arg2,arg3,arg4,arg5,arg6,arg7); }

  @Override
  public final Object invoke (final Object arg1,
                              final Object arg2,
                              final Object arg3,
                              final Object arg4,
                              final Object arg5,
                              final Object arg6,
                              final Object arg7,
                              final Object arg8) {
    return getMethod(
      SignatureN.extract(
        arg1,arg2,arg3,arg4,arg5,arg6,arg7,arg8))
      .invoke(arg1,arg2,arg3,arg4,arg5,arg6,arg7,arg8); }

  @Override
  public final Object invoke (final Object arg1,
                              final Object arg2,
                              final Object arg3,
                              final Object arg4,
                              final Object arg5,
                              final Object arg6,
                              final Object arg7,
                              final Object arg8,
                              final Object arg9) {
    return getMethod(
      SignatureN.extract(
        arg1,arg2,arg3,arg4,arg5,arg6,arg7,arg8,arg9))
      .invoke(arg1,arg2,arg3,arg4,arg5,arg6,arg7,arg8,arg9); }

  @Override
  public final Object invoke (final Object arg1,
                              final Object arg2,
                              final Object arg3,
                              final Object arg4,
                              final Object arg5,
                              final Object arg6,
                              final Object arg7,
                              final Object arg8,
                              final Object arg9,
                              final Object arg10) {
    return getMethod(
      SignatureN.extract(
        arg1,arg2,arg3,arg4,arg5,arg6,arg7,arg8,arg9,arg10))
      .invoke(
        arg1,arg2,arg3,arg4,arg5,arg6,arg7,arg8,arg9,arg10); }

  @Override
  public final Object invoke (final Object arg1,
                              final Object arg2,
                              final Object arg3,
                              final Object arg4,
                              final Object arg5,
                              final Object arg6,
                              final Object arg7,
                              final Object arg8,
                              final Object arg9,
                              final Object arg10,
                              final Object arg11) {
    return getMethod(
      SignatureN.extract(
        arg1,arg2,arg3,arg4,arg5,arg6,arg7,arg8,arg9,arg10,
        arg11))
      .invoke(
        arg1,arg2,arg3,arg4,arg5,arg6,arg7,arg8,arg9,arg10,
        arg11); }

  @Override
  public final Object invoke (final Object arg1,
                              final Object arg2,
                              final Object arg3,
                              final Object arg4,
                              final Object arg5,
                              final Object arg6,
                              final Object arg7,
                              final Object arg8,
                              final Object arg9,
                              final Object arg10,
                              final Object arg11,
                              final Object arg12) {
    return getMethod(
      SignatureN.extract(
        arg1,arg2,arg3,arg4,arg5,arg6,arg7,arg8,arg9,arg10,
        arg11,arg12))
      .invoke(
        arg1,arg2,arg3,arg4,arg5,arg6,arg7,arg8,arg9,arg10,
        arg11,arg12); }

  @Override
  public final Object invoke (final Object arg1,
                              final Object arg2,
                              final Object arg3,
                              final Object arg4,
                              final Object arg5,
                              final Object arg6,
                              final Object arg7,
                              final Object arg8,
                              final Object arg9,
                              final Object arg10,
                              final Object arg11,
                              final Object arg12,
                              final Object arg13) {
    return getMethod(
      SignatureN.extract(
        arg1,arg2,arg3,arg4,arg5,arg6,arg7,arg8,arg9,arg10,
        arg11,arg12,arg13))
      .invoke(
        arg1,arg2,arg3,arg4,arg5,arg6,arg7,arg8,arg9,arg10,
        arg11,arg12,arg13); }

  @Override
  public final Object invoke (final Object arg1,
                              final Object arg2,
                              final Object arg3,
                              final Object arg4,
                              final Object arg5,
                              final Object arg6,
                              final Object arg7,
                              final Object arg8,
                              final Object arg9,
                              final Object arg10,
                              final Object arg11,
                              final Object arg12,
                              final Object arg13,
                              final Object arg14) {
    return getMethod(
      SignatureN.extract(
        arg1,arg2,arg3,arg4,arg5,arg6,arg7,arg8,arg9,arg10,
        arg11,arg12,arg13,arg14))
      .invoke(
        arg1,arg2,arg3,arg4,arg5,arg6,arg7,arg8,arg9,arg10,
        arg11,arg12,arg13,arg14); }

  @Override
  public final Object invoke (final Object arg1,
                              final Object arg2,
                              final Object arg3,
                              final Object arg4,
                              final Object arg5,
                              final Object arg6,
                              final Object arg7,
                              final Object arg8,
                              final Object arg9,
                              final Object arg10,
                              final Object arg11,
                              final Object arg12,
                              final Object arg13,
                              final Object arg14,
                              final Object arg15) {
    return getMethod(
      SignatureN.extract(
        arg1,arg2,arg3,arg4,arg5,arg6,arg7,arg8,arg9,arg10,
        arg11,arg12,arg13,arg14,arg15))
      .invoke(
        arg1,arg2,arg3,arg4,arg5,arg6,arg7,arg8,arg9,arg10,
        arg11,arg12,arg13,arg14,arg15); }

  @Override
  public final Object invoke (final Object arg1,
                              final Object arg2,
                              final Object arg3,
                              final Object arg4,
                              final Object arg5,
                              final Object arg6,
                              final Object arg7,
                              final Object arg8,
                              final Object arg9,
                              final Object arg10,
                              final Object arg11,
                              final Object arg12,
                              final Object arg13,
                              final Object arg14,
                              final Object arg15,
                              final Object arg16) {
    return getMethod(
      SignatureN.extract(
        arg1,arg2,arg3,arg4,arg5,arg6,arg7,arg8,arg9,arg10,
        arg11,arg12,arg13,arg14,arg15,arg16))
      .invoke(
        arg1,arg2,arg3,arg4,arg5,arg6,arg7,arg8,arg9,arg10,
        arg11,arg12,arg13,arg14,arg15,arg16); }

  @Override
  public final Object invoke (final Object arg1,
                              final Object arg2,
                              final Object arg3,
                              final Object arg4,
                              final Object arg5,
                              final Object arg6,
                              final Object arg7,
                              final Object arg8,
                              final Object arg9,
                              final Object arg10,
                              final Object arg11,
                              final Object arg12,
                              final Object arg13,
                              final Object arg14,
                              final Object arg15,
                              final Object arg16,
                              final Object arg17) {
    return getMethod(
      SignatureN.extract(
        arg1,arg2,arg3,arg4,arg5,arg6,arg7,arg8,arg9,arg10,
        arg11,arg12,arg13,arg14,arg15,arg16,arg17))
      .invoke(
        arg1,arg2,arg3,arg4,arg5,arg6,arg7,arg8,arg9,arg10,
        arg11,arg12,arg13,arg14,arg15,arg16,arg17); }

  @Override
  public final Object invoke (final Object arg1,
                              final Object arg2,
                              final Object arg3,
                              final Object arg4,
                              final Object arg5,
                              final Object arg6,
                              final Object arg7,
                              final Object arg8,
                              final Object arg9,
                              final Object arg10,
                              final Object arg11,
                              final Object arg12,
                              final Object arg13,
                              final Object arg14,
                              final Object arg15,
                              final Object arg16,
                              final Object arg17,
                              final Object arg18) {
    return getMethod(
      SignatureN.extract(
        arg1,arg2,arg3,arg4,arg5,arg6,arg7,arg8,arg9,arg10,
        arg11,arg12,arg13,arg14,arg15,arg16,arg17,arg18))
      .invoke(
        arg1,arg2,arg3,arg4,arg5,arg6,arg7,arg8,arg9,arg10,
        arg11,arg12,arg13,arg14,arg15,arg16,arg17,arg18); }

  @Override
  public final Object invoke (final Object arg1,
                              final Object arg2,
                              final Object arg3,
                              final Object arg4,
                              final Object arg5,
                              final Object arg6,
                              final Object arg7,
                              final Object arg8,
                              final Object arg9,
                              final Object arg10,
                              final Object arg11,
                              final Object arg12,
                              final Object arg13,
                              final Object arg14,
                              final Object arg15,
                              final Object arg16,
                              final Object arg17,
                              final Object arg18,
                              final Object arg19) {
    return getMethod(
      SignatureN.extract(
        arg1,arg2,arg3,arg4,arg5,arg6,arg7,arg8,arg9,arg10,
        arg11,arg12,arg13,arg14,arg15,arg16,arg17,arg18,arg19))
      .invoke(
        arg1,arg2,arg3,arg4,arg5,arg6,arg7,arg8,arg9,arg10,
        arg11,arg12,arg13,arg14,arg15,arg16,arg17,arg18,arg19); }

  @Override
  public final Object invoke (final Object arg1,
                              final Object arg2,
                              final Object arg3,
                              final Object arg4,
                              final Object arg5,
                              final Object arg6,
                              final Object arg7,
                              final Object arg8,
                              final Object arg9,
                              final Object arg10,
                              final Object arg11,
                              final Object arg12,
                              final Object arg13,
                              final Object arg14,
                              final Object arg15,
                              final Object arg16,
                              final Object arg17,
                              final Object arg18,
                              final Object arg19,
                              final Object arg20) {
    return getMethod(
      SignatureN.extract(
        arg1,arg2,arg3,arg4,arg5,arg6,arg7,arg8,arg9,arg10,arg11,
        arg12,arg13,arg14,arg15,arg16,arg17,arg18,arg19,arg20))
      .invoke(
        arg1,arg2,arg3,arg4,arg5,arg6,arg7,arg8,arg9,arg10,arg11,
        arg12,arg13,arg14,arg15,arg16,arg17,arg18,arg19,arg20); }

  @Override
  public final Object invoke (final Object arg1,
                              final Object arg2,
                              final Object arg3,
                              final Object arg4,
                              final Object arg5,
                              final Object arg6,
                              final Object arg7,
                              final Object arg8,
                              final Object arg9,
                              final Object arg10,
                              final Object arg11,
                              final Object arg12,
                              final Object arg13,
                              final Object arg14,
                              final Object arg15,
                              final Object arg16,
                              final Object arg17,
                              final Object arg18,
                              final Object arg19,
                              final Object arg20,
                              final Object... args) {
    return getMethod(SignatureN.extract(arg1,arg2,arg3,arg4,arg5,arg6,
      arg7,arg8,arg9,arg10,arg11,arg12,arg13,arg14,arg15,arg16,
      arg17,arg18,arg19,arg20,args)).invoke(arg1,arg2,arg3,arg4,
        arg5,arg6,arg7,arg8,arg9,arg10,arg11,arg12,arg13,arg14,
        arg15,arg16,arg17,arg18,arg19,arg20,args);
  }
  //--------------------------------------------------------------

  @Override
  public final Object call () throws Exception {
    return invoke(); }

  @Override
  public final void run () { invoke();  }

  @Override
  public final Object applyTo (ISeq args) {
    return AFn.applyToHelper(this, args); }

  //--------------------------------------------------------------
}
