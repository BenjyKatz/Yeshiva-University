package edu.yu.da;

/** Stubbed implementation of the SimpleEquationI interface.
 *
 * @author Avraham Leff
 */

import static edu.yu.da.SimpleEquationI.SolutionI;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.LinkedList;
import java.util.*;
import edu.yu.da.GeneticAlgorithmConfig.SelectionType;

public class SimpleEquation implements SimpleEquationI {

    List<Solution> population;
    double mutationProbability;
    double crossoverProbability;
    Solution bestSolution = new Solution(0, 1000, 0);
    int generation = 0;
    double threshold;
  /** Constructor.
   *
   * Students MAY NOT define any other constructor signature.  They
   * MAY change the stubbed implemention in any way they choose.
   */
  public SimpleEquation() {

  }

  @Override
  public SolutionI solveIt(final GeneticAlgorithmConfig gac) {
    
    threshold = gac.getThreshold();
    mutationProbability = gac.getMutationProbability();
    crossoverProbability = gac.getCrossoverProbability();
    //define the population
    population = new LinkedList<Solution>();
    //populate the population
    for(int i = 0; i<gac.getInitialPopulationSize(); i++){
        population.add(new Solution( (int)Math.floor(Math.random()*101), (int)Math.floor(Math.random()*101), 0));
    }
    for(; generation< gac.getMaxGenerations(); generation++){
     //   System.out.println("Gen: "+ generation);
     //   System.out.println("Best We Got: "+ bestSolution.fitness());
        //evolve
        if(gac.getSelectionType() == SelectionType.TOURNAMENT){
            tournament();
        }
        else{
            roulette();
        }
        if(bestSolution.fitness()>= gac.getThreshold()){
            return bestSolution;
        }
    }
      return bestSolution;
  }

  private void tournament(){
    List<Solution> axlList = new LinkedList<Solution>();
    List<Solution> matingPool = new LinkedList<Solution>();
    axlList.addAll(population);
    
    for(Solution s: population){      
        Solution randSolution = axlList.get((int)Math.floor(axlList.size()*Math.random()));
        if(s.fitness()>bestSolution.fitness()){
       //     System.out.println("new best solution found in gen "+s.nGenerations()+ "fitness "+s.fitness());
            bestSolution = s;
        }
        if(s.fitness()>= randSolution.fitness()){
            matingPool.add(s);
        }
        else{
            matingPool.add(randSolution);
        }
    }
    Collections.shuffle(matingPool);
    int index = 0;
    List<Solution> newGen = new LinkedList<Solution>();
    while(index<matingPool.size()/2){  
        newGen.addAll(mate(matingPool.get(index), matingPool.get(matingPool.size()-index-1)));
     //   matingPool.remove(index);
     //   matingPool.remove(index+1);
        index++;
    }
    population = newGen;

  }
  private void roulette(){
    double totalFit = 0;
    List<Solution> matingPool = new LinkedList<Solution>();
    //take the percent multiply by the population size*5 and choose randomly the size of the population
    //this solution unfortunetly gives greater fitness to -1 than 0
    for(Solution s: population){
        if(s.fitness()>bestSolution.fitness()){
       //     System.out.println("new best solution found in gen "+s.nGenerations()+ "fitness "+s.fitness());
            bestSolution = s;
        }

        if(s.fitness()<0){
            totalFit=totalFit+(-1/s.fitness());
        }
        else{
            totalFit = totalFit+s.fitness();
        }
    }
    List<Solution> matingPoolToChooseFrom = new LinkedList<Solution>();
    int slots = 0; 
    for(Solution s: population){
        if(s.fitness()<0){
            slots=(int)Math.floor(5*((-1/s.fitness())/totalFit)*population.size());
           // System.out.println("negative slots "+ slots);
        }
        else{
            slots = (int)Math.floor(5*population.size()*(s.fitness()/totalFit));
         //   System.out.println("positive slots "+ slots);
        }
        for(int i = 0; i<slots; i++){
            matingPoolToChooseFrom.add(s);
        }
        slots = 0;
    }
    for(int i = 0; i< population.size(); i++){
        matingPool.add(matingPoolToChooseFrom.get((int)Math.floor(Math.random()*matingPoolToChooseFrom.size())));
    }
    
    int index = 0;
    List<Solution> newGen = new LinkedList<Solution>();
    while(index<matingPool.size()/2){  
        newGen.addAll(mate(matingPool.get(index), matingPool.get(matingPool.size()-index-1)));
     //   matingPool.remove(index);
     //   matingPool.remove(index+1);
        index++;
    }
    population = newGen;

   
  }
  private List<Solution> mate(Solution male, Solution female){
      List<Solution> newGen = new LinkedList<Solution>();
      if(Math.random()<crossoverProbability){//aka crossover
        int[] chr = {male.getX(), female.getY(), female.getX(), male.getY()};
        for(int i =0; i<4; i++){
            if(Math.random()<mutationProbability){
                chr[i] = (int)Math.floor(Math.random()*101);
            }
        }
        newGen.add(new Solution(chr[0], chr[1], generation));
        newGen.add(new Solution(chr[2], chr[3], generation));
      }
      else{//aka no crossover
        int[] chr = {male.getX(), male.getY(), female.getX(), female.getY()};
        for(int i =0; i<4; i++){
            if(Math.random()<mutationProbability){
                chr[i] = (int)Math.floor(Math.random()*101);
            }
        }
        newGen.add(new Solution(chr[0], chr[1], generation));
        newGen.add(new Solution(chr[2], chr[3], generation));
      }
      return newGen;

  }
  public class Solution implements SolutionI{
    int x;
    int y;
    int gen;
    public Solution(int x, int y, int gen){
        this.x = x;
        this.y = y;
        this.gen = gen;
    }
    @Override
    public double fitness(){
        return getX()*6.0-(getX()*getX())+(getY()*4)-(getY()*getY());
    }
    @Override
    public int hashCode(){
        return getX()*31+getY()*17+nGenerations()*7+(int)fitness()*3;
    }
    @Override
    public boolean equals(Object o){
        if(this.getX()==((Solution)(o)).getX()&&this.getX()==((Solution)(o)).getX()&&this.nGenerations()==((Solution)(o)).nGenerations())return true;
        return false;
    }
    public void setX(int newX){
        this.x = newX;
    }
    public void setY(int newY){
        this.y = newY;
    }
    public void incrementGen(){
        gen++;
    }
    
    @Override
    public int getX(){
        return this.x;
    }
    @Override
    public int getY(){
        return this.y;
    }
    @Override
    public int nGenerations(){
        return this.gen;
    } 
  }
  

  

} // public class SimpleEquation