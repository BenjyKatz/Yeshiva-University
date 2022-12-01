package edu.yu.introtoalgs;
import org.junit.Test;


import java.util.*;
import edu.yu.introtoalgs.GraphsAndMazes;
import edu.yu.introtoalgs.GraphsAndMazes.Coordinate;
public class GraphTest{
    @Test
    public void basicTest(){
    System.out.println("testing basic try1");
    final int[][] exampleMaze = {
      {0, 0, 0},
      {0, 1, 1},
      {0, 1, 0}
    };

    final Coordinate start = new Coordinate(2, 0);
    final Coordinate end = new Coordinate(0, 2);
    final List<Coordinate> path = GraphsAndMazes.searchMaze(exampleMaze, start, end);
    System.out.println("path="+path);
    for(Coordinate cor: path){
        System.out.print(cor.getRow()+","+cor.getColumn()+" ");
    }
    }

    @Test
    public void jaggedTest(){
    System.out.println("testing jagged");
    final int[][] exampleMaze = {
      {0, 0, 0, 0, 0, 0},
      {0, 1, 1},
      {0, 1, 0}
    };

    final Coordinate start = new Coordinate(0, 4);
    final Coordinate end = new Coordinate(2, 2);
    final List<Coordinate> path = GraphsAndMazes.searchMaze(exampleMaze, start, end);
    System.out.println("path="+path);
    for(Coordinate cor: path){
        System.out.print(cor.getRow()+","+cor.getColumn()+" ");
    }
    }

    @Test
    public void bigTest(){
    System.out.println("testing big");
    final int[][] exampleMaze = {
        
            {1,0,1,1,1,1,1,1,1,1},
            {1,0,1,0,0,0,0,0,0,1},
            {1,0,1,0,0,1,1,0,0,1},
            {1,0,1,1,0,0,1,1,0,1},
            {1,0,0,1,0,0,0,1,0,1},
            {1,0,0,1,0,1,0,1,0,1},
            {1,1,0,1,0,1,0,1,0,1},
            {1,0,0,1,1,1,0,1,0,1},
            {1,0,0,0,0,0,0,1,0,1},
            {1,1,1,1,1,1,1,1,0,1}
            
    };

    final Coordinate start = new Coordinate(9, 8);
    final Coordinate end = new Coordinate(0, 1);
    final List<Coordinate> path = GraphsAndMazes.searchMaze(exampleMaze, start, end);
    System.out.println("path="+path);
    for(Coordinate cor: path){
        System.out.print(cor.getRow()+","+cor.getColumn()+" ");
    }
    }

    @Test
    public void reallyBigTest(){
        /*
    System.out.println("testing massive");
     int[][] exampleMaze = new int[1000000][50];
     int[] row = {1,0,1,1,1,0,1,1,0,0,0,1,0,0,1,1,1,0,0,1,0,1,1,0,1,1,0,0,0,1,1,0,0,1,1,1,0,1,0,0,1,1,1,1,1,0,0,1,0,1};
     int[] zeRow = new int[50];
     for(int i =0; i<50; i++){
         zeRow[i]=0;
     }

    for(int i = 0; i<1000000; i++){
        exampleMaze[i] = row;
    }
    exampleMaze[0]= zeRow;
    exampleMaze[99999] = zeRow;


    final Coordinate start = new Coordinate(9, 1);
    final Coordinate end = new Coordinate(110, 9);
    final List<Coordinate> path = GraphsAndMazes.searchMaze(exampleMaze, start, end);
    //System.out.println("path="+path);
    //for(Coordinate cor: path){
   //     System.out.print(cor.getRow()+","+cor.getColumn()+" ");
   // }
   System.out.println("done");
   */
    }
}