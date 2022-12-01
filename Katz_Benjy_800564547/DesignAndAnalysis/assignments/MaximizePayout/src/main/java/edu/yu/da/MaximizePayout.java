package edu.yu.da;

/** Implements the MaximizePayoutI API.
 *
 * Students MAY NOT change the provided constructor signature!
 * 
 * @author Avraham Leff
 */

import java.util.*;

public class MaximizePayout implements MaximizePayoutI {

  /** No-op constructor
   */
  public MaximizePayout() {
    // no-op, students may change the implementation
  }

  @Override
  public long max(final List<Long> A, final List<Long> B) {
    if( A == null || B == null || A.size() != B.size()||A.size() == 0){
        throw new IllegalArgumentException();
    }
      List<Long> baseList = new LinkedList<Long>();
      List<Long> exponentList = new LinkedList<Long>();
      baseList.addAll(A);
      exponentList.addAll(B);
      Collections.sort(baseList);
      Collections.sort(exponentList);
      long total = 1L;
      for(int i = 0; i< exponentList.size(); i++){
       total = total*(long)Math.pow((double)baseList.get(i), (double)exponentList.get(i)); 
      }
      return total;

    //  return -1L;
  }

} // MaximizePayout