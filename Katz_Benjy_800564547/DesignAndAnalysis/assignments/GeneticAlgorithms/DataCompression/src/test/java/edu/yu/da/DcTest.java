package edu.yu.da;
import org.junit.Test;

import java.util.*;
import edu.yu.da.GeneticAlgorithmConfig;
import edu.yu.da.DataCompression;
import static org.junit.Assert.*;
import  org.junit.Assert;

public class DcTest {
  /*  @Test
    public void basicTest(){
        System.out.println("Tournament test");
        List<String> ogList = new LinkedList<String>();
        for(int i = 40; i< 140; i++){
            ogList.add("hey "+ (char)(Math.random()*100));
        }
      
        DataCompression  SE = new DataCompression(ogList);

        DataCompression.SolutionI sol = SE.solveIt(new GeneticAlgorithmConfig(100, 200, 10000000, GeneticAlgorithmConfig.SelectionType.TOURNAMENT, .2, .5));
        System.out.println("gen "+sol.nGenerations());
        System.out.println("fit "+sol.relativeImprovement());

    }*/

 /*   @Test
    public void basicRouleteTest(){
        System.out.println("roulette test");
        List<String> ogList = new LinkedList<String>();
        for(int i = 40; i< 140; i++){
            ogList.add("hey "+ (char)(Math.random()*100));
        }
       
        DataCompression  SE = new DataCompression(ogList);

        DataCompression.SolutionI sol = SE.solveIt(new GeneticAlgorithmConfig(100, 200, 10000000, GeneticAlgorithmConfig.SelectionType.ROULETTE, .2, .5));
        System.out.println("gen "+sol.nGenerations());
        System.out.println("fit "+sol.relativeImprovement());

    }*/
    @Test
    public void replicate(){
        System.out.println("replicate test");
        List<String> ogList = new LinkedList<String>();
        for(int i = 40; i< 140; i++){
            ogList.add(""+ (char)(Math.random()*100)+""+ (char)(Math.random()*100)+""+ (char)(Math.random()*100)+""+ (char)(Math.random()*100)+""+ (char)(Math.random()*100)+""+(char)(Math.random()*100));
        }
      
        DataCompression  SE = new DataCompression(ogList);

        DataCompression.SolutionI sol = SE.solveIt(new GeneticAlgorithmConfig(20, 100, 10000000, GeneticAlgorithmConfig.SelectionType.ROULETTE, .1, .7));
        System.out.println("gen "+sol.nGenerations());
        System.out.println("fit "+sol.relativeImprovement());

    }
    List<String> strings= new ArrayList<>();

    public void DataCompressionTest(){
        fill();
    }

    public void fill(){
        strings.add("Abe");
        strings.add("Benjamin");
        strings.add("Lewis");
        strings.add("Lewis");
        strings.add("Louis");
        strings.add("Sidney");
        strings.add("Sebastian");
        strings.add("Colton");
        strings.add("Jamison");
        strings.add("Christian");
        strings.add("Jamison");
        strings.add("George");
        strings.add("John");
        strings.add("James");
        strings.add("Martin");
        strings.add("Taylor");
        strings.add("Benjamin");
        strings.add("Lewis");
        strings.add("Lewis");
        strings.add("Louis");
        strings.add("Sidney");
        strings.add("Sebastian");
        strings.add("Colton");
        strings.add("Jamison");
        strings.add("Christian");
        strings.add("Colton");
        strings.add("Evgeni");
        strings.add("Justina");
        strings.add("Jamison");
        strings.add("Max");
        strings.add("Max");
    }
    @Test
    public void replicatebro(){
        System.out.println("replicate test");
        DataCompressionTest();
        List<String> ogList = new LinkedList<String>();
        for(int i = 40; i< 140; i++){
            ogList.add("hey "+ (char)(Math.random()*100));
        }
      
        DataCompression  SE = new DataCompression(strings);

        DataCompression.SolutionI sol = SE.solveIt(new GeneticAlgorithmConfig(20, 100, 10000000, GeneticAlgorithmConfig.SelectionType.TOURNAMENT, .1, .7));
        System.out.println("gen "+sol.nGenerations());
        System.out.println("fit "+sol.relativeImprovement());
        List<String> returnedList = sol.getList();
        System.out.println(sol.getList());
        Collections.sort(returnedList);
        Collections.sort(strings);
        System.out.println(returnedList.equals(strings));
       
       
        System.out.println(sol.getList().size());
        System.out.println(strings.size());

    }
}