package edu.yu.da;

/** Implements the StockYourBookshelfI API.
 *
 * Students MAY NOT change the provided constructor signature!
 * 
 * @author Avraham Leff
 */

import java.util.*;
public class StockYourBookshelf implements StockYourBookshelfI {
    LinkedList<Integer> totalsToTry;
    List<Integer> solution;
    // No-op constructor
     
    public StockYourBookshelf() {
	// no-op, students may change the implementation
    }

    @Override
    public List<Integer> solution() {
        if (solution == null){
            throw new IllegalStateException();
        }
        return solution;
	    //return Collections.<Integer>emptyList();
    }

    @Override
    public int maxAmountThatCanBeSpent(final int budget, final Map<String, List<Integer>> seforimClassToTypePrices){
       
        Map<String, List<Integer>> sortedMap = new TreeMap<String, List<Integer>>(seforimClassToTypePrices);
        
        
        //first sort the lists
        List<Integer> sortedList;
        for(String category: sortedMap.keySet()){
            sortedList = sortedMap.get(category);
            Collections.sort(sortedList);
            sortedMap.put(category, sortedList);
        }
        //put books into an array
        Object[] arrayOfListOfBooks = new Object[sortedMap.keySet().size()+1];
        int index = 1;
        for(String books: sortedMap.keySet()){
            arrayOfListOfBooks[index] = sortedMap.get(books);
            index++;
        }
        int[][] budgets = new int[sortedMap.keySet().size()+1][budget+1];
       // Object[] previousPaths = new Object[budget+1];
       // Object[] currentPaths = new Object[budget+1];
       ArrayList<ArrayList<Integer>> previousPaths = new ArrayList<ArrayList<Integer>>(budget+1);
       ArrayList<ArrayList<Integer>> currentPaths = new ArrayList<ArrayList<Integer>>(budget+1);
        int minimumPossible = 0;
        ArrayList<Integer> minSolution = new ArrayList<Integer>();
        minSolution.add(0);
        for(int i = 1; i<=sortedMap.keySet().size(); i++){
          //  System.out.print(((List<Integer>)(arrayOfListOfBooks[i])).get(0)+" ");
            minimumPossible = minimumPossible+((List<Integer>)(arrayOfListOfBooks[i])).get(0);
           minSolution.add(((List<Integer>)(arrayOfListOfBooks[i])).get(0));
        }
        for(int i = 0; i<=sortedMap.keySet().size(); i++){
            for(int j = 0; j<=budget; j++){
                budgets[i][j] = 0;
            }
        }
        //
        for(int j = 0; j<=budget; j++){
            budgets[0][j] = minimumPossible;
            previousPaths.add(j, minSolution);
            currentPaths.add(j, minSolution);
        }
     //   System.out.println("cur "+ currentPaths);
        for(int i = 1; i<=sortedMap.keySet().size(); i++){
        //    System.out.println("begin 4 loop");
            for(int p = 0; p<=budget; p++){
              //  System.out.println(currentPaths[p]);
              //  previousPaths.get(p).clear();
              //  System.out.println((List<Integer>)currentPaths[p]);
             //   previousPaths.get(p).addAll(currentPaths.get(p));
           //     System.out.println(previousPaths[p]);
             //   currentPaths.get(p).clear();
            //    System.out.println("end");
                previousPaths.set(p, currentPaths.get(p));
                
            }
           // currentPaths.clear();
          //  currentPaths.ensureCapacity(budget+1);
          //  currentPaths.add(0, new ArrayList<Integer>());
            
      //      System.out.println("prev: "+previousPaths);
       //     System.out.println("cur: "+currentPaths);
         //   System.out.println(previousPaths[budget]);
            List<Integer> addToTotal = new ArrayList<Integer>();
            int cheapestBook = ((List<Integer>)(arrayOfListOfBooks[i])).get(0);
            for(Integer book: (List<Integer>)(arrayOfListOfBooks[i])){
                addToTotal.add(book-cheapestBook);
            }
         //   System.out.println("add To Total "+budget);
            for(int j = 1; j<=budget; j++){
               
                for(Integer totalToAdd: addToTotal){
                    if(j-totalToAdd>=0 && budgets[i-1][j-totalToAdd] == j-totalToAdd){
                  //      System.out.println("hey");
                   //    ((List<Integer>)(currentPaths[j])).clear();
                //   System.out.println("total to add: "+(j-totalToAdd));
                 //  System.out.println(previousPaths);
                   
               //    System.out.println("j is: "+ j+"totalToadd is: "+totalToAdd);
               //    System.out.println("putting: "+ j +" in the "+ i +"th position in: "+(previousPaths.get(j-totalToAdd)));
                        int prevPrev = previousPaths.get(j-totalToAdd).get(i);
                       (previousPaths.get(j-totalToAdd)).set(i, (previousPaths.get(j-totalToAdd).get(i)+totalToAdd));
                        
                       currentPaths.set(j, new ArrayList<Integer>(previousPaths.get(j-totalToAdd)));
                       
                       (previousPaths.get(j-totalToAdd)).set(i, prevPrev);
                   //    System.out.println("now: "+currentPaths.get(j));

                        budgets[i][j] = j;
                        break;
                    }
                    else{

                      // System.out.println("notOptimal");
                        
                        budgets[i][j] = maxOf(budgets[i-1][j], budgets[i][j-1]);
                        if(budgets[i][j] == budgets[i-1][j]){
                 
                          //  currentPaths.get(j).clear();
                         //  currentPaths.get(j).addAll(previousPaths.get(j));
                       //     System.out.println("getting from below "+j+" "+previousPaths.get(j));
                         //   currentPaths.ensureCapacity(budget+1);
                           currentPaths.set(j, previousPaths.get(j));
                        }
                        else{
                          //  currentPaths[j] = (List<Integer>)(currentPaths[j-1]);
                        //  currentPaths.get(j).clear();
                         //  currentPaths.get(j).addAll(currentPaths.get(j-1));
                           currentPaths.set(j, previousPaths.get(j-1));
                        
                        }
                    }
                    
                }   
                
            }
           
            
           
        
        }
    //    System.out.println("about to return: "+currentPaths.get(budget));
    
        solution = new ArrayList<Integer>();
        solution.clear();
       solution.addAll(currentPaths.get(budget));
       solution.remove(0);

        if(budget<minimumPossible){
            solution.clear();
            return Integer.MIN_VALUE;
            
        }
	    return budgets[sortedMap.keySet().size()][budget];
    }
    private int maxOf(int a, int b){
        if(a>=b) return a;
        return b;
    }

} // StockYourBookshelf






