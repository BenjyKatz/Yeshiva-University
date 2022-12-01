package edu.yu.introtoalgs;
import edu.yu.introtoalgs.*;
//import edu.yu.introtoalgs.SecreteAlgorithm1;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.PrintWriter;
import java.io.IOException;


public class EstimateSecretAlgorithmsClient2{
    public static boolean stopRunningAlgs1 = false;
    public static void main(String[] args) throws IOException{
        /*try{
            FileWriter myWriter = new FileWriter("ResultOutput.txt");
        }
        catch (IOException e) {
            System.out.println("An error occurred.");
        }*/
        String resultsOfN;
        int i = 200;
        //try{
            
           // BufferedWriter writer = new BufferedWriter(new FileWriter("ResultsOutput.txt", true));
         //   PrintWriter printIt = new PrintWriter(writer);
        //}
     //   catch (IOException e) {
       //     System.out.println("An error occurred.");
     //   }
        while(i< 500000000){
            resultsOfN = test(i);
            try{
                FileWriter myWriter = new FileWriter("ResultsOutput.txt", true);
                System.out.println("writing input: " +i+" to file");
                //printIt.print(resultsOfN);
                myWriter.write(resultsOfN);
                myWriter.close();
                System.out.println("wrote to file");
            }
            catch (IOException e) {
                System.out.println("An error occurred.");
            }
            resultsOfN = "";
            i=i*2;
        }
       


    }
    private static String test(int n){
        BigOMeasurable sa1 = new SecretAlgorithm1();
        BigOMeasurable sa2 = new SecretAlgorithm2();
        BigOMeasurable sa3 = new SecretAlgorithm3();
        BigOMeasurable sa4 = new SecretAlgorithm4();
        sa1.setup(n);
        sa2.setup(n);
        sa3.setup(n);
        sa4.setup(n);
        String results = "";
        long startTime = System.currentTimeMillis();
        
       //     sa1.execute();
        
        long endTime = System.currentTimeMillis();
        
        results = results + ("Algs1 with an input of "+n+ " was completes in " + (endTime-startTime) +" milis \n");

        startTime = System.currentTimeMillis();
        sa2.execute();
        endTime = System.currentTimeMillis();
        results = results + ("Algs2 with an input of "+n+ " was completes in " + (endTime-startTime) +" milis \n");

        startTime = System.currentTimeMillis();
        sa3.execute();
        endTime = System.currentTimeMillis();
        results = results + ("Algs3 with an input of "+n+ " was completes in " + (endTime-startTime) +" milis \n");

        startTime = System.nanoTime();
        sa4.execute();
        endTime = System.nanoTime();
        results = results + ("Algs4 with an input of "+n+ " was completes in " + (endTime-startTime) +" nanos \n"+"\n"+"\n"+"\n");
        return results;

    }

}
