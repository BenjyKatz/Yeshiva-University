package edu.yu.cs.com3800.stage4;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

import edu.yu.cs.com3800.JavaRunner;
import edu.yu.cs.com3800.LoggingServer;
import edu.yu.cs.com3800.Message;
import edu.yu.cs.com3800.UDPMessageReceiver;
import edu.yu.cs.com3800.UDPMessageSender;
import edu.yu.cs.com3800.ZooKeeperPeerServer;
import edu.yu.cs.com3800.Message.MessageType;
import java.util.logging.Logger;

import edu.yu.cs.com3800.LoggingServer;




public class JavaRunnerFollower extends Thread implements LoggingServer{
    private final InetSocketAddress myAddress;
    private boolean shutdown;
    //private volatile boolean shutdown;
    private LinkedBlockingQueue<Message> outgoingMessages;
    private LinkedBlockingQueue<Message> incomingMessages;

    private Map<Long,InetSocketAddress> peerIDtoAddress;

    private Map<Long, InetSocketAddress> requestIDtoClient;
    private long requestID;

    private ZooKeeperPeerServer zooKeeperServer;
    private int myPort;
    private JavaRunner javaRunner;
    private Socket globalClientSocket;
    private LinkedBlockingQueue<Message> workQueue;
    private LinkedBlockingQueue<Message> completedWork;
    private Logger logger;


    public JavaRunnerFollower(LinkedBlockingQueue<Message> outgoingMessages, LinkedBlockingQueue<Message> incomingMessages, int myPort, ZooKeeperPeerServer zooKeeperServer, InetSocketAddress myAddress, Map<Long,InetSocketAddress> peerIDtoAddress) throws IOException{
        setDaemon(true);
        this.myAddress = myAddress;
        this.myPort = myPort;
        this.shutdown = false;
        this.zooKeeperServer = zooKeeperServer;
 
        this.outgoingMessages = outgoingMessages;
        this.incomingMessages = incomingMessages;
 
        this.requestIDtoClient = new HashMap<Long, InetSocketAddress>();
        this.javaRunner = new JavaRunner();
        this.globalClientSocket = null;
        this.workQueue = new LinkedBlockingQueue<Message>();
        this.completedWork = new LinkedBlockingQueue<Message>();
        try{
            this.logger = initializeLogging("JavaRunner" + "-on-port-" + this.myPort);
        }
        catch(IOException e){
            System.out.println("logger wont initialize");
        }

    }
    public void shutdown(){
        this.shutdown = true;
    }
    public void run(){
        startListener(); 
        while(!this.shutdown){ 
            //this.logger.info("JR is running");
            Message incoming = this.workQueue.peek();
            
            if(!(incoming == null) && incoming.getMessageType() == Message.MessageType.WORK){
                this.logger.info("JR I got work to do ");
                Message workMessage = this.workQueue.poll();

                byte[] code = workMessage.getMessageContents();
                InputStream codeInputStream = new ByteArrayInputStream(code);
                String result = "";
                try {
                    result = this.javaRunner.compileAndRun(codeInputStream);
                } catch (IllegalArgumentException | IOException | ReflectiveOperationException e) {
                    // TODO Auto-generated catch block
                    //e.printStackTrace();
                    result = e.toString();
                }
                this.logger.info("JR result: "+result);
                byte[] resultBytes = result.getBytes();
                //zooKeeperServer.sendMessage(Message.MessageType.WORK, workMessage.getMessageContents(), nextWorker);

                Message message = new Message(Message.MessageType.COMPLETED_WORK, resultBytes, this.myAddress.getHostString(), this.myPort, workMessage.getSenderHost(), workMessage.getSenderPort(), workMessage.getRequestID());     
                this.completedWork.add(message);
                //sendResponse(globalClientSocket, message);
                //this.outgoingMessages.offer(message);
                this.logger.info("Server on port: "+zooKeeperServer.getUdpPort()+" handled request: "+ workMessage.getRequestID());
            }
            else if(!(incoming == null)){
                Message deadMessage = this.incomingMessages.poll();
                this.logger.info("deadMessage");
                this.logger.info("deadMessage: "+ deadMessage.getMessageContents().toString());
            }
        }
        
    }
    private void startListener(){
        try{
            ServerSocket serverSocket = new ServerSocket(this.myPort);
            Thread getRequests = new Thread(() -> {
                try{
                    while (true) {
                        Socket clientSocket = serverSocket.accept();
                        this.globalClientSocket = clientSocket;
                        this.logger.info("JR Accepted connection from " + clientSocket.getInetAddress());

                        // Handle client communication in a new thread
                        new Thread(() -> handleClient(clientSocket)).start();
                    }
                }
                catch(Exception e){

                }
            });
            getRequests.start();

        }
        catch(Exception e){

        }
    }


    private void handleClient(Socket clientSocket) {
        try {
            BufferedReader input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            String clientMessage;
            String fullCode = "";
            this.logger.info("JR on port "+myPort+ " received work form "+clientSocket.getPort());
            while ((clientMessage = input.readLine()) != null) {
                if(clientMessage.contains("$$$")){
                    break;
                }
                this.logger.info("JR Received from client: " + clientMessage);
                fullCode = fullCode+ clientMessage+"\n";
            }

            //add to queue
            this.logger.info("JR work added to queue");
            this.workQueue.add(new Message(MessageType.WORK, fullCode.getBytes(),clientSocket.getInetAddress().getHostName(),clientSocket.getPort(),"",0));
            Message resultMessage;
            try{
                this.logger.info("JR waiting for work to be done");
                resultMessage = this.completedWork.take();
                this.logger.info("JR sending back: "+resultMessage.toString());
                OutputStream outputStream = clientSocket.getOutputStream();
                //outputStream.write(resultMessage.getMessageContents());
                
                byte[] eof = "\n$$$\n".getBytes();
                ByteBuffer buffer = ByteBuffer.allocate(resultMessage.getMessageContents().length + eof.length);
                buffer.put(resultMessage.getMessageContents());
                buffer.put(eof);
                
                outputStream.write(buffer.array());
                
                //outputStream.close();1250
            }
            catch(Exception e){
                this.logger.info("JR problem with response");
                e.printStackTrace();
            }

            // Close resources
            //input.close(); 1210
            //clientSocket.close(); 1210
        } catch (IOException e) {
            this.logger.info("JR There was a problem handling request in JR");
            //e.printStackTrace();
        }
    }
    
}
