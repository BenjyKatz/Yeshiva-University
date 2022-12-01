package edu.yu.da;
import org.junit.Test;

import java.util.*;
import edu.yu.da.GeneticAlgorithmConfig;
import edu.yu.da.SimpleEquation;
import static org.junit.Assert.*;
import  org.junit.Assert;

public class GaTest {
    @Test
    public void basicTest(){
        System.out.println("Tournament test");
      SimpleEquation  SE = new SimpleEquation();

        SimpleEquation.SolutionI sol = SE.solveIt(new GeneticAlgorithmConfig(100, 100, 13, GeneticAlgorithmConfig.SelectionType.TOURNAMENT, .2, .5));
        System.out.println("gen "+sol.nGenerations());
        System.out.println("fit "+sol.fitness());

    }
    @Test
    public void firstGenTest(){
        System.out.println("first gen test");
        SimpleEquation  SE = new SimpleEquation();

        SimpleEquation.SolutionI sol = SE.solveIt(new GeneticAlgorithmConfig(400, 1, 4, GeneticAlgorithmConfig.SelectionType.TOURNAMENT, .2, .5));
        System.out.println("gen "+sol.nGenerations());
        System.out.println("fit "+sol.fitness());

    }

    @Test
    public void rouletteTest(){
        System.out.println("rouletterTest");
        SimpleEquation  SE = new SimpleEquation();

        SimpleEquation.SolutionI sol = SE.solveIt(new GeneticAlgorithmConfig(100, 100, 13, GeneticAlgorithmConfig.SelectionType.ROULETTE, 0.2, 0.5));
        System.out.println("gen "+sol.nGenerations());
        System.out.println("fit "+sol.fitness());

    }
}