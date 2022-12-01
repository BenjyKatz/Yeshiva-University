package edu.yu.da;
import org.junit.Test;
import edu.yu.da.MultiMergeBase.*;
import edu.yu.da.MultiMerge;
import java.util.*;

import static org.junit.Assert.*;
import  org.junit.Assert;

public class MergeTest {
    @Test
    public void basicTest(){
        
        MultiMerge mm = new MultiMerge();
        int[][]sortMe = {{1,2,5,8},{2,3,12,19},{-1,0,3,9}, {-8, 5, 8, 10}, {1,3,7,9}};
        int[] result = mm.merge(sortMe);
        System.out.println("basic test");
        for(int i = 0; i < result.length; i++  )
        System.out.print(result[i]+" ");
        System.out.println("Merges: "+ mm.getNCombinedMerges());
        
    }
    @Test
    public void evenTest(){
        MultiMerge mm = new MultiMerge();
        int[][]sortMe = {{1,2,5,8},{2,3,12,19},{-1,0,3,9}, {-8, 5, 8, 10}, {1,3,7,9}, {-3, 10, 11, 12}};
        int[] result = mm.merge(sortMe);
        System.out.println("even test");
        for(int i = 0; i < result.length; i++  )
        System.out.print(result[i]+" ");
        System.out.println("Merges: "+ mm.getNCombinedMerges());
        
    }
    @Test
    public void powerOfTwoTest(){
        MultiMerge mm = new MultiMerge();
        int[][]sortMe = {{1,2,5,8},{2,3,12,19},{-1,0,3,9}, {-8, 5, 8, 10}};
        int[] result = mm.merge(sortMe);
        System.out.println("power of two test");
        for(int i = 0; i < result.length; i++  )
        System.out.print(result[i]+" ");
        System.out.println("Merges: "+ mm.getNCombinedMerges());   
        
    }
}