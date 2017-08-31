package palisades.lakes.dynarity.java;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/** Utilities for 'immutable' maps.
 *
 * @author palisades dot lakes at gmail dot com
 * @since 2017-08-30
 * @version 2017-08-30
 */

@SuppressWarnings("unchecked")
public final class Maps {

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
  // disabled construction
  //--------------------------------------------------------------

  private Maps () {
    throw new
    UnsupportedOperationException(
      "can't instantiate " + getClass()); }

  //--------------------------------------------------------------
}
