package edu.yu.cs.com3800.stage5;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import edu.yu.cs.com3800.Message;
import edu.yu.cs.com3800.ZooKeeperPeerServer;
import edu.yu.cs.com3800.Message.MessageType;
import edu.yu.cs.com3800.ZooKeeperPeerServer.ServerState;

public class Gossiper extends Thread{
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

    private LinkedBlockingQueue<Message> workQueue;
    private LinkedBlockingQueue<Message> completedWork;
    private JavaRunnerFollower javaRunnerFollower;
    private RoundRobinLeader roundRobinLeader;
    private Gossip myGossip;

    public Gossiper(LinkedBlockingQueue<Message> outgoingMessages, LinkedBlockingQueue<Message> incomingMessages, int myPort, ZooKeeperPeerServer zooKeeperServer, InetSocketAddress myAddress, Map<Long,InetSocketAddress> peerIDtoAddress, RoundRobinLeader roundRobinLeader, JavaRunnerFollower javaRunnerFollower){
        this.outgoingMessages = outgoingMessages;
        this.incomingMessages = incomingMessages;
        this.peerIDtoAddress = peerIDtoAddress;
        this.myPort = myPort;
        this.zooKeeperServer = zooKeeperServer;
        this.myAddress = null;
        this.roundRobinLeader = roundRobinLeader;
        this.javaRunnerFollower = javaRunnerFollower;
        this.shutdown = false;
    }
    public void setLeader(RoundRobinLeader r){
        this.roundRobinLeader = r;
    }
    public void setFollower(JavaRunnerFollower j){
        this.javaRunnerFollower = j;

    }
    public HashSet<Integer> getDead(){
        return this.myGossip.getFailedNodes();
    }
    public int numFailedNodes(){
        
        return myGossip.getFailedNodes().size();
     }
    public void shutdown(){
        this.shutdown = true;
    }

    @Override
    public void run(){
        //does not include my own address
        //System.out.println("peerIdToAdrress: "+peerIDtoAddress.toString());
        // TODO Auto-generated method stub
        //create a new gossip, 
        ConcurrentHashMap<Integer, long[]> myTable = new ConcurrentHashMap<Integer, long[]>();
        long[] myValues = {1, System.currentTimeMillis(), 0};
        myTable.put(myPort, myValues);
        myGossip = new Gossip(this.myPort, myTable);
        MessageListener messageListener = new MessageListener(outgoingMessages, incomingMessages, myPort, zooKeeperServer, myAddress, peerIDtoAddress, roundRobinLeader, javaRunnerFollower, myGossip);
        Thread listenerThread = new Thread(messageListener);
        listenerThread.start();
        

        while(!this.shutdown){

            //send it to someone randomly
            Long[] peerIDs = this.peerIDtoAddress.keySet().toArray(Long[]::new);
            InetSocketAddress randomRecipient = this.peerIDtoAddress.get(peerIDs[(int)(peerIDs.length*Math.random())]);
            byte[] myTableContent;
            try{
                myTableContent = serializeToByteArray(myTable);
            }
            catch(IOException e){
                myTableContent = null;
                System.out.println("error serializing: "+e.getMessage());
            }
            
            this.zooKeeperServer.sendMessage(MessageType.GOSSIP, myTableContent, randomRecipient);

            //check to see the latest gossip message I got
            

            //if a follower is dead and I am the leader, stop sending them stuff
            
            //increase my heartbeat
            myGossip.beat();
            //wait a second
            
            try{
                sleep(1000);
            }
            catch(InterruptedException e){
                System.out.println("Gossiper Problem waiting one second ");
            }
            
        }
        messageListener.shutdown();
        



    }
    private static byte[] serializeToByteArray(ConcurrentHashMap<Integer, long[]> map) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutput out = null;
        byte[] result;

        try {
            out = new ObjectOutputStream(bos);
            out.writeObject(map);
            result = bos.toByteArray();
        } finally {
            if (out != null) {
                out.close();
            }
            bos.close();
        }

        return result;
    }

       
}


class MessageListener implements Runnable{

    private final InetSocketAddress myAddress;
    private Gossip myGossip;

    //private volatile boolean shutdown;
    private LinkedBlockingQueue<Message> outgoingMessages;
    private LinkedBlockingQueue<Message> incomingMessages;
    private boolean shutdown;
    private Map<Long,InetSocketAddress> peerIDtoAddress;

    private Map<Long, InetSocketAddress> requestIDtoClient;
    private long requestID;

