package multix.java.matrices;

import org.apache.commons.rng.UniformRandomProvider;
import clojure.lang.IFn;
import benchtools.java.matrices.DenseMatrix;
import benchtools.java.matrices.DiagonalMatrix;
import benchtools.java.matrices.Matrix;
import benchtools.random.PRNG;

/**
 * @author palisades dot lakes at gmail dot com
 * @since 2017-08-10
 * @version 2017-08-10
 */
public final class MethodLookup {

  private static final void tryAdd (final Matrix a,
                                    final Matrix b) {
    System.out.println(
      a.getClass().getSimpleName() + 
      " + " +
      b.getClass().getSimpleName());
    final Matrix c = a.add(b);
    System.out.println("-> " + c.getClass().getSimpleName()); }
  
  //--------------------------------------------------------------
  
  public static void main (String[] args) {
    final UniformRandomProvider urp =
      PRNG.uniformRandomProvider(
        "seeds/Well44497b-2017-07-25.edn");
    final IFn.D generator = 
      PRNG.uniformDoubleGenerator(-1.0e6,1.0e6,urp);
    
    final Matrix m0 = DenseMatrix.generate(generator,16,8);
    final Matrix m1 = DiagonalMatrix.generate(generator,16,8);
    
    tryAdd(m0,m0);
    tryAdd(m0,m1);
    tryAdd(m1,m0);
    tryAdd(m1,m1); }

  //--------------------------------------------------------------
}
