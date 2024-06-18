package edu.yu.cs.com3800.stage3;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

import edu.yu.cs.com3800.Message;
import edu.yu.cs.com3800.UDPMessageReceiver;
import edu.yu.cs.com3800.UDPMessageSender;
import edu.yu.cs.com3800.ZooKeeperPeerServer;

public class RoundRobinLeader extends Thread{
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

    public RoundRobinLeader(LinkedBlockingQueue<Message> outgoingMessages, LinkedBlockingQueue<Message> incomingMessages, int myPort, ZooKeeperPeerServer zooKeeperServer, InetSocketAddress myAddress, Map<Long,InetSocketAddress> peerIDtoAddress){
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
        setDaemon(true);
        
    }
    
    public void shutdown(){
        this.shutdown = true;
    }
    public void run(){
        System.out.println("The leader round robin has started");
        
        try{
            //allow time for followers to assume role before assigning tasks
            sleep(500);
        }
        catch(Exception e){
        } 
        while(!this.shutdown){
            //System.out.println("running leader");
            Message incoming = this.incomingMessages.peek();
                if(!(incoming == null) && incoming.getMessageType() == Message.MessageType.WORK){
                    
                    Message workMessage = this.incomingMessages.poll();
                    InetSocketAddress nextWorker = this.workersAddress.remove();

                    Message message = new Message(Message.MessageType.WORK, workMessage.getMessageContents(), this.myAddress.getHostString(), this.myPort, nextWorker.getHostString(), nextWorker.getPort(), this.requestID);     
                    this.outgoingMessages.offer(message);

                    this.workersAddress.add(nextWorker);
                    this.requestIDtoClient.put(requestID, new InetSocketAddress(workMessage.getSenderHost(), workMessage.getSenderPort()));
                    requestID++;
                }
                else if(!(incoming == null) && incoming.getMessageType() == Message.MessageType.COMPLETED_WORK){
                    Message completedWorkMessage = this.incomingMessages.poll();
                    //get the client inetAddress to send it to
                    long completedID = completedWorkMessage.getRequestID();
                    InetSocketAddress clientAddress = requestIDtoClient.get(completedID);
                    
                    //zooKeeperServer.sendMessage(Message.MessageType.COMPLETED_WORK, completedWorkMessage.getMessageContents(), clientAddress);
                    Message message = new Message(Message.MessageType.COMPLETED_WORK, completedWorkMessage.getMessageContents(), this.myAddress.getHostString(), this.myPort, clientAddress.getHostString(), clientAddress.getPort(), completedID);     
                    this.outgoingMessages.offer(message);
                    
                }
                else if(!(incoming == null)){
                    this.incomingMessages.poll();
                }
        }
    
    }

    
}
