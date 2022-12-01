package edu.yu.da;

/** Stubbed implementation of the SimpleEquationI interface.
 *
 * @author Avraham Leff
 */

import static edu.yu.da.DataCompressionI.SolutionI;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.LinkedList;
import java.util.*;
import edu.yu.da.GeneticAlgorithmConfig.SelectionType;

public class DataCompression implements DataCompressionI {

    List<Solution> population;
    double mutationProbability;
    double crossoverProbability;
    Solution bestSolution;
    int generation = 0;
    double threshold;
    final List<String> original;
    /** Constructor.
    *
    * @param original the list whose elements we want to reorder
    * to reduce the
    * number of bytes when compressing the list.
    */
    public DataCompression (final List <String> original) {
        this.original = original;
        bestSolution = new Solution(original, original, 0);
    }
     /** Returns the best Solution found by a genetic algorithm for the simple
     * equation specified by the requirements document.
     *
     * @param gac contains properties needed by a genetic algorithm
     * @see GeneticAlgorithmConfig
     */
    public SolutionI solveIt(GeneticAlgorithmConfig gac){
        threshold = gac.getThreshold();
        mutationProbability = gac.getMutationProbability();
        crossoverProbability = gac.getCrossoverProbability();
        //define the population
        population = new LinkedList<Solution>();
        //populate the population
        List<String> shuffledList = new LinkedList<String>();
        shuffledList.addAll(original);
        for(int i = 0; i<gac.getInitialPopulationSize(); i++){
            Collections.shuffle(shuffledList);
           // System.out.println("shuffledList: "+ shuffledList);
            population.add(new Solution(original, shuffledList, 0));
        }
        for(; generation< gac.getMaxGenerations(); generation++){
     //      System.out.println("Gen: "+ generation);
     //      System.out.println("Best We Got: "+ bestSolution.fitness());
            //evolve
            if(gac.getSelectionType() == SelectionType.TOURNAMENT){
                tournament();
            }
            else{
                roulette();
            }
            if(bestSolution.relativeImprovement()>= gac.getThreshold()){
                return bestSolution;
            }
        }
      return bestSolution;
    }
    private void roulette(){
        //assign lottery tickets based on fittness
        double totalFit = 0;
        List<Solution> matingPool = new LinkedList<Solution>();
        //take the percent multiply by the population size*5 and choose randomly the size of the population
        //this solution unfortunetly gives greater fitness to -1 than 0
        for(Solution s: population){
            if(s.fitness()>bestSolution.fitness()){
        //     System.out.println("new best solution found in gen "+s.nGenerations()+ "fitness "+s.fitness());
                bestSolution = s;
            }
         //   System.out.println("calcalating total fit");
                //every point more than 1000 gets 20 more raffle tickets
             //   totalFit = totalFit+s.fitness()+(s.fitness()%1000)*20;
            
        }
        List<Solution> matingPoolToChooseFrom = new LinkedList<Solution>();
       // System.out.println("about to add solutions");
        for(Solution s: population){
            int bonusTickets = 0;
            if(s.fitness()> 1000) bonusTickets = 10 + (s.fitness()-1000)/10;
            else bonusTickets = s.fitness()/100;
            for(int i = 0; i<bonusTickets; i++){
                matingPoolToChooseFrom.add(s);
            }
           
        }
      //  System.out.println("finished adding solutions");
        for(int i = 0; i< population.size(); i++){
            matingPool.add(matingPoolToChooseFrom.get((int)Math.floor(Math.random()*matingPoolToChooseFrom.size())));
        }
        
        int index = 0;  
        List<Solution> newGen = new LinkedList<Solution>();
        while(index<matingPool.size()/2){  
          //  System.out.println("Stuck with you");
            newGen.addAll(mate(matingPool.get(index), matingPool.get(matingPool.size()-index-1)));
        //   matingPool.remove(index);
        //   matingPool.remove(index+1);
            index++;
        }
        population = newGen;

   
  }
    
