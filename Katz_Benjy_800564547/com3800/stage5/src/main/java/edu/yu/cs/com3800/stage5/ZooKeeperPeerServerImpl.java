package edu.yu.cs.com3800.stage5;

import edu.yu.cs.com3800.*;
//import edu.yu.cs.com3800.Message.MessageType;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;


public class ZooKeeperPeerServerImpl extends Thread implements ZooKeeperPeerServer{
    private final InetSocketAddress myAddress;
    private final int myPort;
    private ServerState state;
    private volatile boolean shutdown;
    private LinkedBlockingQueue<Message> outgoingMessages;
    private LinkedBlockingQueue<Message> incomingMessages;
    private Long id;
    private long peerEpoch;
    private volatile Vote currentLeader;
    private Map<Long,InetSocketAddress> peerIDtoAddress;

    private UDPMessageSender senderWorker;
    private UDPMessageReceiver receiverWorker;
    private RoundRobinLeader leader;
    private JavaRunnerFollower javaRunnerFollower;
    private Long gateWayID;
    private Gossiper gossiper;
    private int numPeerServers;

    public ZooKeeperPeerServerImpl(int myPort, long peerEpoch, Long id, Map<Long,InetSocketAddress> peerIDtoAddress, Long gatewayID){
        //code here...
        this.myPort = myPort;
        this.peerEpoch = peerEpoch;
        this.id = id;
        this.peerIDtoAddress = peerIDtoAddress;
        //not sure
        //this.myAddress = this.peerIDtoAddress.get(id);
        this.myAddress = new InetSocketAddress("localhost",myPort);
        this.outgoingMessages = new LinkedBlockingQueue<Message>();
        this.incomingMessages = new LinkedBlockingQueue<Message>();
        this.shutdown = false;
        this.state = ServerState.LOOKING;
        this.peerEpoch = peerEpoch;
        this.currentLeader = new Vote(this.id, this.peerEpoch);
        this.leader = null;
        this.gateWayID = gatewayID;
        this.gossiper = null;
        this.numPeerServers = peerIDtoAddress.size()+1;

        
    }

    @Override
    public void shutdown(){
        this.shutdown = true;
        this.senderWorker.shutdown();
        this.receiverWorker.shutdown();
        if(!(this.leader == null)){
            this.leader.shutdown();
        }
        if(!(this.javaRunnerFollower == null)){
            this.javaRunnerFollower.shutdown();
        }
        if(!(this.gossiper == null)){
            this.gossiper.shutdown();
        }
        
    }
    
    public void lostAServer(){
     //   this.numPeerServers--;
    }

