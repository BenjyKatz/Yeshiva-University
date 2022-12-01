package edu.yu.da;
import org.junit.Test;
import edu.yu.da.StockYourBookshelf;
import edu.yu.da.StockYourBookshelfI;
//import edu.yu.da.MultiMerge;
import java.util.*;

import static org.junit.Assert.*;
import  org.junit.Assert;

public class BookShelfTest {
    @Test
    public void basicTest(){
        System.out.println("Start test");
        StockYourBookshelf syb = new StockYourBookshelf();
        LinkedHashMap<String, List<Integer>> map = new LinkedHashMap<String, List<Integer>>();

        List<Integer> G = new LinkedList<Integer>();
        List<Integer> T = new LinkedList<Integer>();
        List<Integer> M = new LinkedList<Integer>();
        List<Integer> A = new LinkedList<Integer>();
        List<Integer> B = new LinkedList<Integer>();
        List<Integer> C = new LinkedList<Integer>();
        List<Integer> D = new LinkedList<Integer>();
        G.add(3);
        G.add(2);
        G.add(1);
       // A.add(300L);


        T.add(3);
        T.add(8);
        T.add(7);
        T.add(1);
        T.add(20);
        
        A.add(5);
        A.add(7);
        A.add(20);
        A.add(100);
        A.add(3);

        B.add(3);
        B.add(8);
        B.add(7);
        B.add(1);
        B.add(20);

        C.add(3);
        C.add(80);
        C.add(2);
        C.add(9);
        C.add(20);

        D.add(2);
        D.add(8);
        D.add(3);

        M.add(2);
        M.add(8);
        M.add(3);

        map.put("G", G);
        map.put("T", T);
        map.put("M", M);
        map.put("A", A);
        map.put("B", B);
        map.put("C", C);
        map.put("D", D);
      //  syb.maxAmountThatCanBeSpent(10, map);
      for(int i = 0; i < 241; i++){
        int max = syb.maxAmountThatCanBeSpent(i, map);
       // if(!(max== i)){
          System.out.println("for budget: "+i+" max is: "+ max);
          System.out.println(syb.solution());
        }
     // }
    /*  int max = syb.maxAmountThatCanBeSpent(20, map);
      System.out.println("for budget: "+20+" max is: "+ max);*/
      

     //   System.out.println(syb.maxAmountThatCanBeSpent(500, map));
     //   System.out.println(syb.solution());
        //B.add(0L);

        
        
        
    }
    @Test
    public void justOne(){
        System.out.println("Start test 2");
        StockYourBookshelf syb = new StockYourBookshelf();
        LinkedHashMap<String, List<Integer>> map = new LinkedHashMap<String, List<Integer>>();
      //  System.out.println(syb.solution());
        List<Integer> G = new LinkedList<Integer>();
        G.add(3);
        G.add(2);
        G.add(1);
        map.put("G", G);
        int max = syb.maxAmountThatCanBeSpent(2, map);
   
          System.out.println("for budget: "+5+" max is: "+ max);
          System.out.println(syb.solution());

    }
    
        
}