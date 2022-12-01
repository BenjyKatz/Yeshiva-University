package edu.yu.da;
import org.junit.Test;
import edu.yu.da.MaximizePayout;
//import edu.yu.da.MultiMerge;
import java.util.*;

import static org.junit.Assert.*;
import  org.junit.Assert;

public class PayoutTest {
    @Test
    public void basicTest(){
        
        MaximizePayout mp = new MaximizePayout();
        List<Long> A = new LinkedList<Long>();
        List<Long> B = new LinkedList<Long>();
        A.add(3L);
        A.add(2L);
        A.add(1L);
       // A.add(300L);


        B.add(2L);
        B.add(3L);
        B.add(1L);
        //B.add(0L);

        System.out.println(mp.max(A, B));
        
        
    }
    
        
}