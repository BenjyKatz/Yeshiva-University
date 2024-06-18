package edu.yu.cs.com3800.stage3;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

import edu.yu.cs.com3800.JavaRunner;
import edu.yu.cs.com3800.Message;
import edu.yu.cs.com3800.UDPMessageReceiver;
import edu.yu.cs.com3800.UDPMessageSender;
import edu.yu.cs.com3800.ZooKeeperPeerServer;




public class JavaRunnerFollower extends Thread{
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


    public JavaRunnerFollower(LinkedBlockingQueue<Message> outgoingMessages, LinkedBlockingQueue<Message> incomingMessages, int myPort, ZooKeeperPeerServer zooKeeperServer, InetSocketAddress myAddress, Map<Long,InetSocketAddress> peerIDtoAddress) throws IOException{
        this.myAddress = myAddress;
        this.myPort = myPort;
        this.shutdown = false;
        this.zooKeeperServer = zooKeeperServer;
 
        this.outgoingMessages = outgoingMessages;
        this.incomingMessages = incomingMessages;
 
        this.requestIDtoClient = new HashMap<Long, InetSocketAddress>();
        this.javaRunner = new JavaRunner();
    }
    public void shutdown(){
        this.shutdown = true;
    }
    public void run(){
         
        while(!this.shutdown){ 
            
            Message incoming = this.incomingMessages.peek();
            if(!(incoming == null) && incoming.getMessageType() == Message.MessageType.WORK){
                
                Message workMessage = this.incomingMessages.poll();

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
                byte[] resultBytes = result.getBytes();
                //zooKeeperServer.sendMessage(Message.MessageType.WORK, workMessage.getMessageContents(), nextWorker);

                Message message = new Message(Message.MessageType.COMPLETED_WORK, resultBytes, this.myAddress.getHostString(), this.myPort, workMessage.getSenderHost(), workMessage.getSenderPort(), workMessage.getRequestID());     
                this.outgoingMessages.offer(message);
                System.out.println("Server on port: "+zooKeeperServer.getUdpPort()+" handled request: "+ workMessage.getRequestID());
            }
            else if(!(incoming == null)){
                Message deadMessage = this.incomingMessages.poll();
                System.out.println("deadMessage");
                System.out.println("deadMessage: "+ deadMessage.getMessageContents().toString());
            }
        }
        
    }
    
}
