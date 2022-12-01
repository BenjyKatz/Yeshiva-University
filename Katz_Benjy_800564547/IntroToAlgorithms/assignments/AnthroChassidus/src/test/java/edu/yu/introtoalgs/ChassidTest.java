package edu.yu.introtoalgs;
import org.junit.Test;

import edu.yu.introtoalgs.AnthroChassidus;
//import jdk.internal.jline.internal.TestAccessible;
//import jdk.internal.jline.internal.TestAccessible;

import static org.junit.Assert.*;
import  org.junit.Assert;

public class ChassidTest {
    @Test
    public void basicTest(){
        int[] a = {0, 5, 1, 1,};
        int[] b = {5, 7, 4, 2};
        AnthroChassidus ac = new AnthroChassidus(10, a, b);
        assertEquals(6, ac.getLowerBoundOnChassidusTypes());
        assertEquals(3, ac.nShareSameChassidus(0));
        assertEquals(3, ac.nShareSameChassidus(4));
    }
    @Test
    public void testMinimal(){
        int n = 50;
        int[] a = {0};
        int[] b = {1};
        AnthroChassidus ac = new AnthroChassidus(n, a, b);
        assertEquals(ac.getLowerBoundOnChassidusTypes(), 49);
        assertEquals(ac.nShareSameChassidus(0), 2);
        assertEquals(ac.nShareSameChassidus(1), 2);
    }
    @Test
    public void testRepeat(){
        int n = 5;
        int[] a = {0, 1, 2, 3, 4, 0, 1, 2, 3, 4 };
        int[] b = {4, 3, 2, 1, 0, 4, 3, 2, 1, 0};
        AnthroChassidus ac = new AnthroChassidus(n, a, b);
        assertEquals(ac.getLowerBoundOnChassidusTypes(), 3);
        assertEquals(ac.nShareSameChassidus(0), 2);
        assertEquals(ac.nShareSameChassidus(1), 2);
    }
    @Test
    public void allOne(){
        int n = 5;
        int[] a = {0, 1, 2, 3};
        int[] b = {1, 2, 3, 4};
        AnthroChassidus ac = new AnthroChassidus(n, a, b);
        assertEquals(ac.getLowerBoundOnChassidusTypes(), 1);
        assertEquals(ac.nShareSameChassidus(0), 5);
        assertEquals(ac.nShareSameChassidus(1), 5);
    } 
    @Test
    public void massive(){
        //seems like linear time!
        int n = 400000;
        int[] a = new int[n];
        int[] b = new int[n];
        
        for(int i = 0; i<n; i++){
            a[i] = n-i;
            b[i] = i;
        }
        AnthroChassidus ac = new AnthroChassidus(n, a, b);
        System.out.println(ac.nShareSameChassidus(0));
        System.out.println(ac.getLowerBoundOnChassidusTypes());
        

    }
    @Test
    public void randomMassive(){
        //seems like linear time!
        int n = 400000;
        int[] a = new int[n];
        int[] b = new int[n];
        
        for(int i = 0; i<n; i++){
            a[i] = (int)(Math.random()*n);
            b[i] = (int)(Math.random()*n);
        }
        AnthroChassidus ac = new AnthroChassidus(n, a, b);
        System.out.println("nShare "+ ac.nShareSameChassidus(50));
        System.out.println("LBow "+ac.getLowerBoundOnChassidusTypes());
        

    }
}