    @Override
    public void run(){
        try{
            //step 1: create and run thread that sends broadcast messages
            senderWorker = new UDPMessageSender(this.outgoingMessages,this.myPort);
            senderWorker.start();
            //step 2: create and run thread that listens for messages sent to this server
            receiverWorker = new UDPMessageReceiver(this.incomingMessages,this.myAddress,this.myPort,this);
            receiverWorker.start();
            

        }catch(IOException e){
            e.printStackTrace();
            return;
        }
        
        //step 3: main server loop
        try{
            while (!this.shutdown){
                
                //switch (getPeerState()){
                 //   case LOOKING:
                 if(getPeerState() == ServerState.OBSERVER){
                    System.out.println("Server id "+this.id+" started observing");
                    ZooKeeperLeaderElection election = new ZooKeeperLeaderElection(this, this.incomingMessages);
                    Vote leader = election.lookForLeader();
                    System.out.println("the observer found the leader "+leader.getProposedLeaderID());
                    setCurrentLeader(leader);

                    if(this.gossiper == null){
                        gossiper = new Gossiper(outgoingMessages, incomingMessages, myPort, this, myAddress, peerIDtoAddress, null, null);
                        gossiper.start();
                    }
                    sleep(100);

                 }
                 if(getPeerState() == ServerState.LOOKING ){
                        //start leader election, set leader to the election winner
                        sleep(100);
                        //System.out.println("Server id "+this.id+" started looking");
                        ZooKeeperLeaderElection election = new ZooKeeperLeaderElection(this, this.incomingMessages);
                        Vote leader = election.lookForLeader();

                        setCurrentLeader(leader);
                        //System.out.println("Server id "+this.id+" selects leader "+leader.getProposedLeaderID());
                        sleep(100);
                        //break;
                }
                if(getPeerState() == ServerState.LEADING){
                    if(this.leader == null){
                    //create a roundrobinleader with a new intelSocketAddress
                        if(this.javaRunnerFollower != null){
                          //  System.out.println("new leader shuting down the follower");
                            this.javaRunnerFollower.shutdown();
                            
                            this.javaRunnerFollower.join();
                            this.javaRunnerFollower = null;
                            sleep(200);
                            this.leader.alertDeadWorkers(this.gossiper.getDead());
                            //System.out.println("new leader shut down");
                        } 
                        this.leader = new RoundRobinLeader(this.outgoingMessages, this.incomingMessages, this.myPort, this, this.myAddress, this.peerIDtoAddress);
                        
                        this.leader.start();  

                    }
                    if(this.gossiper == null){
                        gossiper = new Gossiper(outgoingMessages, incomingMessages, myPort, this, myAddress, peerIDtoAddress, leader, null);
                        gossiper.start();
                    }
                    else if (this.gossiper != null){
                        gossiper.setLeader(leader);
                        gossiper.setFollower(null);
                    }
                    
                }
                if(getPeerState() == ServerState.FOLLOWING){
                    if(javaRunnerFollower==null){
                       // System.out.println("creating new javaRunner");
                        javaRunnerFollower = new JavaRunnerFollower(this.outgoingMessages, this.incomingMessages, this.myPort, this, this.myAddress, this.peerIDtoAddress);
                        javaRunnerFollower.start();
                    }
                    if(this.gossiper == null){
                        gossiper = new Gossiper(outgoingMessages, incomingMessages, myPort, this, myAddress, peerIDtoAddress, null, javaRunnerFollower);
                        gossiper.start();
                    }
                    
                }

            }
            sleep(100);
            shutdown();
        }
        catch (Exception e) {
           //code...
        }
    }
    public void setCurrentLeader(Vote v) throws IOException{
        this.currentLeader = v;
    }
    public void increasePeerEpoch(){
        this.peerEpoch++;
    }

    public Vote getCurrentLeader(){
        return this.currentLeader;
    }
    public int getPort(){
        return this.myPort;
    }
    public boolean isLeader(){
        if (leader != null) return true;
        return false;
    }


    //figure out how the sendMessage works
    public void sendMessage(Message.MessageType type, byte[] messageContents, InetSocketAddress target) throws IllegalArgumentException{
        Message message = new Message(type, messageContents, this.myAddress.getHostString(), this.myPort, target.getHostString(), target.getPort());     
        this.outgoingMessages.offer(message);
    }

    public void sendBroadcast(Message.MessageType type, byte[] messageContents){
        
        for(InetSocketAddress peer : this.peerIDtoAddress.values()){
            
            Message message = new Message(type, messageContents, this.myAddress.getHostString(), this.myPort, peer.getHostString(), peer.getPort());
            
            this.outgoingMessages.offer(message);

        }
    }

    public ServerState getPeerState(){
        return this.state;
    }

    public void setPeerState(ServerState newState){
        this.state = newState;
    }

    public Long getServerId(){
        return this.id;
    }

    public long getPeerEpoch(){
        return this.peerEpoch;
    }

    public InetSocketAddress getAddress(){
        return this.myAddress;
    }

    public int getUdpPort(){
        return this.myPort;
    }

    public InetSocketAddress getPeerByID(long peerId){
        return peerIDtoAddress.get(peerId);
    }

    public int getQuorumSize(){
        int temp = this.numPeerServers;
        if(this.gossiper!=null){
            temp = this.numPeerServers - this.gossiper.numFailedNodes();
        }
        
        return (temp/2)+1;
        
    }
    public long getGateWayId(){
        return this.gateWayID;
    }

    

}
