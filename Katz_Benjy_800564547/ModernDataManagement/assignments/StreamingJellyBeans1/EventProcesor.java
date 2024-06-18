import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.net.*;
import java.io.*;

public class EventProcesor implements Runnable{
    public static LinkedHashMap<String, Integer> bop = new LinkedHashMap<String, Integer>();
    public static LinkedHashMap<String, Integer> bgr = new LinkedHashMap<String, Integer>();
    public static LinkedHashSet<String> bopSet = new LinkedHashSet<String>();
    public static LinkedHashSet<String> bgrSet = new LinkedHashSet<String>();

    BufferedReader in;
    int port;
    int inst;
    public EventProcesor(int port, int inst){
        System.out.println("creating ep");
        bopSet.add("blue");
        bopSet.add("orange");
        bopSet.add("purple");
        bopSet.add("yellow");

        bgrSet.add("black");
        bgrSet.add("green");
        bgrSet.add("red");
        bgrSet.add("white");

        this.port = port;
        this.inst = inst;
        try{
            ServerSocket serverSocket = new ServerSocket(port);
            Socket clientSocket = serverSocket.accept();
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        }
        catch(Exception e){
            System.out.println("Error with Socket");
        }
        
    }
    @Override
    public void run(){
        System.out.println("Starting run");
        String event;
        try{
            while((event = in.readLine()) != null){
                System.out.println("SensorReader :: instance "+inst+" --> "+event);
                process(event);
            }
        }
        catch(Exception e){
            System.out.println("error while listening");
        }
    }


    public static void process(String event){
        synchronized(bop){
            if(bopSet.contains(event)){
                Integer count = bop.get(event);
                if(count == null) count = 0;
                bop.put(event, count+1);
                printMethod(bop, 0);
            }
        }

        synchronized(bgr){
            if(bgrSet.contains(event)){
                Integer count = bgr.get(event);
                if(count == null) count = 0;
                bgr.put(event, count+1);
                printMethod(bgr, 1);
            }
        }
    } 
    public static void printMethod(HashMap<String, Integer> hash, int inst){
        System.out.println("JellyBeanCounter :: instance "+inst+" -->");
        if(inst == 0){
            for(String key:bopSet){
                if(hash.get(key)!=null){
                    System.out.println("  "+key+": "+hash.get(key));
                }
            }
        }
        if(inst == 1){
            for(String key:bgrSet){
                if(hash.get(key)!=null){
                    System.out.println("  "+key+": "+hash.get(key));
                }
            }
        }
        /*
        for(String key:hash.keySet()){
            System.out.println(key+": "+hash.get(key));
        }*/
        System.out.println();
    }   
}