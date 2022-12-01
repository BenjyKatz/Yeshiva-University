package edu.yu.introtoalgs;

import java.util.*;

/** Implements the "Add an Interval To a Set of Intervals" semantics defined in
 * the requirements document.
 * 
 * @author Avraham Leff 
 */

public class MergeAnInterval {

  /** An immutable class, holds a left and right integer-valued pair that
   * defines a closed interval
   *
   * IMPORTANT: students may not modify the semantics of the "left", "right"
   * instance variables, nor may they use any other constructor signature.
   * Students may (are encouraged to) add any other methods that they choose,
   * bearing in mind that my tests will ONLY DIRECTLY INVOKE the constructor
   * and the "merge" method.
   */
  public static class Interval implements Comparable<Interval>{
    public final int left;
    public final int right;

    /** Constructor
     * 
     * @param left the left endpoint of the interval, may be negative
     * @param right the right endpoint of the interval, may be negative
     * @throws IllegalArgumentException if left is >= right
     */
    public Interval(int l, int r) {
    if(l>=r){
        throw new IllegalArgumentException();
    }
      this.left = l;
      this.right = r;
    }
    public int getLeft(){
        return this.left;
    }
    public int getRight(){
        return this.right;
    }

    @Override
    public int compareTo(Interval o) { 
        if(o.getLeft() == this.left && o.getRight()==this.right){
            return 0;
        }
        if(this.left<o.getLeft()){
            return -1;
        }
         return 1;
        // fill me in!
      
    }
    @Override
    public boolean equals(Object o){
        if ((o instanceof Interval) && ((Interval)o).getLeft() == this.left && ((Interval)o).getRight()==this.right){
            return true;
        }
        return false;
    }
    @Override
    public int hashCode(){
        return this.getLeft()*31+this.getRight();
    }

  } // Interval class

  /** Merges the new interval into an existing set of disjoint intervals.
   *
   * @param intervals an set of disjoint intervals (may be empty)
   * @param newInterval the interval to be added
   * @return a new set of disjoint intervals containing the original intervals
   * and the new interval, merging the new interval if necessary into existing
   * interval(s), to preseve the "disjointedness" property.
   * @throws IllegalArgumentException if either parameter is null
   */
  public static Set<Interval> merge(final Set<Interval> intervals, Interval newInterval){
    int newLeft = newInterval.getLeft();
    int newRight = newInterval.getRight();
    Interval newIntervalToAdd;
    Interval leftFalls = null;
    Interval rightFalls = null;
    for(Interval interval: intervals){
        if(interval.getLeft()<=newLeft && interval.getRight()>=newLeft){
            leftFalls = interval;
           // intervals.remove(interval);
        }
        if(interval.getLeft()<=newRight&&interval.getRight()>=newRight){
            rightFalls = interval;
           // intervals.remove(interval);
        }
    } 
    int newIntervalLeft;
    int newIntervalRight; 
    if(leftFalls == null){
         newIntervalLeft = newLeft;
    }
    else{
         newIntervalLeft = leftFalls.getLeft();
    }
    if(rightFalls == null) {
        newIntervalRight = newRight;
    }
    else {
        newIntervalRight = rightFalls.getRight();
    }
    Set<Interval> setToReturn = new HashSet<Interval>();
    for(Interval interval: intervals){
        if(interval.getLeft()>=newIntervalLeft && interval.getLeft()<=newIntervalRight){
            //intervals.remove(interval);
        }
        else{
            setToReturn.add(interval);
        }
    }
    newIntervalToAdd = new Interval(newIntervalLeft, newIntervalRight);
    setToReturn.add(newIntervalToAdd);
    return setToReturn;
  }
}