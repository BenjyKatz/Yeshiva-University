package edu.yu.da;
import org.junit.Test;
import edu.yu.da.ShortestCycleBase.*;
import edu.yu.da.ShortestCycle;
import java.util.*;

import static org.junit.Assert.*;
import  org.junit.Assert;

public class CycleTest {
    @Test
    public void basicTest(){
        List<Edge> edges= new LinkedList<Edge>();
        edges.add(new Edge(0, 1, 30));
        edges.add(new Edge(1, 2, 3));
        edges.add(new Edge(2, 0, 10));
        ShortestCycleBase scb = new ShortestCycle(edges, new Edge(2, 0, 10));
        System.out.println(scb.doIt());

    }
    @Test
    public void biggerTest(){
        List<Edge> edges= new LinkedList<Edge>();
        edges.add(new Edge(0, 1, 30));
        edges.add(new Edge(0, 2, 3));
        edges.add(new Edge(0, 6, 10));
        edges.add(new Edge(6, 4, 10));
        edges.add(new Edge(4, 5, 100));
        edges.add(new Edge(4, 3, 10));
        edges.add(new Edge(3, 5, 10));
        edges.add(new Edge(0, 5, 10));
        edges.add(new Edge(7, 8, 10));
        ShortestCycleBase scb = new ShortestCycle(edges, new Edge(0, 6, 10));
        System.out.println(scb.doIt());

    }
    @Test
    public void problemTest(){
        //{(1,2), weight=3.00}, {(1,3), weight=1.00}, {(2,3), weight=7.00}, {(2,4), weight=5.00}, {(3,4), weight=2.00}, {(2,5), weight=1.00}, {(4,5), weight=7.00}
        List<Edge> edges= new LinkedList<Edge>();
        edges.add(new Edge(1, 2, 3.00));
        edges.add(new Edge(1, 3, 1.00));
        edges.add(new Edge(2, 3, 7.00));
        edges.add(new Edge(2, 4, 5.00));
        edges.add(new Edge(3, 4, 2.00));
        edges.add(new Edge(2, 5, 1.00));
        edges.add(new Edge(4, 5, 7.00));
        List<Edge> solutionList = new LinkedList<Edge>();
        solutionList.add(new Edge(1, 2, 3.00));
        solutionList.add(new Edge(1, 3, 1.00));
        solutionList.add(new Edge(2, 3, 7.00));
        
        ShortestCycleBase scb = new ShortestCycle(edges, new Edge(1, 2, 3.00));
        assertEquals(scb.doIt(), solutionList);
        System.out.println(scb.doIt());

    }
}