package edu.yu.da;
import org.junit.Test;

import edu.yu.da.WaitNoMore;

//import edu.yu.da.MultiMerge;
import java.util.*;

import static org.junit.Assert.*;
import  org.junit.Assert;

public class WaitTest {
    @Test
    public void basicTest(){
        
        WaitNoMore WNM = new WaitNoMore();
        int[] durs = {100, 5, 10, 20};
        int[] weights = {10, 0, 100, 30};
        System.out.println(WNM.minTotalWaitingTime(durs, weights));
        
        
    }
    
        
}