    private void tournament(){
        List<Solution> axlList = new LinkedList<Solution>();
        List<Solution> matingPool = new LinkedList<Solution>();
        axlList.addAll(population);  
        for(Solution s: population){      
            Solution randSolution = axlList.get((int)Math.floor(axlList.size()*Math.random()));
            if(s.fitness()>bestSolution.fitness()){
            //    System.out.println("new best solution found in gen "+s.nGenerations()+ "fitness "+s.fitness());
                bestSolution = s;
            }
            if(s.fitness()>= randSolution.fitness()){
           //     System.out.println("fit comp: "+ s.fitness()+ ", "+ randSolution.fitness());
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
    private LinkedList<LinkedList<String>>crossover(Solution male, Solution female){
        int listSize = male.getList().size();
        HashMap<Integer, String> maleStaticMap = new HashMap<Integer, String>();
        HashMap<Integer, String> femaleStaticMap = new HashMap<Integer, String>();
        HashMap<String, Integer> maleStringToOcur = new HashMap<String, Integer>();
        HashMap<String, Integer> femaleStringToOcur = new HashMap<String, Integer>();
        for(int i = 0; i<listSize; i++){
            if(i%2 == 0){
                maleStaticMap.put(i, male.getList().get(i));
                maleStringToOcur.putIfAbsent(male.getList().get(i), 0);
                int numOcur = maleStringToOcur.get(male.getList().get(i));
                maleStringToOcur.put(male.getList().get(i), numOcur+1);

                String femaleString = female.getList().get(i);
                femaleStaticMap.put(i, femaleString);
                femaleStringToOcur.putIfAbsent(femaleString, 0);
                int numFOcur = femaleStringToOcur.get(femaleString);
                femaleStringToOcur.put(femaleString, numFOcur+1);

            }
            else {
                maleStringToOcur.putIfAbsent(male.getList().get(i), 0);
                femaleStringToOcur.putIfAbsent(female.getList().get(i), 0);
            }
        }
        int femaleListPointer = 0;
        for(int i = 0; i<listSize; i++){
            while(maleStaticMap.get(i)==null){//open space fill it in with a female that is not yet in male
                String fString = female.getList().get(femaleListPointer);
                int ocurs = maleStringToOcur.get(fString);
                if(ocurs<1){
                    maleStaticMap.put(i, fString); 
                }
                else{
                    maleStringToOcur.put(fString, ocurs-1);
                }
                femaleListPointer++;  
            }
               
        }

        int maleListPointer = 0;
        for(int i = 0; i<listSize; i++){
            while(femaleStaticMap.get(i)==null){//open space fill it in with a female that is not yet in male
                String mString = male.getList().get(maleListPointer);
                int ocurs = femaleStringToOcur.get(mString);
                if(ocurs<1){
                    femaleStaticMap.put(i, mString); 
                }
                else{
                    femaleStringToOcur.put(mString, ocurs-1);
                }
                maleListPointer++;  
            }
               
        }
        LinkedList<LinkedList<String>> listOfLists = new LinkedList<LinkedList<String>>();
        LinkedList<String> newListm = new LinkedList<String>();
        LinkedList<String> newListf = new LinkedList<String>();

        for(int i = 0; i<listSize; i++){
            newListm.add(maleStaticMap.get(i));
            newListf.add(femaleStaticMap.get(i));

        }
        listOfLists.add(newListm);
        listOfLists.add(newListf);
        return listOfLists;


    }

    private List<Solution> mate(Solution male, Solution female){
        List<Solution> newGen = new LinkedList<Solution>();
        List<String> finalCombinedListMale = new LinkedList<String>();
        List<String> finalCombinedListFemale = new LinkedList<String>();
        int arraySize = male.getList().size();
        String[] combinedListMale = new String[arraySize];
        String[] combinedListFemale = new String[arraySize];
        if(Math.random()<crossoverProbability){//aka crossover
         LinkedList<LinkedList<String>> lol =  crossover(male, female);
         /*
            HashMap<String, Integer> setByMale = new HashMap<String, Integer>();
            int amountOfString;
            for(int i = 0; i < arraySize; i = i+2){
                combinedListMale[i] = male.getList().get(i);
                
                if(setByMale.get(combinedListMale[i]) == null){
                    amountOfString = 0;
                }
                else amountOfString = setByMale.get(combinedListMale[i]);
                amountOfString++;
                setByMale.put(combinedListMale[i], amountOfString);
            }
            int femaleIndex = 0;
            for(int i = 1; i< arraySize; i = i+2){
                while(femaleIndex < arraySize-1 && (setByMale.get(female.getList().get(femaleIndex))== null || setByMale.get(female.getList().get(femaleIndex)) < 1)){
                    femaleIndex++;
                }
                combinedListMale[i] = female.getList().get(femaleIndex);
                int count;
                if(setByMale.get(combinedListMale[i]) == null){
                    count = 0;
                }
                else count = setByMale.get(combinedListMale[i]);
                setByMale.put(combinedListMale[i], --count);
                if(femaleIndex < arraySize-1) femaleIndex++;
            }
            
            HashMap<String, Integer> setByFemale = new HashMap<String, Integer>();
            for(int i = 0; i < arraySize; i = i+2){
               // combinedListFemale[i] = female.getList().get(i);
              //  setByFemale.add(combinedListFemale[i]);

                combinedListFemale[i] = female.getList().get(i);
                
                if(setByFemale.get(combinedListFemale[i]) == null){
                    amountOfString = 0;
                }
                else amountOfString = setByFemale.get(combinedListFemale[i]);
                amountOfString++;
                setByFemale.put(combinedListFemale[i], amountOfString);
            }
            int maleIndex = 0;
            for(int i = 1; i< arraySize; i = i+2){
              
                while(maleIndex < arraySize-1 && (setByFemale.get(male.getList().get(maleIndex))== null || setByFemale.get(male.getList().get(maleIndex)) < 1)){
                    maleIndex++;
                }
                combinedListFemale[i] = male.getList().get(maleIndex);
                int count;
                if(setByFemale.get(combinedListFemale[i]) == null){
                    count = 0;
                }
                else count = setByFemale.get(combinedListFemale[i]);
                setByFemale.put(combinedListFemale[i], --count);
                if(maleIndex < arraySize-1) maleIndex++;
            }*/
            for(int i = 0; i< lol.get(0).size(); i++){
                combinedListMale[i] = lol.get(0).get(i);
                combinedListFemale[i] = lol.get(1).get(i);
            }
                 
            if(Math.random()<mutationProbability){
                int numToSwap = (int)Math.floor(Math.random()*arraySize)/2;
                for(int i = 0; i<numToSwap; i++){
                    int randOne = (int)Math.floor(Math.random()*arraySize);
                    int randTwo = (int)Math.floor(Math.random()*arraySize);
                    String axOne;
                    String axTwo;
                    axOne = combinedListFemale[randOne];
                    axTwo = combinedListFemale[randTwo];
                    combinedListFemale[randOne] = axTwo;
                    combinedListFemale[randTwo] = axOne;
                    
                }
            }
            if(Math.random()<mutationProbability){
                int numToSwap = (int)Math.floor(Math.random()*arraySize)/2;
                for(int i = 0; i<numToSwap; i++){
                    int randOne = (int)Math.floor(Math.random()*arraySize);
                    int randTwo = (int)Math.floor(Math.random()*arraySize);
                    String axOne;
                    String axTwo;
                    axOne = combinedListMale[randOne];
                    axTwo = combinedListMale[randTwo];
                    combinedListMale[randOne] = axTwo;
                    combinedListMale[randTwo] = axOne;
                    
                }
            }
            for(int i = 0; i<combinedListMale.length; i++){
                finalCombinedListMale.add(combinedListMale[i]);
            }
       //     System.out.println("maleList: "+ male.getList());
         //   System.out.println("femaleList: "+ female.getList());
           // System.out.println("Comined list: "+finalCombinedListMale);
            newGen.add(new Solution(original, finalCombinedListMale, generation));

            for(int i = 0; i<combinedListFemale.length; i++){
                finalCombinedListFemale.add(combinedListFemale[i]);
            }
            newGen.add(new Solution(original, finalCombinedListFemale, generation));
        }
        else{//aka no crossover
                for(int i = 0; i< arraySize; i++){
                    combinedListMale[i] = male.getList().get(i);
                    combinedListFemale[i] = female.getList().get(i);
                }
              
                if(Math.random()<mutationProbability){
                    int numToSwap = (int)Math.floor(Math.random()*arraySize)/2;
                    for(int i = 0; i<numToSwap; i++){
                        int randOne = (int)Math.floor(Math.random()*arraySize);
                        int randTwo = (int)Math.floor(Math.random()*arraySize);
                        String axOne;
                        String axTwo;
                        axOne = combinedListFemale[randOne];
                        axTwo = combinedListFemale[randTwo];
                        combinedListFemale[randOne] = axTwo;
                        combinedListFemale[randTwo] = axOne;
                        
                    }
                }
                if(Math.random()<mutationProbability){
                    int numToSwap = (int)Math.floor(Math.random()*arraySize)/2;
                    for(int i = 0; i<numToSwap; i++){
                        int randOne = (int)Math.floor(Math.random()*arraySize);
                        int randTwo = (int)Math.floor(Math.random()*arraySize);
                        String axOne;
                        String axTwo;
                        axOne = combinedListMale[randOne];
                        axTwo =  combinedListMale[randTwo];
                        combinedListMale[randOne] = axTwo;
                        combinedListMale[randTwo] = axOne;
                        
                    }
                }
          
                for(int i = 0; i<combinedListMale.length; i++){
                    finalCombinedListMale.add(combinedListMale[i]);
                }
                newGen.add(new Solution(original, finalCombinedListMale, generation));
    
                for(int i = 0; i<combinedListFemale.length; i++){
                    finalCombinedListFemale.add(combinedListFemale[i]);
                }
                newGen.add(new Solution(original, finalCombinedListFemale, generation));
        }
        return newGen;
    }

     /** Return the number of bytes when applying compression to the original
     * list.
     *
     * @return number of bytes
     */
    public int nCompressedBytesInOriginalList(){
        return bytesCompressed(original);
    }
    public static int bytesCompressed(final List<String> list) {
        return DataCompressionI.bytesCompressed(list);
    }

    public class Solution implements SolutionI{
        List<String> list = new LinkedList<String>();
        List<String> ogList;
        int gen;
       // HashMap<String, Integer> ogPosition = new HashMap<String, Integer>(); 
     //   HashMap<String, Integer> listPosition = new HashMap<String, Integer>(); 
        public Solution(List<String> og, List<String> newList, int gen){
            
            this.ogList = og;
            this.list = newList;
            this.gen = gen;
        //    System.out.println("og: "+ this.ogList + " new: " + this.list);
         /*   int ogIndex = 0;
            for(String ogString: og){
                ogPosition.put(ogString, ogIndex);
                ogIndex++;
            }
            int newIndex = 0;
            for(String newString: list){
                listPosition.put(newString, newIndex);
                newIndex++;
            }*/

        }
      /*  public int getIndex(String string){
            return listPosition.get(string);
        }
        public int getOgIndex(String string){
            return ogPosition.get(string);
        }
        public void setList(List<String> changedList){
            list = changedList;
            int newIndex = 0;
            for(String newString: list){
                listPosition.put(newString, newIndex);
                newIndex++;
            }

        }*/
        /** Returns the list associated with this solution: the elements are
         * identical to the original list, but may be ordered differently to
         * require fewer bytes when compressed.
         *
         * @return the solution's List.
         */
        @Override
        public List<String> getList(){
            return list;
        }
        @Override
        public boolean equals(Object o){
            if(this.getList()==((Solution)(o)).getList()&&this.nGenerations()==((Solution)(o)).nGenerations())return true;
            return false;
        }
        @Override
        public int hashCode(){
            int hash = 0;
            int i = 1;
            for(String string: list){
                hash = hash+(string.hashCode()*i);
            }
            hash = hash+31*gen;
            return hash;
        }

        /** Returns the original List.
         *
         * @return the original List.
         */
        @Override
        public List<String> getOriginalList(){
            return ogList;
        }

        /** Returns the ratio of the compressed number of bytes associated with the
         * original list (numerator) to the solution's compressed number of bytes
         * (denominator).
         *
         */
        @Override
        public double relativeImprovement(){
            return (double)((double)(bytesCompressed(ogList)/(double)(bytesCompressed(this.list))));
        }
        public int fitness(){
           return (int)(1000*this.relativeImprovement()); 
        }
        /** Returns the number of generations required to generate the solution.
         *
         * @return number of generations required.
         */
        @Override
        public int nGenerations(){
            return gen;
        }
        public void incrementGen(){
            gen++;
        }

    } // inner SolutionI interface
    
}