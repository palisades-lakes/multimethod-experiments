package palisades.lakes.dynarity.java;

import java.util.Collections;

import clojure.lang.IFn;
import palisades.lakes.dynarity.java.DynaFun;
import palisades.lakes.dynarity.java.DynaFun1;

/** Less flexible, but faster alternative to 
 * <code>clojure.lang.MultiFn</code>
 *
 * @author palisades dot lakes at gmail dot com
 * @since 2017-08-30
 * @version 2017-08-31
 */

public interface DynaFun extends IFn {

  public static DynaFun empty (final String name) {
    return new DynaFun1(
      name,
      Collections.emptyMap(),
      Collections.emptyMap()); }

  public DynaFun addMethod (final Object signature,
                            final IFn method);

  public DynaFun preferMethod (final Object x,
                               final Object y) ;
}
