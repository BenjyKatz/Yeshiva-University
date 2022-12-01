package edu.yu.da;

import java.util.Collections;
import java.util.*;


/** Implements the WaitNoMoreI API.
 *
 * Students MAY NOT change the provided constructor signature!
 * 
 * @author Avraham Leff
 */

public class WaitNoMore implements WaitNoMoreI {

  /** No-op constructor
   */
  public WaitNoMore() {
    // no-op, students may change the implementation
  }

  @Override
  public int minTotalWaitingTime(final int[] durations, final int[] weights) {
      HashMap<Double, Integer> ratioToInd = new HashMap<Double, Integer>();
      ArrayList<Double> ratios = new ArrayList<Double>();
      for(int i = 0; i< durations.length; i++){
        ratioToInd.put(((double)(weights[i]))/((double)(durations[i])), i);
        ratios.add(((double)(weights[i]))/((double)(durations[i])));
      }
      Collections.sort(ratios);
      Collections.reverse(ratios);
      //high to low
      int waitTimePerJob = 0;
      int totalWaitTime = 0;
      for(Double key: ratios){
        int index = ratioToInd.get(key);
        totalWaitTime = totalWaitTime+(waitTimePerJob);
        waitTimePerJob = waitTimePerJob+durations[index];
      }
      
      return totalWaitTime;
  }
} // WaitNoMore