    private ZooKeeperPeerServer zooKeeperServer;
    private int myPort;

    private LinkedBlockingQueue<Message> workQueue;
    private LinkedBlockingQueue<Message> completedWork;
    private JavaRunnerFollower javaRunnerFollower;
    private RoundRobinLeader roundRobinLeader;

    public MessageListener(LinkedBlockingQueue<Message> outgoingMessages, LinkedBlockingQueue<Message> incomingMessages, int myPort, ZooKeeperPeerServer zooKeeperServer, InetSocketAddress myAddress, Map<Long,InetSocketAddress> peerIDtoAddress, RoundRobinLeader roundRobinLeader, JavaRunnerFollower javaRunnerFollower, Gossip myGossip){
        this.outgoingMessages = outgoingMessages;
        this.incomingMessages = incomingMessages;
        this.peerIDtoAddress = peerIDtoAddress;
        this.myPort = myPort;
        this.zooKeeperServer = zooKeeperServer;
        this.myAddress = null;
        this.roundRobinLeader = roundRobinLeader;
        this.javaRunnerFollower = javaRunnerFollower;
        this.myGossip = myGossip;
        this.shutdown = false;
    }

    public void shutdown(){
        this.shutdown = true;
    }
    public void setLeader(RoundRobinLeader r){
        this.roundRobinLeader = r;
    }
    public void setFollower(JavaRunnerFollower j){
        this.javaRunnerFollower = j;

    }
    
   
    
    @Override
    public void run(){
        while(!this.shutdown){
            Message message;
            while(true){
                if(this.shutdown){
                    return;
                }
                message = this.incomingMessages.poll();
                if(message == null){
                    continue;
                }
                else if(message.getMessageType().equals(MessageType.GOSSIP)){
                    break;
                }
                else{
                    this.incomingMessages.add(message);
                }
                
            }
            
            //compare it to my gossip which updates my table
            ConcurrentHashMap<Integer, long[]> otherTable = null;
            try{
                otherTable = deserializeFromByteArray(message.getMessageContents());
            }
            catch(IOException  | ClassNotFoundException e){
                System.out.println("problem deserializing map");
            }
            myGossip.updateTable(otherTable);
            //check to see if the leader is dead
            HashSet<Integer> failedNodes = myGossip.getFailedNodes();

            //problem with null val
            //System.out.println("current leader peer id "+this.zooKeeperServer.getCurrentLeader().getProposedLeaderID());
            if (this.zooKeeperServer.isLeader()){
                //System.out.println("I am the leader and the nodes that have failed are: "+failedNodes);
                if(roundRobinLeader !=null){
                    roundRobinLeader.alertDeadWorkers(failedNodes);
                }
                
                
            }
            
            else if(this.zooKeeperServer.getPeerState().equals(ServerState.OBSERVER) && failedNodes.contains(peerIDtoAddress.get(this.zooKeeperServer.getCurrentLeader().getProposedLeaderID()).getPort())){
                //System.out.println("I am an observer "+this.zooKeeperServer.getServerId() +" and the leader has died I retain my status");
                this.zooKeeperServer.increasePeerEpoch();
            }
            else if(!this.zooKeeperServer.getPeerState().equals(ServerState.LOOKING) &&!this.zooKeeperServer.getPeerState().equals(ServerState.OBSERVER) && (peerIDtoAddress.get(this.zooKeeperServer.getCurrentLeader().getProposedLeaderID()) == null||failedNodes.contains(peerIDtoAddress.get(this.zooKeeperServer.getCurrentLeader().getProposedLeaderID()).getPort()))){
                //if the leader is dead call for a new election
               // System.out.println("I am a follower "+this.zooKeeperServer.getServerId() +" and the leader has died I am changing my state to looking");
                this.zooKeeperServer.increasePeerEpoch();
                this.zooKeeperServer.setPeerState(ServerState.LOOKING);
                
            }
            //If I am the leader


            

            //if a follower is dead and I am the leader, stop sending them stuff
            

            
        }
    }
    private static ConcurrentHashMap<Integer, long[]> deserializeFromByteArray(byte[] data) throws IOException, ClassNotFoundException {
        ByteArrayInputStream bis = new ByteArrayInputStream(data);
        ObjectInput in = null;
        ConcurrentHashMap<Integer, long[]> result;

        try {
            in = new ObjectInputStream(bis);
            result = (ConcurrentHashMap<Integer, long[]>) in.readObject();
        } finally {
            bis.close();
            if (in != null) {
                in.close();
            }
        }

        return result;
    }
}