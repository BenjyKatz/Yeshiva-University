import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.net.*;


public class JellyBeanServer {


    public static void main(String[] args){
        ExecutorService executor = Executors.newFixedThreadPool(2);
        executor.submit(new EventProcesor(9990, 0));
        executor.submit(new EventProcesor(9991, 1));
    }
    
}
