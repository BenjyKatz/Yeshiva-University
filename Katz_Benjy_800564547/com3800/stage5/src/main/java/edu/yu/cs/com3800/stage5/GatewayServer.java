package edu.yu.cs.com3800.stage5;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;



import com.sun.net.httpserver.*;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.net.InetSocketAddress;
import java.net.Socket;

public class GatewayServer{

    int port;

    private final HttpServer server;
    GatewayPeerServerImpl gatewayPeerServerImpl;

    public GatewayServer(int port, GatewayPeerServerImpl gatewayPeerServerImpl) throws IOException {
        this.port = port;

        // Create and configure the HttpServer
        this.server =  HttpServer.create(new InetSocketAddress(port), 0);
        this.server.setExecutor(Executors.newFixedThreadPool(5));
        this.gatewayPeerServerImpl = gatewayPeerServerImpl;
        // set up the routes to the broker service
        this.server.createContext("/compileandrun", new compileAndRun(this.gatewayPeerServerImpl));

        

    }
    
    /**
     * Starts the HttpServer
     */
    public void start() {
        this.server.start();
    }

    /**
     * Stops the HttpServer
     *
     * @param delay the maximum time in seconds to wait until exchanges have finished
     * @throws IllegalArgumentException if delay is less than zero
     */
    public void stop(int delay) {
        this.server.stop(delay);
    }


    // Handler for the "/compileandrun" context
    class compileAndRun implements HttpHandler {
        BlockingQueue<HttpExchange> failedRequests = new LinkedBlockingQueue<>();
        GatewayPeerServerImpl gatewayPeerServerImpl;
        public compileAndRun(GatewayPeerServerImpl gatewayPeerServerImpl){
            this.gatewayPeerServerImpl = gatewayPeerServerImpl;
        
            new Thread(() -> {
                while (true) {
                    try {
                        HttpExchange failedRequest = failedRequests.take(); // Blocks until a failed request is available
                        System.out.println("Retrying failed request...");
                        handle(failedRequest);
                        Thread.sleep(200);
                    } catch (IOException|InterruptedException e) {
                        e.printStackTrace();
                        //Thread.currentThread().interrupt();
                        break;
                    }
                }
            });
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
           // System.out.println("I am handling the request in gatewayServer");
            InputStream is = exchange.getRequestBody();
            //JavaRunner jr = new JavaRunner();
            

            Headers headerMap = exchange.getRequestHeaders();
           // System.out.println(headerMap.get("Content-type"));
            
            if(!headerMap.get("Content-type").contains("text/x-java-source")){
                exchange.sendResponseHeaders(400, 0);
            }

            try{
                //String result = jr.compileAndRun(is);
                
                //result must = the is(input stream) submitted via tcp to the master/leader
                long leaderId = this.gatewayPeerServerImpl.getCurrentLeader().getProposedLeaderID();
                //System.out.println(leaderId);
                InetSocketAddress leaderSocketAddress = gatewayPeerServerImpl.getPeerByID(leaderId);
                //send it the work
                Socket socket = new Socket();
                
                //I think this blocks instead of throwing exception
                //System.out.println("GS trying to connect");
                socket.connect(leaderSocketAddress);
                //System.out.println("GS connected");
                OutputStream socketOutputStream = socket.getOutputStream();
               // System.out.println("we are connected");

                byte[] buffer = new byte[1024];
                int bytesRead;

                // Read from the InputStream and write to the socket's OutputStream
                while ((bytesRead = is.read(buffer)) != -1) {
                    socketOutputStream.write(buffer, 0, bytesRead);
                    socketOutputStream.write("\n$$$\n".getBytes());
                }
                //socketOutputStream.close();

                // Close the InputStream and the socket
                //is.close();
               // System.out.println("socket info: "+socket);
                BufferedReader serverResponseReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                //socketOutputStream.close();
                is.close();
                //System.out.println("GS made past close");
                String serverResponse;
                String totalResponse = "";
                
                
                while ((serverResponse = serverResponseReader.readLine()) != null) {
                    // Receive and display the server's response
                    if(serverResponse.contains("$$$")){
                        break;
                    }
                    totalResponse = totalResponse + serverResponse+"\n";
                   // System.out.println("GS Server says: " + serverResponse);
                }
                //System.out.println("GS response received: "+totalResponse);
                socket.close();
                
                //here it sends the work to the master/leader
                                
                exchange.sendResponseHeaders(200, totalResponse.length());
                OutputStream os = exchange.getResponseBody();
                os.write(totalResponse.getBytes());
                os.close();
                
            }

            catch (Exception e){
                System.out.println("GS server error:");
                e.printStackTrace();
                failedRequests.add(exchange);
                /*
                e.printStackTrace();
                ByteArrayOutputStream outputStreamForPrint = new ByteArrayOutputStream();
                PrintStream s = new PrintStream(outputStreamForPrint);
                e.printStackTrace(s);
                String stackTrace = outputStreamForPrint.toString();
                String errorMessage = e.getMessage()+"/n"+stackTrace;
                exchange.sendResponseHeaders(400, errorMessage.length());
                OutputStream os = exchange.getResponseBody();
                os.write(errorMessage.getBytes());
                os.close();
                System.out.println("GS End of handle");
                */
            }  
        }
    }
    
    


    
    
}