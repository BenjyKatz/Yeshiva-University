package edu.yu.cs.com3800.stage5;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Logger;

import edu.yu.cs.com3800.LoggingServer;
import edu.yu.cs.com3800.Message;
//import edu.yu.cs.com3800.UDPMessageReceiver;
//import edu.yu.cs.com3800.UDPMessageSender;
import edu.yu.cs.com3800.Util;
import edu.yu.cs.com3800.ZooKeeperPeerServer;
import edu.yu.cs.com3800.Message.MessageType;

public class RoundRobinLeader extends Thread implements LoggingServer{
    private final InetSocketAddress myAddress;

    //private volatile boolean shutdown;
    private LinkedBlockingQueue<Message> outgoingMessages;
    private LinkedBlockingQueue<Message> incomingMessages;
    private boolean shutdown;
    private Map<Long,InetSocketAddress> peerIDtoAddress;

    private Map<Long, InetSocketAddress> requestIDtoClient;
    private long requestID;

    private ZooKeeperPeerServer zooKeeperServer;
    private int myPort;
    private LinkedList<InetSocketAddress> workersAddress;
    private LinkedBlockingQueue<Message> workQueue;
    private LinkedBlockingQueue<Message> completedWork;

    private Logger logger;
    private HashSet<Integer> deadPorts;
    

    
    public RoundRobinLeader(LinkedBlockingQueue<Message> outgoingMessages, LinkedBlockingQueue<Message> incomingMessages, int myPort, ZooKeeperPeerServer zooKeeperServer, InetSocketAddress myAddress, Map<Long,InetSocketAddress> peerIDtoAddress){
        
        setDaemon(true);
        this.shutdown = false;
        this.myAddress = myAddress;
        this.myPort = myPort;
        this.peerIDtoAddress = peerIDtoAddress;
        this.zooKeeperServer = zooKeeperServer;
 
        this.outgoingMessages = outgoingMessages;
        this.incomingMessages = incomingMessages;
        this.workersAddress = new LinkedList<InetSocketAddress>();
        this.workersAddress.addAll(peerIDtoAddress.values());
        this.workersAddress.remove(this.myAddress);
        this.requestID = 0;
        this.requestIDtoClient = new HashMap<Long, InetSocketAddress>();
        this.workQueue = new LinkedBlockingQueue<Message>();
        this.completedWork = new LinkedBlockingQueue<Message>();
        this.deadPorts = new HashSet<Integer>();
      
        this.workersAddress.remove(this.peerIDtoAddress.remove(((ZooKeeperPeerServerImpl)(this.zooKeeperServer)).getGateWayId()));
        try{
            this.logger = initializeLogging("RoundRobin" + "-on-port-" + this.myPort);
        }
        catch(IOException e){
            System.out.println("logger wont initialize");
        }
        this.logger.info("in the constructor");
    }
    
    public void shutdown(){
        this.shutdown = true;
    }
    public void alertDeadWorkers(HashSet<Integer> deadPorts){
        if(deadPorts == null) return;
        this.deadPorts.addAll(deadPorts);
        for(Integer port: deadPorts){
            //get the work that was supposed to be done and add it back to the queue
            //remove these workers from the worker queue
            /*
            System.out.println(this.workersAddress);
            System.out.println("Equals");
            System.out.println(this.workersAddress.contains(new InetSocketAddress("localhost",port)));
            System.out.println("New port");
            System.out.println(new InetSocketAddress("localhost",port));
            */
            this.workersAddress.remove(new InetSocketAddress("localhost",port));


        }
        //System.out.println("realocate the work");

    }
    public void run(){
        this.logger.info("The leader round robin has started");
        this.logger.info("RR map "+this.workersAddress);
        this.logger.info("RR my port "+this.myPort);

        
        try{
            //allow time for followers to assume role before assigning tasks
            sleep(500);
        }
        catch(Exception e){
        } 
        try{

           // System.out.println("port with a problem: "+this.myPort);
            ServerSocket serverSocket = new ServerSocket(this.myPort);
            Thread getRequests = new Thread(() -> {
                try{
                    while (!this.shutdown) {
                        Socket clientSocket = serverSocket.accept();
                        this.logger.info("In Round Robin, Accepted connection from " + clientSocket.getInetAddress());

                        // Handle client communication in a new thread
                        new Thread(() -> handleClient(clientSocket)).start();
                    }
                    serverSocket.close();
                }
                catch(Exception e){
                    this.logger.info("RR problem in getRequests");
                    e.printStackTrace();
                }
            });
            getRequests.start();

        }
        catch(Exception e){
            this.logger.info("problem in round robin handler");
            e.printStackTrace();

        }
        while(!this.shutdown){
            
            


            //this.logger.info("running leader");
            Message incoming = this.workQueue.peek();
            
                if(!(incoming == null) && incoming.getMessageType() == Message.MessageType.WORK){
                    
                    Message workMessage = this.workQueue.poll();
                    InetSocketAddress nextWorker = this.workersAddress.remove();

                    Message message = new Message(Message.MessageType.WORK, workMessage.getMessageContents(), this.myAddress.getHostString(), this.myPort, nextWorker.getHostString(), nextWorker.getPort(), this.requestID);     
                    //this.outgoingMessages.offer(message);
                    //sendWork(message, nextWorker);
                    
                    this.logger.info("sending from round robin to worker");
                    Thread sendRequestThread = new Thread(()->{
                        try{
                            Socket workerSocket = new Socket();
                            //problem here
                           // System.out.println("RR connecting to socket "+nextWorker.getPort());
                            workerSocket.connect(nextWorker);
                            sendRequest(workerSocket, message);
                        }
                        catch(IOException e){
                            e.printStackTrace();
                          //  System.out.println("RR problem sendRequest thread");
                        }
                        
                    });
                    sendRequestThread.start();
                        
                    
                    
                    

                    this.workersAddress.add(nextWorker);
                    this.requestIDtoClient.put(requestID, new InetSocketAddress(workMessage.getSenderHost(), workMessage.getSenderPort()));
                    requestID++;
                }
                else if(!(incoming == null) && incoming.getMessageType() == Message.MessageType.COMPLETED_WORK){
                    this.logger.info("Never here");
                    Message completedWorkMessage = this.workQueue.poll();
                    //get the client inetAddress to send it to
                    long completedID = completedWorkMessage.getRequestID();
                    InetSocketAddress clientAddress = requestIDtoClient.get(completedID);
                    
                    //zooKeeperServer.sendMessage(Message.MessageType.COMPLETED_WORK, completedWorkMessage.getMessageContents(), clientAddress);
                    Message message = new Message(Message.MessageType.COMPLETED_WORK, completedWorkMessage.getMessageContents(), this.myAddress.getHostString(), this.myPort, clientAddress.getHostString(), clientAddress.getPort(), completedID);  
                    this.completedWork.add(message);   

                    //this.outgoingMessages.offer(message);
                    /*
                    Socket responseSocket = new Socket();
                    try{
                        responseSocket.connect(clientAddress);
                    }
                    catch(IOException e){

                    }
                    sendResponse(responseSocket, message);
                    */
                    
                }
                else if(!(incoming == null)){
                    this.incomingMessages.poll();
                }
        }
        
    
    }
    
