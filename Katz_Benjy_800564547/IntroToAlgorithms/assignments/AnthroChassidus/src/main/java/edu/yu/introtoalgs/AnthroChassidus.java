package edu.yu.introtoalgs;

import java.util.HashMap;
import java.util.LinkedList;

/** Defines and implements the AnthroChassidus API per the requirements
 * documentation.
 *
 * @author Avraham Leff
 */

public class AnthroChassidus {
    HashMap<Integer, Node> map = new HashMap<Integer, Node>();
    int lowerBound = 0;
    int upperBound = 0;
    class Node{
        Node parent = this;
        int id = -1;
        int size = 1;
        public Node(int i){
            this.id = i;
        }
        public Node getParent(){
            return this.parent;
        }
        public void setParent(Node n){
            this.parent = n;
        }
        public void setSize(int j){
            this.size = j;
            System.out.println("Setting size for root "+this.getId()+" is size "+j );
        }
        public int getSize(){
            return this.size;
        }
        public int getId(){
            return this.id;
        }
        @Override
        public boolean equals(Object o){
            if(this.getId()==((Node)o).getId()) return true;
            return false;
        }
    }
  /** Constructor.  When the constructor completes, ALL necessary processing
   * for subsequent API calls have been made such that any subsequent call will
   * incur an O(1) cost.
   *
   * @param n the size of the underlying population that we're investigating:
   * need not correspond in any way to the number of people actually
   * interviewed (i.e., the number of elements in the "a" and "b" parameters).
   * Must be greater than 2.
   * @param a interviewed people, element value corresponds to a unique "person
   * id" in the range 0..n-1
   * @param b interviewed people, element value corresponds to a unique "person
   * id" in the range 0..n-1.  Pairs of a_i and b_i entries represent the fact
   * that the corresponding people follow the same Chassidus (without
   * specifying what that Chassidus is).
   */
  public AnthroChassidus(final int n, final int[] a, final int[] b) {
      if(a.length != b.length){
          throw new IllegalArgumentException();
      }
     upperBound = n;
     for(int i = 0; i < a.length; i++){
        if(map.get(a[i]) == null){
            map.put(a[i], new Node(a[i]));
        }
        if(map.get(b[i]) == null){
            map.put(b[i], new Node(b[i]));
        }
       // System.out.println("")
        union(map.get(a[i]), map.get(b[i]));
     }
     map.forEach((k, v)->{
        System.out.println("looking at: "+v.getId());
        if(v.getParent() == v){
            System.out.println("found a root");
            lowerBound++;
            upperBound = upperBound - v.getSize() + 1;
            System.out.println("total chasidusses is: "+lowerBound);
        }
     });
  }
  private Node find(Node a){
    System.out.print("Node :"+a.getId());
    LinkedList<Node> path = new LinkedList<Node>();
    while(a.getParent()!= a){
        a = a.getParent();
        path.add(a);
    }
    System.out.println(" has the root "+a.getId());
    for(Node e: path){
        System.out.println("Flattening the tree");
        e.setParent(a);//flattens the tree!
    }
    path = null;
    return a;
  }
  private void union(Node a, Node b){
    Node roota = find(a);
    //a.setParent(roota);//flattens the tree!
    Node rootb = find(b);
   // b.setParent(rootb);//flattens the tree!
   if(roota.equals(rootb)){
    System.out.println("The roots are equal: "+roota.getId()+" "+rootb.getId());
       return;
   }
    if(roota.getSize()>rootb.getSize()){
        System.out.println("Doing union a>b");
        rootb.setParent(roota);
        roota.setSize(roota.getSize()+rootb.getSize());//add size
        
    }
    else{
        System.out.println("Doing union a<=b");
        roota.setParent(rootb);
        rootb.setSize(roota.getSize()+rootb.getSize());//add size   
    }
    return;
  }
  /** Return the tightest value less than or equal to "n" specifying how many
   * types of Chassidus exist in the population: this answer is inferred from
   * the interviewers data supplied to the constructor
   *
   * @return tightest possible lower bound on the number of Chassidus in the
   * underlying population.
   */
  public int getLowerBoundOnChassidusTypes() {
      //THIS IS NOT COMPLETE!!!! although it might be now!
    return upperBound;
  }

  /** Return the number of interviewed people who follow the same Chassidus as
   * this person.
   *
   * @param id uniquely identifies the interviewed person
   * @return the number of interviewed people who follow the same Chassidus as
   * this person.
   */
  public int nShareSameChassidus(final int id) {
    return find(map.get(id)).getSize();

  }

} // class