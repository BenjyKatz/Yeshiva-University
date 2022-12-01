package edu.yu.introtoalgs;
import edu.yu.introtoalgs.MaxQueue;
import jdk.internal.jline.internal.TestAccessible;
import java.util.NoSuchElementException;
import java.lang.Math;

import org.junit.Test;
import static org.junit.Assert.*;
import  org.junit.Assert;

//import org.junit.Assert.*;
public class MaxQueueTest{
	@Test
	public void basicTest(){
        MaxQueue myQueue = new MaxQueue();
		myQueue.enqueue(10);
        myQueue.enqueue(9);
        myQueue.enqueue(8);
        myQueue.enqueue(7);
        myQueue.enqueue(6);
        myQueue.enqueue(5);
        myQueue.enqueue(4);
        myQueue.enqueue(3);
        myQueue.enqueue(2);
        myQueue.enqueue(1);
        myQueue.enqueue(0);
        myQueue.enqueue(-1);
        int max = myQueue.max();
        assertEquals(max,10);
        myQueue.enqueue(1000);
        int i = 0;
        for(int y = 0; y<12; y++){
       i = myQueue.dequeue();
        }
       System.out.println(i);
       assertEquals(i,-1);
       assertEquals(myQueue.max(),1000);
	}
    @Test
    public void fillTheQueue(){
        MaxQueue myFullQueue = new MaxQueue();
        for(int i = 200; i>0; i-- ){
            myFullQueue.enqueue(i);
        }
        int dq;
        for(int j = 0; j<100; j++){
            dq = myFullQueue.dequeue();
            assertEquals(200-j, dq);
        }
        for(int i = 200; i>0; i-- ){
            myFullQueue.enqueue(i);
        }
    }
    @Test
    public void standardTest(){
        MaxQueue myStandardQueue = new MaxQueue();
        myStandardQueue.enqueue(10);
        myStandardQueue.enqueue(100);
        myStandardQueue.enqueue(14);
        myStandardQueue.enqueue(12);
        myStandardQueue.enqueue(20);
        myStandardQueue.enqueue(0);
        myStandardQueue.enqueue(-10);
        myStandardQueue.enqueue(5);
        myStandardQueue.enqueue(1);
        myStandardQueue.enqueue(14);
        myStandardQueue.enqueue(5);
        assertEquals(100, myStandardQueue.max());
        assertEquals(10, myStandardQueue.dequeue());
        assertEquals(100, myStandardQueue.max());
        assertEquals(100, myStandardQueue.dequeue());
        assertEquals(20, myStandardQueue.max());
        assertEquals(14, myStandardQueue.dequeue());
        assertEquals(12, myStandardQueue.dequeue());
        assertEquals(20, myStandardQueue.dequeue());
        assertEquals(14, myStandardQueue.max());
        myStandardQueue.enqueue(15);
        assertEquals(15, myStandardQueue.max());
        assertEquals(0, myStandardQueue.dequeue());
        assertEquals(-10, myStandardQueue.dequeue());
        assertEquals(5, myStandardQueue.dequeue());
        assertEquals(1, myStandardQueue.dequeue());
        assertEquals(14, myStandardQueue.dequeue());
        assertEquals(5, myStandardQueue.dequeue());
        assertEquals(15, myStandardQueue.dequeue());
        boolean thrown = false;

  try {
    myStandardQueue.dequeue();
  } catch (NoSuchElementException e) {
    thrown = true;
  }

  assertTrue(thrown);
    }
   /* @Test
    public void massiveQ(){
        MaxQueue myHugeQueue = new MaxQueue();
        for(int i = 1; i<500000000;i++){
            myHugeQueue.enqueue(i*(int)(Math.random()*1000));
            if (i%15==0){
                myHugeQueue.dequeue();
                myHugeQueue.max();
            }
        }
    }*/
}