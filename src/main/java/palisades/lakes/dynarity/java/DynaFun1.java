package palisades.lakes.dynarity.java;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import clojure.lang.IFn;
import clojure.lang.ISeq;
import palisades.lakes.dynafun.java.signature.Signatures;

/** Dynamic functions whose methods are all arity 1.
 *
 * @author palisades dot lakes at gmail dot com
 * @since 2017-08-30
 * @version 2017-09-01
 */

@SuppressWarnings("unchecked")
public final class DynaFun1 implements DynaFun {

  private final String name;

  private final Map<Class,IFn> methodTable;

  private final Map<Class,Set> preferTable;

  private volatile MethodCache methodCache;

  //--------------------------------------------------------------
  // construction
  //--------------------------------------------------------------
  // private because it doesn't copy the input maps.

  public DynaFun1 (final String n, 
                   final Map mTable,
                   final Map pTable) {
    assert (null != n) && (! n.isEmpty());
    name = n;
    methodTable = mTable;
    preferTable = pTable;
    // won't work for Signatures!
    methodCache = MethodCache.empty(); }

  public static final DynaFun1 make (final String name) {
    return new DynaFun1(
      name,
      Collections.emptyMap(),
      Collections.emptyMap()); }

  //--------------------------------------------------------------

  @Override
  public final DynaFun addMethod (final Object signature,
                                  final IFn method) {
    if (signature instanceof Class) {
      return 
        new DynaFun1(
          name,
          Maps.assoc(methodTable,signature,method),
          preferTable); }
    return 
      new DynaFunGeneral(
        name,
        Maps.assoc(methodTable,signature,method),
        preferTable); }

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

  private final DynaFun1 preferMethod (final Class x,
                                       final Class y) {
    if (prefers(y,x)) { 
      throw new IllegalStateException(
        String.format(
          "Preference conflict in multimethod '%s':" + 
            "%s is already preferred to %s",
            name,y,x)); }
    return 
      new DynaFun1(
        name,
        methodTable,
        Maps.add(preferTable,x,y)); }

  @Override
  public final DynaFun preferMethod (final Object x,
                                     final Object y) {
    if ((x instanceof Class) && (y instanceof Class) )
      return preferMethod((Class) x, (Class) y);

    return new DynaFunGeneral(name,methodTable,preferTable)
      .preferMethod(x,y); }

  //--------------------------------------------------------------

  private final boolean dominates (final Class x,
                                   final Class y) {
    return prefers(x,y) || y.isAssignableFrom(x); }

  //--------------------------------------------------------------

  private final IFn findAndCacheBestMethod (final Class c) {
    Map.Entry bestEntry = null;
    for (final Object o : methodTable.entrySet()) {
      final Map.Entry e = (Map.Entry) o;
      if (Signatures.isAssignableFrom(e.getKey(),c)) {
        if ((bestEntry == null)
          || dominates(
            (Class) e.getKey(),(Class) bestEntry.getKey())) {
          bestEntry = e; }
        if (!dominates(
          (Class) bestEntry.getKey(),(Class) e.getKey())) { 
          throw new IllegalArgumentException(
            String.format(
              "Multiple methods in multimethod '%s' "
                + "match signature value: %s -> %s and %s, "
                + "and neither is preferred",
                name,
                c,
                e.getKey(),
                bestEntry.getKey())); } } }
    if (null == bestEntry) { return null; }
    final IFn method = (IFn) bestEntry.getValue();
    methodCache = methodCache.assoc(c,method);
    return method; }

  //--------------------------------------------------------------

  private final IFn getMethod (final Class c) {
    final IFn cached = methodCache.get(c);
    if (null != cached) { return cached; }
    final IFn method = findAndCacheBestMethod(c); 
    if (method == null) { 
      throw new IllegalArgumentException(
        String.format(
          "No method in multimethod '%s' for signature: %s",name,
          c)); }
    return method; }

  //--------------------------------------------------------------
  // IFn interface
  //--------------------------------------------------------------

  private final UnsupportedOperationException 
  illegalArity (final int i) {
    return
      new UnsupportedOperationException(
        getClass().getSimpleName() + 
        " can't handle " + i + "operands"); }

  @Override
  public final Object invoke () { throw illegalArity(0); }

  @Override
  public final Object invoke (final Object x) {
    return
      getMethod(
        x.getClass())
      .invoke(x); }

  @Override
  public final Object invoke (final Object x1,
                              final Object x2) {
    throw illegalArity(2); }

  @Override
  public final Object invoke (final Object x1,
                              final Object x2,
                              final Object x3) {
    throw illegalArity(3); }

  @Override
  public final Object invoke (final Object x1,
                              final Object x2,
                              final Object x3,
                              final Object x4) {
    throw illegalArity(4); }

  @Override
  public final Object invoke (final Object x1,
                              final Object x2,
                              final Object x3,
                              final Object x4,
                              final Object x5) {
    throw illegalArity(5); }

  @Override
  public final Object invoke (final Object x1,
                              final Object x2,
                              final Object x3,
                              final Object x4,
                              final Object x5,
                              final Object x6) {
    throw illegalArity(6); }

  @Override
  public final Object invoke (final Object x1,
                              final Object x2,
                              final Object x3,
                              final Object x4,
                              final Object x5,
                              final Object x6,
                              final Object x7) {
    throw illegalArity(7); }

  @Override
  public final Object invoke (final Object x1,
                              final Object x2,
                              final Object x3,
                              final Object x4,
                              final Object x5,
                              final Object x6,
                              final Object x7,
                              final Object x8) {
    throw illegalArity(8); }