/*
public class StockYourBookshelf implements StockYourBookshelfI {
    LinkedList<Integer> totalsToTry;
    List<Integer> solution;
    // No-op constructor
     
    public StockYourBookshelf() {
	// no-op, students may change the implementation
    }

    @Override
    public List<Integer> solution() {
        if (solution == null){
            throw new IllegalStateException();
        }
        return solution;
	    //return Collections.<Integer>emptyList();
    }

    @Override
    public int maxAmountThatCanBeSpent(final int budget, final Map<String, List<Integer>> seforimClassToTypePrices){
       
        Map<String, List<Integer>> sortedMap = new TreeMap<String, List<Integer>>(seforimClassToTypePrices);
        //first sort the lists
        List<Integer> sortedList;
        HashMap<Integer, List<Integer>> everyBudget = new HashMap<Integer, List<Integer>>();
        for(String category: sortedMap.keySet()){
            sortedList = sortedMap.get(category);
            Collections.sort(sortedList);
            sortedMap.put(category, sortedList);
        }
        List<Integer> firstSelection = new LinkedList<Integer>();
        int cheapestBook;
        int totalCost = 0;
        for(String category: sortedMap.keySet()){
            cheapestBook = sortedMap.get(category).get(0);
            firstSelection.add(cheapestBook);
            totalCost = totalCost+cheapestBook;
            
        }
        LinkedList<List<Integer>> scttpv = new LinkedList<List<Integer>>();
        for(String cat: sortedMap.keySet()){
            scttpv.add(sortedMap.get(cat));
        }
        HashSet<Integer> totalsTried = new HashSet<Integer>();
        totalsToTry = new LinkedList<Integer>();
        totalsToTry.add(totalCost);
        everyBudget.put(totalCost, firstSelection);
     //   System.out.println(oneChange(firstSelection, totalCost, scttpv, budget));
        everyBudget.putAll(oneChange(firstSelection, totalCost, scttpv, budget));
        totalsTried.add(totalCost);
  //      totalsToTry.remove(totalCost);
   //     System.out.println(everyBudget);
        while(!totalsToTry.isEmpty()){
           
            
        //    System.out.println("what are we trying "+totalsToTry.get(0));
            if(!totalsTried.contains(totalsToTry.get(0))){
                everyBudget.putAll(oneChange(everyBudget.get(totalsToTry.get(0)), totalsToTry.get(0), scttpv, budget));
                totalsTried.add(totalsToTry.get(0)); 
            }
        //    else{System.out.println("already done");}
            totalsToTry.remove(0);
        }
        int max = 0;
        for(Integer key: everyBudget.keySet()){
            if(key> max) max = key;
        }
        solution = everyBudget.get(max);
        if(max>budget){
            return Integer.MIN_VALUE;
        }
        return max;
	   // return -1;
    }
    private Map<Integer, List<Integer>> oneChange(List<Integer> selection,int totalCost, List<List<Integer>> scttpv, int budget){
        Map<Integer, List<Integer>> mapWithChanges = new HashMap<Integer, List<Integer>> ();
        int i = 0;        
        for(List<Integer> offering: scttpv){
            for(Integer priceOfSwap: offering){
                
                if(priceOfSwap > selection.get(i)){
                    
                 //   int selectionGet = selection.get(i);
                    if((totalCost + priceOfSwap - selection.get(i))<=budget){
                   //     System.out.println("i "+i);
                   //     System.out.println("priceOfSwap "+priceOfSwap);
                   //     System.out.println("selectionget "+selection.get(i));
                   //     System.out.println("old total "+(totalCost));
                        int newTotal = totalCost + priceOfSwap - selection.get(i);
                  //      System.out.println("new total "+(totalCost + priceOfSwap - selection.get(i)));
                        totalsToTry.add(newTotal);
                        List<Integer> newSelection = new LinkedList<Integer>(selection);
                        newSelection.set(i, priceOfSwap);
                        mapWithChanges.put(newTotal, newSelection);
                    //    System.out.println(selection);
                        //selection.set(i, selectionGet);
                        
                    }
                }
            }
            i++;
        }
       
        return mapWithChanges;
    }

} // StockYourBookshelf
*/