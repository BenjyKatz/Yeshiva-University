package edu.yu.introtoalgs;

import java.util.*;

public class GraphsAndMazes {
    

  /** A immutable coordinate in 2D space.
   *
   * Students must NOT modify the constructor (or its semantics) in any way,
   * but can ADD whatever they choose.
   */
  public static class Coordinate { 
    public final int x, y;
    
    /** Constructor, defines an immutable coordinate in 2D space.
     *
     * @param x specifies x coordinate
     * @param y specifies x coordinate
     */
    public Coordinate(final int x, final int y) {
      this.x = x;
      this.y = y;
    }
    public int getRow(){
        return this.x;
    }
    public int getColumn(){
        return this.y;
    }
    @Override
    public int hashCode(){
        return this.x*31+this.y;
    }
    @Override
    public boolean equals(Object o){
        if((o instanceof Coordinate) && ((Coordinate)o).getRow()== this.x && ((Coordinate)o).getColumn()==this.y){
            return true;
        }
        return false;
    }
    @Override
    public String toString(){
        return "("+this.getRow()+","+this.getColumn()+")";
    }

    /** Add any methods, instance variables, static variables that you choose
     */
  } // Coordinate class

  /** Given a maze (specified by a 2D integer array, and start and end
   * Coordinate instances), return a path (beginning with the start
   * coordinate, and terminating wih the end coordinate), that legally
   * traverses the maze from the start to end coordinates.  If no such
   * path exists, returns an empty list.  The path need need not be a
   * "shortest path".
   *
   * @param maze 2D int array whose "0" entries are interpreted as
   * "coordinates that can be navigated to in a maze traversal (can be
   * part of a maze path)" and "1" entries are interpreted as
   * "coordinates that cannot be navigated to (part of a maze wall)".
   * @param start maze navigation must begin here, must have a value
   * of "0"
   * @param end maze navigation must terminate here, must have a value
   * of "0"
   * @return a path, beginning with the start coordinate, terminating
   * with the end coordinate, and intervening elements represent a
   * legal navigation from maze start to maze end.  If no such path
   * exists, returns an empty list.  A legal navigation may only
   * traverse maze coordinates, may not contain coordinates whose
   * value is "1", may only traverse from a coordinate to one of its
   * immediate neighbors using one of the standard four compass
   * directions (no diagonal movement allowed).  A legal path may not
   * contain a cycle.  It is legal for a path to contain only the
   * start coordinate, if the start coordinate is equal to the end
   * coordinate.
   */


