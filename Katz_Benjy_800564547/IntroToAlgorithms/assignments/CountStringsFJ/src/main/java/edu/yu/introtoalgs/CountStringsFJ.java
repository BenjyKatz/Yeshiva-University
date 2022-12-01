package edu.yu.introtoalgs;
import java.util.concurrent.ForkJoinPool ;
import java.util.concurrent.ForkJoinTask ;
import java.util.concurrent.RecursiveTask ;

/** Implements the CountStringsFJ semantics specified in the requirements
 * document.
 *
 * @author Avraham Leff
 */

public class CountStringsFJ {
    class ForkJoinCount extends RecursiveTask <Integer> {
        ForkJoinCount ( int threshold , String [] array , int low , int high, String target)
        {
        if(threshold<0 || low>high || target == null){
             throw new IllegalArgumentException();
        }
        // @fixme No error checking!
        this.low = low ;
        this.high = high ;
        this.array = array ;
        // If array size is this small,
        // don’t process recursively
        this.threshold = threshold;
        this.target = target;
        }
        private final int low ;
        private final String target;
        private final int high ;
        private final String[] array ;
        private final int threshold ;

        public Integer compute(){
            if ( high - low <= threshold ) {
                return countSequentialy(array , low , high, target);
            } // sequential processing
            else {
                ForkJoinCount left = new ForkJoinCount( threshold , array , low ,( high + low ) /2, target) ;
                ForkJoinCount right = new ForkJoinCount( threshold , array ,(high + low ) /2 , high, target );
                left.fork () ;
                final int rightAnswer = right.compute () ;
                final int leftAnswer = left.join () ;
                return leftAnswer+rightAnswer;
            }
        }
        public int countSequentialy(String[] array, int low, int high, String target){
            int counter = 0;
            for(int i = low; i<high;i++){
                if(array[i] != null && array[i].equals(target)){
                    counter++;
                }
            }
            return counter;
        }
    }

  /** Constructor.
   *
   * @param arr the array to process, can't be null or empty
   * @param str the string to match, can't be null, may be empty
   * @param threshold when the length of arr is less than threshold, processing
   * must be sequential; otherwise, processing must use a fork/join, recursive
   * divide-and-conquer strategy.  The parameter must be greater than 0.
   *
   * IMPORTANT: Students must use this constructor, they MAY NOT add another
   * constructor.
   */
  int parallelism;
  final ForkJoinPool fjPool;
  ForkJoinTask <Integer> task;
  public CountStringsFJ(final String[] arr, final String str, final int threshold) {
    parallelism = Runtime.getRuntime().availableProcessors() * 1;
    task = new ForkJoinCount (threshold , arr , 0, arr.length, str);
    fjPool = new ForkJoinPool(parallelism);
    
    // fill me in!
  }
  
  /** Returns the number of elements in arr that ".equal" the "str" parameter
   *
   * @return Using a strategy dictated by the relative values of threshold and
   * the size of arr, returns the number of times that str appears in arr
   */
  public int doIt() {
    int parallelCount = fjPool.invoke(task);
    fjPool.shutdown () ; 
      return parallelCount;
  }
}