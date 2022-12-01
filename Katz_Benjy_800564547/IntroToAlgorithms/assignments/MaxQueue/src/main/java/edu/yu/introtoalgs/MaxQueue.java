package edu.yu.introtoalgs;

/** Enhances the Queue enqueue() and dequeue() API with a O(1) max()
 * method and O(1) size().  The dequeue() method is O(1), the enqueue
 * is amortized O(1).  The implementation is O(n) in space.
 *
 * @author Avraham Leff
 */

import java.util.NoSuchElementException;

public class MaxQueue {

  /** No-argument constructor: students may not add any other constructor for
   * this class
   */
    int size = 0;
    int max = Integer.MIN_VALUE;
    int[] queue = new int[10];
    int arraySize = 10;
    int front = 1;
    int back = 1;
    int frontOfMax = 1;
    int backOfMax = 2;
    int arraySizeMax = 10;
    int[] queueOfMaxes = new int[10];
    int maxArrayCount = 1;
    
  public MaxQueue() {
    // students may insert whatever code they please, but the code may not
    // throw an exception
    queueOfMaxes[0] = Integer.MAX_VALUE;
    queueOfMaxes[1] = Integer.MIN_VALUE;
  }

  /** Insert the element with FIFO semantics
   *
   * @param x the element to be inserted.
   */
  public void enqueue(int x) {
      // your code goes here
      if(size == arraySize-1){ //array double
        int[] newQueue = new int[arraySize*2];
        for(int i = 0; i< arraySize; i++){
            newQueue[i+1] = queue[(front+i)%arraySize];
        }
        front = 1;
        back = arraySize;
        arraySize = arraySize*2;
        queue = newQueue;
      }
    queue[back % arraySize] = x;
    back++;
    System.out.println("maxarrayCount: "+maxArrayCount);
        if(maxArrayCount == arraySizeMax-1){//array double the max queue
            System.out.println("doubling the maxarray");
            int[] newMaxQueue = new int[arraySizeMax*2];
            for(int i = 0; i< arraySizeMax; i++){
                newMaxQueue[i+1] = queueOfMaxes[(frontOfMax+i)%arraySizeMax];
            }
            frontOfMax = 1;
            backOfMax = arraySizeMax;
            arraySizeMax = arraySizeMax*2;
            queueOfMaxes = newMaxQueue;
        }
    if(backOfMax>0 && x<= queueOfMaxes[backOfMax%arraySizeMax-1]){
        
        System.out.println("puting "+x +" as the last max");  
        queueOfMaxes[backOfMax%arraySizeMax] = x;
        
        
    }
    while(backOfMax>0 && backOfMax>frontOfMax && x>queueOfMaxes[backOfMax%arraySizeMax-1]){
        System.out.println("replacing "+queueOfMaxes[backOfMax%arraySizeMax-1] +" with "+ x);
        queueOfMaxes[backOfMax%arraySizeMax-1] = x;
        backOfMax--; 
        maxArrayCount--;
    }
    maxArrayCount++;
    backOfMax++;
    size++;
    System.out.println("putting "+x+" in the queue the maxArrayCount is"+maxArrayCount+ "the backMax is "+backOfMax);
  }

  /** Dequeue an element with FIFO semantics.
   *
   * @return the element that satisfies the FIFO semantics if the queue is not
   * empty.
   * @throws NoSuchElementException if the queue is empty
   */
  public int dequeue() {
      if(size == 0){
          throw new NoSuchElementException();
      }
      
      int intToReturn = queue[front%arraySize];
      System.out.println("intToRetrun: "+intToReturn);
      System.out.println("What should be max: "+queueOfMaxes[frontOfMax%arraySizeMax]);

     // queue[front] = null;
      front++;
      if(intToReturn == queueOfMaxes[frontOfMax%arraySizeMax]){
      //  queueOfMaxes[frontOfMax] = null;
        maxArrayCount--;
        frontOfMax++;
        System.out.println("What should be the next max after dequing: "+queueOfMaxes[frontOfMax%arraySizeMax]);
      }
      size--;
      return intToReturn;
  }

  /** Returns the number of elements in the queue
   *
   * @return number of elements in the queue
   */
  public int size() {
      return size;
  }


  /** Returns the element with the maximum value
   * 
   * @return the element with the maximum value
   * @throws NoSuchElementException if the queue is empty
   */
  public int max() {
    if(size == 0){
        throw new NoSuchElementException();
    }
      return queueOfMaxes[frontOfMax];
  }
  
} // MaxQueue