  public static List<Coordinate> searchMaze (final int[][] maze, final Coordinate start, final Coordinate end){
    int x; 
    int y; 
    try{
         x = maze[start.getRow()][start.getColumn()];
         y = maze[end.getRow()][end.getColumn()];
      }
      catch(ArrayIndexOutOfBoundsException e){
          throw new IllegalArgumentException();
      }
      if(x == 1 || y == 1){
          throw new IllegalArgumentException();
      }
    List<Coordinate> path = new LinkedList<Coordinate>();
    List<Coordinate> stack = new LinkedList<Coordinate>();
    HashMap<Coordinate, Boolean> marked = new HashMap<Coordinate, Boolean>();
    Coordinate currentNode = start;
    marked.put(start, true);
    stack.add(0, null);
    stack.add(0, start);
    //explore
    //try to look for all 4 children
    boolean justPopped = false;
    while(currentNode!=null){
        Coordinate nodeToExplore;
        int road;
        //look up
      //  System.out.println("Trying to look up");
        if(currentNode.getRow()-1>=0 && maze[currentNode.getRow()-1].length>currentNode.getColumn()){
            
            nodeToExplore = new Coordinate(currentNode.getRow()-1, currentNode.getColumn());
            road = maze[currentNode.getRow()-1][currentNode.getColumn()];
            if(marked.get(nodeToExplore)==null && road==0){
                if(justPopped){
                    stack.add(0, currentNode);
                    justPopped = false;
                }
         //       System.out.println("Exploring up");
                marked.put(nodeToExplore,true);
                
                currentNode = nodeToExplore;
                stack.add(0, currentNode);
                if(currentNode.equals(end)){
                    return stackToList(stack);
                }
                continue;
            }
        }
        //look right
     //   System.out.println("Trying to look right");
        if(currentNode.getColumn()+1<maze[currentNode.getRow()].length){
           // System.out.println("can look right column: "+currentNode.getColumn()+" out of "+ maze[currentNode.getRow()].length);
            nodeToExplore = new Coordinate(currentNode.getRow(), currentNode.getColumn()+1);
            road = maze[currentNode.getRow()][currentNode.getColumn()+1];
            if(marked.get(nodeToExplore)==null && road==0){
                if(justPopped){
                    stack.add(0, currentNode);
                    justPopped = false;
                }
        //        System.out.println("Exploring right");
                marked.put(nodeToExplore,true);
                
                currentNode = nodeToExplore;
                stack.add(0, currentNode);
                if(currentNode.equals(end)){
                    return stackToList(stack);
                }
                continue;
            }
        }

        //look down
    //    System.out.println("Trying to look down");
        if(currentNode.getRow()+1<maze.length && maze[currentNode.getRow()+1].length>currentNode.getColumn()){
           // System.out.println("Can look down");
            nodeToExplore = new Coordinate(currentNode.getRow()+1, currentNode.getColumn());
            //System.out.println("Node to explore in look down: "+ nodeToExplore + marked.get(nodeToExplore));
            road = maze[currentNode.getRow()+1][currentNode.getColumn()];
          //  System.out.println("road: "+road);
            if(marked.get(nodeToExplore)==null && road==0){
                if(justPopped){
                    stack.add(0, currentNode);
                    justPopped = false;
                }
       //         System.out.println("Exploring down");
                marked.put(nodeToExplore,true);
                
                currentNode = nodeToExplore;
                stack.add(0, currentNode);
                if(currentNode.equals(end)){
                    return stackToList(stack);
                }
                continue;
            }
        }
        //look left
    //    System.out.println("Trying to look left");
        if(currentNode.getColumn()-1>=0){
           // System.out.println("Can look left");
            nodeToExplore = new Coordinate(currentNode.getRow(), currentNode.getColumn()-1);
            road = maze[currentNode.getRow()][currentNode.getColumn()-1];
            if(marked.get(nodeToExplore)==null && road==0){
                if(justPopped){
                    stack.add(0, currentNode);
                    justPopped = false;
                }
     //           System.out.println("Exploring left");
                marked.put(nodeToExplore,true);
                
                currentNode = nodeToExplore;
                stack.add(0, currentNode);
                if(currentNode.equals(end)){
                    return stackToList(stack);
                }
                continue;
            }
        }
        //stack.remove(0);//pop itself off stack
        currentNode = stack.remove(0);
        justPopped = true;

       /* if(currentNode!=null){
            System.out.println("Popping off stack, new Node is: "+ currentNode.getRow()+","+currentNode.getColumn());
        }
        else System.out.println("last node popeped its null");*/
    }
    


    


    // fill me in
    return new LinkedList<Coordinate>();                // fix this!
  }
  private static List<Coordinate> stackToList(List<Coordinate> stack){
    List<Coordinate> listToReturn = new LinkedList<Coordinate>();
    
    while(!stack.isEmpty()){
       listToReturn.add(0, stack.remove(0));
    }
   // return stack;
   listToReturn.remove(0);
    return listToReturn;
} 

  /** minimal main() demonstrates use of APIs
   */
  /*
  public static void main (final String[] args) {
    System.out.println("hey");
    final int[][] exampleMaze = {
      {0, 0, 0},
      {0, 1, 1},
      {0, 1, 0}
    };

    final Coordinate start = new Coordinate(2, 0);
    final Coordinate end = new Coordinate(0, 2);
    final List<Coordinate> path = searchMaze(exampleMaze, start, end);
    System.out.println("path="+path);
  }
*/

}