  @Override
  public final Object invoke (final Object x1,
                              final Object x2,
                              final Object x3,
                              final Object x4,
                              final Object x5,
                              final Object x6,
                              final Object x7,
                              final Object x8,
                              final Object x9) {
    throw illegalArity(9); }

  @Override
  public final Object invoke (final Object x1,
                              final Object x2,
                              final Object x3,
                              final Object x4,
                              final Object x5,
                              final Object x6,
                              final Object x7,
                              final Object x8,
                              final Object x9,
                              final Object x10) {
    throw illegalArity(10); }

  @Override
  public final Object invoke (final Object x1,
                              final Object x2,
                              final Object x3,
                              final Object x4,
                              final Object x5,
                              final Object x6,
                              final Object x7,
                              final Object x8,
                              final Object x9,
                              final Object x10,
                              final Object x11) {
    throw illegalArity(11); }

  @Override
  public final Object invoke (final Object x1,
                              final Object x2,
                              final Object x3,
                              final Object x4,
                              final Object x5,
                              final Object x6,
                              final Object x7,
                              final Object x8,
                              final Object x9,
                              final Object x10,
                              final Object x11,
                              final Object x12) {
    throw illegalArity(12); }

  @Override
  public final Object invoke (final Object x1,
                              final Object x2,
                              final Object x3,
                              final Object x4,
                              final Object x5,
                              final Object x6,
                              final Object x7,
                              final Object x8,
                              final Object x9,
                              final Object x10,
                              final Object x11,
                              final Object x12,
                              final Object x13) {
    throw illegalArity(13); }

  @Override
  public final Object invoke (final Object x1,
                              final Object x2,
                              final Object x3,
                              final Object x4,
                              final Object x5,
                              final Object x6,
                              final Object x7,
                              final Object x8,
                              final Object x9,
                              final Object x10,
                              final Object x11,
                              final Object x12,
                              final Object x13,
                              final Object x14) {
    throw illegalArity(14); }

  @Override
  public final Object invoke (final Object x1,
                              final Object x2,
                              final Object x3,
                              final Object x4,
                              final Object x5,
                              final Object x6,
                              final Object x7,
                              final Object x8,
                              final Object x9,
                              final Object x10,
                              final Object x11,
                              final Object x12,
                              final Object x13,
                              final Object x14,
                              final Object x15) {
    throw illegalArity(15); }

  @Override
  public final Object invoke (final Object x1,
                              final Object x2,
                              final Object x3,
                              final Object x4,
                              final Object x5,
                              final Object x6,
                              final Object x7,
                              final Object x8,
                              final Object x9,
                              final Object x10,
                              final Object x11,
                              final Object x12,
                              final Object x13,
                              final Object x14,
                              final Object x15,
                              final Object x16) {
    throw illegalArity(16); }

  @Override
  public final Object invoke (final Object x1,
                              final Object x2,
                              final Object x3,
                              final Object x4,
                              final Object x5,
                              final Object x6,
                              final Object x7,
                              final Object x8,
                              final Object x9,
                              final Object x10,
                              final Object x11,
                              final Object x12,
                              final Object x13,
                              final Object x14,
                              final Object x15,
                              final Object x16,
                              final Object x17) {
    throw illegalArity(17); }

  @Override
  public final Object invoke (final Object x1,
                              final Object x2,
                              final Object x3,
                              final Object x4,
                              final Object x5,
                              final Object x6,
                              final Object x7,
                              final Object x8,
                              final Object x9,
                              final Object x10,
                              final Object x11,
                              final Object x12,
                              final Object x13,
                              final Object x14,
                              final Object x15,
                              final Object x16,
                              final Object x17,
                              final Object x18) {
    throw illegalArity(18); }

  @Override
  public final Object invoke (final Object x1,
                              final Object x2,
                              final Object x3,
                              final Object x4,
                              final Object x5,
                              final Object x6,
                              final Object x7,
                              final Object x8,
                              final Object x9,
                              final Object x10,
                              final Object x11,
                              final Object x12,
                              final Object x13,
                              final Object x14,
                              final Object x15,
                              final Object x16,
                              final Object x17,
                              final Object x18,
                              final Object x19) {
    throw illegalArity(19); }

  @Override
  public final Object invoke (final Object x1,
                              final Object x2,
                              final Object x3,
                              final Object x4,
                              final Object x5,
                              final Object x6,
                              final Object x7,
                              final Object x8,
                              final Object x9,
                              final Object x10,
                              final Object x11,
                              final Object x12,
                              final Object x13,
                              final Object x14,
                              final Object x15,
                              final Object x16,
                              final Object x17,
                              final Object x18,
                              final Object x19,
                              final Object x20) {
    throw illegalArity(20); }

  @Override
  public final Object invoke (final Object x1,
                              final Object x2,
                              final Object x3,
                              final Object x4,
                              final Object x5,
                              final Object x6,
                              final Object x7,
                              final Object x8,
                              final Object x9,
                              final Object x10,
                              final Object x11,
                              final Object x12,
                              final Object x13,
                              final Object x14,
                              final Object x15,
                              final Object x16,
                              final Object x17,
                              final Object x18,
                              final Object x19,
                              final Object x20,
                              final Object... xs) {
    throw illegalArity(20 + xs.length); } 
  //--------------------------------------------------------------

  @Override
  public final Object call () throws Exception { throw illegalArity(0); }

  @Override
  public final void run () { throw illegalArity(0);  }

  @Override
  public final Object applyTo (final ISeq xs) {
    if (1 == xs.count()) { return invoke(xs.first()); } 
    throw illegalArity(xs.count()); }

  //--------------------------------------------------------------
}
