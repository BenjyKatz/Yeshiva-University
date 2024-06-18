package edu.yu.cs.com3800.stage5;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.io.FileWriter;   // Import the FileWriter class
import java.io.IOException;

public class Gossip{
    int myPort;
    long myHeartBeatCount;
    HashSet<Integer> failNodes;
    //int time;
    static final int GOSSIP = 3000; 
    static final int FAIL = GOSSIP * 10; 
    static final int CLEANUP = FAIL * 2;
    FileWriter myWriter;
    ConcurrentHashMap<Integer, long[]> table;// = new ConcurrentHashMap<Integer, ArrayList<Integer>>();
    public Gossip(int myPort, ConcurrentHashMap<Integer, long[]> table){
        this.myPort = myPort;
        this.table = table;
        this.myHeartBeatCount = this.table.get(myPort)[0];
        this.failNodes = new HashSet<Integer>();
        
        try{
            this.myWriter = new FileWriter(myPort+"_gossip.txt");
        }
        catch(IOException e){
            //
        }
       
    }
    public ConcurrentHashMap<Integer, long[]> getTable(){
        return this.table;
    }
    public void updateTable(ConcurrentHashMap<Integer, long[]> otherTable){
        //ConcurrentHashMap<Integer, long[]> otherTable = otherGossip.getTable();
        //System.out.println("Gossip this table: "+ this.table.toString());
        //System.out.println("Gossip other table: "+ otherTable.toString());
        try{
            this.myWriter.write("\n");
            for (Map.Entry<Integer, long[]> entry : this.table.entrySet()) {
                
                this.myWriter.write(entry.getKey()+"\n");
                for(Long v: entry.getValue()){
                    this.myWriter.write(" "+v+" ");
                }
                this.myWriter.write("\n\n\n");
            }
            this.myWriter.close();
        }
        catch(IOException e){
            //problem writing to file
        }
        
        

        for (Map.Entry<Integer, long[]> entry : otherTable.entrySet()) {
            Integer port = entry.getKey();
            
            long[] otherValues = entry.getValue();
            
            long otherHeartBeatCount = otherValues[0];
            long otherTime = otherValues[1];
            long otherIsFailed = otherValues[2];


            long[] values = this.table.get(port);
            //if there is no macthing entry
            if(values == null){
                this.table.put(entry.getKey(), entry.getValue());
                continue;
            }
            
            long heartBeatCount = values[0];
            long time = values[1];
            long isFailed = values[2];

            long currentTime = System.currentTimeMillis();
            long failNode = isFailed;
            if(currentTime-otherTime>FAIL||otherIsFailed == 1){
                //System.out.println("failing a node on port: "+port);
                failNode=1;
                failNodes.add(port);
            }
            long[] newValues = {heartBeatCount, time, failNode};
            if(otherHeartBeatCount>heartBeatCount){
                newValues[0] = otherHeartBeatCount;
                newValues[1] = System.currentTimeMillis();
                newValues[2] = failNode;
            }
            this.table.put(port, newValues);

        }

    }
    public void beat(){
        this.myHeartBeatCount++; 
        this.table.get(myPort)[0] = this.myHeartBeatCount;
        this.table.get(myPort)[1] = System.currentTimeMillis();
    }
    public HashSet<Integer> getFailedNodes(){
        return this.failNodes;
    }


}