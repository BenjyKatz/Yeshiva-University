package edu.yu.cs.com3800.stage1;
import java.io.IOException;
import java.net.InetSocketAddress;


import edu.yu.cs.com3800.JavaRunner;
import edu.yu.cs.com3800.SimpleServer;
import com.sun.net.httpserver.*;
import java.io.*;

public class SimpleServerImpl implements SimpleServer{
    class MyHandler implements HttpHandler {
        public void handle(HttpExchange t) throws IOException {
            InputStream is = t.getRequestBody();
            JavaRunner jr = new JavaRunner();
            Headers headerMap = t.getRequestHeaders();
            System.out.println(headerMap.get("Content-type"));
            if(!headerMap.get("Content-type").contains("text/x-java-source")){
                t.sendResponseHeaders(400, 0);
            }

            
            

            try{
                String result = jr.compileAndRun(is);
                t.sendResponseHeaders(200, result.length());
                OutputStream os = t.getResponseBody();
                os.write(result.getBytes());
                os.close();
            }
            catch (Exception e){
                System.out.println("server error:");
                ByteArrayOutputStream outputStreamForPrint = new ByteArrayOutputStream();
                PrintStream s = new PrintStream(outputStreamForPrint);
                e.printStackTrace(s);
                String stackTrace = outputStreamForPrint.toString();
                String errorMessage = e.getMessage()+"/n"+stackTrace;
                t.sendResponseHeaders(400, errorMessage.length());
                OutputStream os = t.getResponseBody();
                os.write(errorMessage.getBytes());
                os.close();
                System.out.println("End of handle");
            }  
        }
    }
    int port;
    public SimpleServerImpl(int port) throws IOException{
        this.port = port;
    }
    HttpServer server;
    public void start(){
        
        try{
            System.out.println("running server");
            //server = HttpServer.create(new InetSocketAddress("localhost",port), 0);
            server = HttpServer.create(new InetSocketAddress(port), 0);
        }
        catch (IOException e){
            System.out.println("unable to start server");
            return;
        }
        server.createContext("/compileandrun", new MyHandler());
        server.setExecutor(null); // creates a default executor
        server.start();
    }
    public void stop(){
        server.stop(0);
    }

    public static void main(String[] args){
        int port = 9000;
        if(args.length >0){
            port = Integer.parseInt(args[0]);
        }
        SimpleServer myserver = null; 
        try{
            System.out.println("running server");
            myserver = new SimpleServerImpl(port); 
            myserver.start();
        }
        catch(Exception e){
            System.err.println(e.getMessage());
            myserver.stop();
        }
    }
}