package edu.yu.introtoalgs;
import edu.yu.introtoalgs.CountStringsFJ;

import org.junit.Test;

public class CountStringsTest{
  /*
@Test
    public void basicTest(){
       // int inputSize = 2000;
        int threshold = 250;
        int inputSize = 1048576000*2;
      //for(int p = 0; p<15; p++){
        while(threshold<5000000){
            threshold = threshold*2;
            inputSize = 1048576000*2;
     //   while(inputSize<1000000000){
       while(inputSize>2000){
  //      System.out.println();
        inputSize = inputSize/2;
     //   System.out.println("Input size: "+ inputSize);
        System.out.print(threshold+":@!:"+inputSize+":@!:");
        String[] randWords = {"Hey", "Hey","You", "You", "I", "Don't", "Like", "Your", "Boyfriend", "So"};
        String[] full = new String[inputSize];
        for(int i = 0; i<inputSize; i=i+10){
            for(int j = 0; j<10; j++){
                full[i+j] = randWords[j];
            }  
        }
      /* long startTime = System.currentTimeMillis();
       int counter = 0;
       for(int i = 0; i< inputSize; i++){
        if(full[i].equals("Hey"))counter++;
       }
       long endTime = System.currentTimeMillis(); 
       //System.out.println("Sequential Hey count: "+ counter + "In this amount of milis: " +(endTime-startTime));
       System.out.println((endTime-startTime));*/
  /* long startTime = System.currentTimeMillis();
       CountStringsFJ csf = new CountStringsFJ(full, "Hey", threshold);
       
     int  counter = csf.doIt();
     long  endTime = System.currentTimeMillis(); 
       //System.out.println("Paralel Hey count: "+ counter + "In this amount of milis: " +(endTime-startTime));
       System.out.println((endTime-startTime));
      }
    }
    }
*/

    @Test
    public void lopSidedTest(){
      /*
        System.out.println("Lopsided");
        String[] randWords = {"Hey", "Hey","You", "You", "I", "Don't", "Like", "Your", "Boyfriend", "So"};
        int inputSize = 1000000000/64;
        int threshold = 125000000;
        System.out.println(inputSize);
        String[] full = new String[inputSize];
        for(int i = 0; i<inputSize/2; i=i+10){
            for(int j = 0; j<10; j++){
                full[i+j] = randWords[j];
            }  
        }
        for(int i = inputSize/2; i<inputSize; i++){
            full[i] = "Hey";
        }
       long startTime = System.currentTimeMillis();
       int counter = 0;
       for(int i = 0; i< inputSize; i++){
        if(full[i].equals("Hey"))counter++;
       }
       long endTime = System.currentTimeMillis(); 
       System.out.println("Sequential Hey count: "+ counter + "In this amount of milis: " +(endTime-startTime));
       startTime = System.currentTimeMillis();
       CountStringsFJ csf = new CountStringsFJ(full, "Hey", threshold);
       
       counter = csf.doIt();
       endTime = System.currentTimeMillis(); 
       System.out.println("Paralel Hey count: "+ counter + "In this amount of milis: " +(endTime-startTime));

*/
    }
}