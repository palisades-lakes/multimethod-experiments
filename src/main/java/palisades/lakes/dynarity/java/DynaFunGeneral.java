package palisades.lakes.dynarity.java;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import clojure.lang.AFn;
import clojure.lang.IFn;
import clojure.lang.ISeq;
import palisades.lakes.dynafun.java.signature.Signature2;
import palisades.lakes.dynafun.java.signature.Signature3;
import palisades.lakes.dynafun.java.signature.Signatures;
import palisades.lakes.dynarity.java.DynaFun;
import palisades.lakes.dynarity.java.DynaFunGeneral;
import palisades.lakes.dynarity.java.Maps;

/** Less flexible, but faster alternative to 
 * <code>clojure.lang.MultiFn</code>
 *
 * @author palisades dot lakes at gmail dot com
 * @since 2017-08-18
 * @version 2017-09-01
 */

@SuppressWarnings("unchecked")
public final class DynaFunGeneral implements DynaFun {

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

  DynaFunGeneral (final String n, 
    final Map mTable,
    final Map pTable) {
    assert (null != n) && (! n.isEmpty());
    name = n;
    methodTable = mTable;
    preferTable = pTable;
    methodCache = new HashMap(mTable); }

  public static final DynaFunGeneral make (final String name) {
    return new DynaFunGeneral(
      name,
      Collections.emptyMap(),
      Collections.emptyMap()); }

  //--------------------------------------------------------------

  @Override
  public final DynaFun addMethod (final Object signature,
                                  final IFn method) {
    return 
      new DynaFunGeneral(
        name,
        Maps.assoc(methodTable,signature,method),
        preferTable); }

  //  public final DynaFun removeMethod (final Object signature) {
  //    return 
  //      new DynaFun(
  //      name,
  //      Maps.dissoc(methodTable,signature),
  //      preferTable); }

  //--------------------------------------------------------------

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
    // TODO: does this make the next loop unnecessary?
    for (final Object k : preferTable.keySet()) {
      if ((!x.equals(k)) 
        && Signatures.isAssignableFrom(k,x) 
        && prefers(k,y)) { 
        return true; } }

    return false; }

  //--------------------------------------------------------------

  @Override
  public final DynaFun preferMethod (final Object x,
                                     final Object y) {
    if (prefers(y,x)) { 
      throw new IllegalStateException(
        String.format(
          "Preference conflict in multimethod '%s':" + 
            "%s is already preferred to %s",
            name,y,x)); }
    return 
      new DynaFunGeneral(
        name,
        methodTable,
        Maps.add(preferTable,x,y)); }

  //--------------------------------------------------------------

  private final boolean dominates (final Object x,
                                   final Object y) {
    return prefers(x,y) || Signatures.isAssignableFrom(y,x); }

  //--------------------------------------------------------------

  private final IFn findAndCacheBestMethod (final Object signature) {
    Map.Entry bestEntry = null;
    for (final Object o : methodTable.entrySet()) {
      final Map.Entry e = (Map.Entry) o;
      if (Signatures.isAssignableFrom(e.getKey(),signature)) {
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
    methodCache = Maps.assoc(methodCache,signature,method);
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
    final Class k = arg1.getClass();
    final IFn f = getMethod(k);
    return
      f.invoke(arg1); }

  @Override
  public final Object invoke (final Object arg1,
                              final Object arg2) {
    final Object k = new Signature2(
      arg1.getClass(),
      arg2.getClass());
    final IFn f = getMethod(k);
    return 
      f.invoke(arg1,arg2); }

  @Override
  public final Object invoke (final Object arg1,
                              final Object arg2,
                              final Object arg3) {
    final Object k = new Signature3(
      arg1.getClass(),
      arg2.getClass(),
      arg3.getClass());
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
        Signatures.extract(arg1,arg2,arg3,arg4))
      .invoke(
        arg1,arg2,arg3,arg4); }

  @Override
  public final Object invoke (final Object arg1,
                              final Object arg2,
                              final Object arg3,
                              final Object arg4,
                              final Object arg5) {
    return getMethod(Signatures.extract(arg1,arg2,arg3,arg4,arg5))
      .invoke(arg1,arg2,arg3,arg4,arg5); }

  @Override
  public final Object invoke (final Object arg1,
                              final Object arg2,
                              final Object arg3,
                              final Object arg4,
                              final Object arg5,
                              final Object arg6) {
    return getMethod(
      Signatures.extract(arg1,arg2,arg3,arg4,arg5,arg6))
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
      Signatures.extract(arg1,arg2,arg3,arg4,arg5,arg6,arg7))
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
      Signatures.extract(
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
      Signatures.extract(
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
      Signatures.extract(
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
      Signatures.extract(
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
      Signatures.extract(
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
      Signatures.extract(
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
      Signatures.extract(
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
      Signatures.extract(
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
      Signatures.extract(
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
      Signatures.extract(
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
      Signatures.extract(
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
      Signatures.extract(
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
      Signatures.extract(
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
    return getMethod(Signatures.extract(arg1,arg2,arg3,arg4,arg5,arg6,
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