    private void sendRequest(Socket clientSocket, Message message){
        try {
            this.logger.info("RR begin sending to port: " + clientSocket.getPort());
            OutputStream outputStream = clientSocket.getOutputStream();
            byte[] eof = "\n$$$\n".getBytes();
            ByteBuffer buffer = ByteBuffer.allocate(message.getMessageContents().length + eof.length);
            buffer.put(message.getMessageContents());
            buffer.put(eof);
            //outputStream.write(message.getMessageContents());
            
            outputStream.write(buffer.array());
            //outputStream.write("$$$".getBytes());
            
            // Close resources
            //outputStream.close();
            this.logger.info("RR sent work to worker");
            //this.logger.info(clientSocket.getInputStream());
            //this buffer reader is not happy
            BufferedReader serverResponseReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            this.logger.info("RR created response reader");
                

            String serverResponse;
            String totalResponse = "";
            
            
            //outputStream.close(); 

            //outputStream.flush();
            
            while(!serverResponseReader.ready()){
                //if the port is dead, add the work back to the work queue 
                //and remove the port from the availiable workers and return
                if(deadPorts.contains(clientSocket.getPort())){
                    try{
                        this.workQueue.put(message); 
                    }
                    catch(InterruptedException e){
                      //  System.out.println("can't put back in queue");
                        shutdown();
                        
                    }
                    return;

                }
            }
            while ((serverResponse = serverResponseReader.readLine()) != null) {
                // Receive and display the server's response
                if(serverResponse.contains("$$$")){
                    break;
                }
                totalResponse = totalResponse + serverResponse+"\n";
                this.logger.info("RR response from JR: "+serverResponse);
                //this.logger.info("Server says: " + serverResponse);
            }
            this.logger.info("RR response from worker: "+totalResponse);
            this.completedWork.add(new Message(MessageType.COMPLETED_WORK, totalResponse.getBytes(),clientSocket.getInetAddress().getHostName(),clientSocket.getPort(),"",0));


            //clientSocket.close(); 1212
        } catch (IOException e) {
            this.logger.info("RR problem with request to worker");
            e.printStackTrace();
        }
    }

    private void handleClient(Socket clientSocket) {
        try {
            //setDaemon(true);
            BufferedReader input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            String clientMessage;
            String fullCode = "";
            while ((clientMessage = input.readLine()) != null) {
                if(clientMessage.contains("$$$")){
                    break;
                }
                this.logger.info("RR Received from client: " + clientMessage);
                fullCode = fullCode+ clientMessage+"\n";
            }
            //add to queue
            this.logger.info("handleing in round robin: "+fullCode);
            this.logger.info("RR client socket "+ clientSocket.getInetAddress()+" on port "+clientSocket.getPort());
            this.workQueue.add(new Message(MessageType.WORK, fullCode.getBytes(),clientSocket.getInetAddress().getHostName(),clientSocket.getPort(),"",0));
            
            // Close resources
            //input.close(); 1210

            //clientSocket.close();
        } catch (IOException e) {
            this.logger.info("RR problem with request");
            e.printStackTrace();
        }
        Message resultMessage;
        try{
            this.logger.info("RR waiting for completed work");
            resultMessage = this.completedWork.take();
            this.logger.info("RR sending back: "+resultMessage.toString());
            //Util.readAllBytesFromNetwork(in)
            OutputStream outputStream = clientSocket.getOutputStream();
            //outputStream.write(resultMessage.getMessageContents());

            byte[] eof = "\n$$$\n".getBytes();
            ByteBuffer buffer = ByteBuffer.allocate(resultMessage.getMessageContents().length + eof.length);
            buffer.put(resultMessage.getMessageContents());
            buffer.put(eof);
            outputStream.write(buffer.array());
            //outputStream.close();1245
            //clientSocket.close(); 1210
        }
        catch(Exception e){
            this.logger.info("RR problem with response");
            e.printStackTrace();
        }
    
        
    }
    
    

    
}
