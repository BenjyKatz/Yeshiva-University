package edu.yu.cs.com3800;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import edu.yu.cs.com3800.Message.MessageType;
import edu.yu.cs.com3800.ZooKeeperPeerServer.ServerState;
//import edu.yu.cs.com3800.*;
public class ZooKeeperLeaderElection implements LoggingServer
{
    /**
     * time to wait once we believe we've reached the end of leader election.
     */
    private final static int finalizeWait = 100;
    private ZooKeeperPeerServer myPeerServer;
    private LinkedBlockingQueue<Message> incomingMessages;
    private Long proposedLeader;
    private Long proposedEpoch;
    private ConcurrentHashMap<Long, ElectionNotification> allVotes = new ConcurrentHashMap<>();
    private Logger logger;
    /**
     * Upper bound on the amount of time between two consecutive notification checks.
     * This impacts the amount of time to get the system up again after long partitions. Currently 60 seconds.
     */
    //change jan 4
    private final static int maxNotificationInterval = 500;

    public ZooKeeperLeaderElection(ZooKeeperPeerServer server, LinkedBlockingQueue<Message> incomingMessages)
    {
       // System.out.println("in leader election starting election");
        this.incomingMessages = incomingMessages;
        this.myPeerServer = server;
        this.proposedLeader = this.myPeerServer.getServerId();
        this.proposedEpoch = this.myPeerServer.getPeerEpoch();
        this.allVotes.put(this.proposedLeader, new ElectionNotification(this.proposedLeader, this.myPeerServer.getPeerState(), this.myPeerServer.getServerId(), this.proposedEpoch));
       // System.out.println("end constructor leader election");
        /*try {
            this.logger = initializeLogging( "logs/Election_on_port: " + this.myPeerServer.getUdpPort());
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        //this.logger = Logger.getLogger( "logs/Election_on_port: " + this.myPeerServer.getUdpPort());
    }

    private synchronized Vote getCurrentVote() {
        return new Vote(this.proposedLeader, this.proposedEpoch);
    }

    private byte[] buildMsgContent(){
        
        byte[] byteArray = new byte[3 * Long.BYTES + Character.BYTES];
        ByteBuffer buffer = ByteBuffer.wrap(byteArray);
       
        buffer.putLong(this.proposedLeader);
        buffer.putChar(this.myPeerServer.getPeerState().getChar());
        buffer.putLong(this.myPeerServer.getServerId());
        buffer.putLong(this.myPeerServer.getPeerEpoch());
 
        return byteArray;
    }
    public static byte[] buildMsgContent(ElectionNotification notification) {
        byte[] byteArray = new byte[3 * Long.BYTES + Character.BYTES];
        ByteBuffer buffer = ByteBuffer.wrap(byteArray);
        buffer.putLong(notification.getProposedLeaderID());
        char c = notification.getState().getChar();
        buffer.putChar(c);
        buffer.putLong(notification.getSenderID());
        buffer.putLong(notification.getPeerEpoch());
        return byteArray;
    }
    public static ElectionNotification getNotificationFromMessage(Message message){
        ByteBuffer buffer = ByteBuffer.wrap(message.getMessageContents());
        long leader = buffer.getLong();
        char stateChar = buffer.getChar();
        long senderId = buffer.getLong();
        long peerEpoch = buffer.getLong();
        return new ElectionNotification(leader, ZooKeeperPeerServer.ServerState.getServerState(stateChar), senderId, peerEpoch);
    }
    
    public /*synchronized*/ Vote lookForLeader()
    {
        //System.out.println("starting lookForLeader "+this.myPeerServer.getServerId());
        //System.out.println("Quorum "+this.myPeerServer.getQuorumSize()+" I starting "+this.myPeerServer.getServerId());
        try {
            this.logger = initializeLogging( "logs/Election_on_port" + this.myPeerServer.getUdpPort());
        } catch (IOException e) {
            //e.printStackTrace();
        }
        

        //this.logger.info("looking for leader"); 
        //this.logger.info("My Server Id is: "+this.myPeerServer.getServerId());

        //send initial notifications to other peers to get things started
        //sendNotifications();
        this.myPeerServer.sendBroadcast(Message.MessageType.ELECTION, buildMsgContent());
        int currentWait = finalizeWait*2;
        try{
            //Loop, exchanging notifications with other servers until we find a leader
           // System.out.println(this.myPeerServer.getPeerState().toString());
            
            while (this.myPeerServer.getPeerState() == ZooKeeperPeerServer.ServerState.LOOKING || this.myPeerServer.getPeerState() == ZooKeeperPeerServer.ServerState.OBSERVER){
               // System.out.println("Quorum "+this.myPeerServer.getQuorumSize()+" I am looking and at the begining of the loop id "+this.myPeerServer.getServerId());
                //Remove next notification from queue, timing out after 2 times the termination time
                //if no notifications received..
                    //..resend notifications to prompt a reply from others..
                    //.and implement exponential back-off when notifications not received..
                //jan 4 broadcast here
                this.myPeerServer.sendBroadcast(Message.MessageType.ELECTION, buildMsgContent());
                Message nextReceived = this.incomingMessages.poll(currentWait, TimeUnit.MILLISECONDS);
                //System.out.println("server id 2 rec: "+this.myPeerServer.getServerId());
                while(nextReceived!= null && nextReceived.getMessageType()==MessageType.GOSSIP){
                    this.incomingMessages.add(nextReceived);
                    nextReceived = this.incomingMessages.poll(currentWait, TimeUnit.MILLISECONDS);

                }
                //System.out.println("message rec: "+nextReceived);
                if(nextReceived == null){
                    currentWait = Math.min(currentWait*2, maxNotificationInterval);
                    //sendNotifications();
                   // System.out.println("sending election broadcast epoch:" + this.myPeerServer.getPeerEpoch()+" from server "+this.myPeerServer.getServerId());
                    this.myPeerServer.sendBroadcast(Message.MessageType.ELECTION, buildMsgContent());
                }
                else{
                    
                    ElectionNotification notification =  getNotificationFromMessage(nextReceived);
                  //  System.out.println("I received a message for new proposed ID: "+notification.getProposedLeaderID());
                    ZooKeeperPeerServer.ServerState state = notification.getState();

                
                
                //if/when we get a message and it's from a valid server and for a valid server..
                //switch on the state of the sender:
                    switch(state){
                        case OBSERVER:
                            continue;
                            /*
                            System.out.println("I am observing");
                            Vote voteOb = new Vote(notification.getProposedLeaderID(), notification.getPeerEpoch());
                            //this.allVotes.put(notification.getSenderID(), notification);
                            
                            if (haveEnoughVotes(this.allVotes, voteOb) ) {
                                System.out.println("We have enough votes, the winner I observed is "+ voteOb.getProposedLeaderID());
                                Vote vOb = acceptElectionWinner(notification);
                                //this.myPeerServer.sendBroadcast(Message.MessageType.ELECTION, buildMsgContent());
                                //sendNotifications();
                                return vOb;
                            }*/
                            
                            
                        case LOOKING: //if the sender is also looking
                            //Vote sendersVote = new Vote(notification.getProposedLeaderID(), notification.getPeerEpoch());
                            this.allVotes.put(notification.getSenderID(), notification);

                            //if the received message has a vote for a leader which supersedes mine, change my vote and tell all my peers what my new vote is.
                          //  System.out.println("Epoch: "+notification.getPeerEpoch());
                            if (supersedesCurrentVote(notification.getProposedLeaderID(), notification.getPeerEpoch())){
                              //  System.out.println("found a better leader: "+ notification.getProposedLeaderID()+" is better than "+this.proposedLeader); 
                                this.proposedLeader = notification.getProposedLeaderID();
                                this.proposedEpoch = notification.getPeerEpoch();
                                //change my vote to his vote
                                this.allVotes.put(this.myPeerServer.getServerId(), notification);
                                //sendNotifications();
                                this.myPeerServer.sendBroadcast(Message.MessageType.ELECTION, buildMsgContent());
                            }
                            //keep track of the votes I received and who I received them from.
                            ////if I have enough votes to declare my currently proposed leader as the leader:
                       //     System.out.println("Quorum size: "+ this.myPeerServer.getQuorumSize()+" votes for id: "+ this.myPeerServer.getServerId()+" "+this.allVotes);
                            if (haveEnoughVotes(this.allVotes, getCurrentVote())==true) {
                              //  System.out.println("Quorum size: "+ this.myPeerServer.getQuorumSize()+" we have enough votes");
                                
                                Message nextMessage = null;
                                //commented out jan 4
                                this.myPeerServer.sendBroadcast(Message.MessageType.ELECTION, buildMsgContent());
                                int gossipCount = 0; 
                                while ((nextMessage = this.incomingMessages.poll(finalizeWait, TimeUnit.MILLISECONDS)) != null) {
                                    if(nextMessage.getMessageType()==MessageType.GOSSIP){
                                        this.incomingMessages.add(nextMessage);
                                        gossipCount++;
                                        if(gossipCount == 2){
                                            //ElectionNotification en = new ElectionNotification(this.proposedLeader, notification.getState(), this.myPeerServer.getServerId(), this.proposedEpoch);
                                            //return acceptElectionWinner(en);
                                            break;
                                        }
                                        continue;
                                    }
                                    gossipCount=0;
                                    ElectionNotification electionNotification = getNotificationFromMessage(nextMessage);
                                    //if someone else is not looking, a winner is determined
                                    if (electionNotification.getState() == ZooKeeperPeerServer.ServerState.LEADING || electionNotification.getState() == ZooKeeperPeerServer.ServerState.FOLLOWING){  
                                      //  System.out.println("found someone who knows the leader "+ electionNotification.getProposedLeaderID());   
                                        return acceptElectionWinner(electionNotification);
                                    }
                                    else if (supersedesCurrentVote(electionNotification.getProposedLeaderID(), electionNotification.getPeerEpoch()) ) {
                                      //  System.out.println("found a better leader: "+ notification.getProposedLeaderID()); 
                                        this.incomingMessages.add(nextMessage);
                                        //break to start of the larger while loop
                                        break;
                                    }
                                }
                                
                                //went through all messages
                                if(nextMessage == null){
                                  // System.out.println("finished the messages"); 
                                    //no more messages
                                    //if we have enough votes, tell everyone
                                    //this.myPeerServer.sendBroadcast(Message.MessageType.ELECTION, buildMsgContent());

                                    ElectionNotification en = new ElectionNotification(this.proposedLeader, notification.getState(), this.myPeerServer.getServerId(), this.proposedEpoch);
                                    return acceptElectionWinner(en);
                                }

                                
                            }
                            
                            break;
                                //first check if there are any new votes for a higher ranked possible leader before I declare a leader. If so, continue in my election loop
                                //If not, set my own state to either LEADING (if I won the election) or FOLLOWING (if someone lese won the election) and exit the election
                        case FOLLOWING: case LEADING: //if the sender is following a leader already or thinks it is the leader
                          //  System.out.println("Other server is following or leading");
                            Vote vote = new Vote(notification.getProposedLeaderID(), notification.getPeerEpoch());
                            this.allVotes.put(notification.getSenderID(), notification);
                            
                            if (haveEnoughVotes(this.allVotes, vote) ) {
                                Vote v = acceptElectionWinner(notification);
                                this.myPeerServer.sendBroadcast(Message.MessageType.ELECTION, buildMsgContent());
                                //sendNotifications();
                                return v;
                            }
                            break;
                            //IF: see if the sender's vote allows me to reach a conclusion based on the election epoch that I'm in, i.e. it gives the majority to the vote of the FOLLOWING or LEADING peer whose vote I just received.
                                //if so, accept the election winner.
                                //As, once someone declares a winner, we are done. We are not worried about / accounting for misbehaving peers.
                            //ELSE: if n is from a LATER election epoch
                                //IF a quorum from that epoch are voting for the same peer as the vote of the FOLLOWING or LEADING peer whose vote I just received.
                                //THEN accept their leader, and update my epoch to be their epoch
                                //ELSE:
                                    //keep looping on the election loop. 
                    } 
                }  
            }
        }
        catch(InterruptedException e){
            //System.out.println("could not find leader");
            return null;
        }
       // System.out.println("Returning null for election");
        return null;
    }

    private Vote acceptElectionWinner(ElectionNotification n)
    {
        //System.out.println("found the winner: "+ n.getProposedLeaderID()); 
        //set my state to either LEADING or FOLLOWING
        //clear out the incoming queue before returning
        this.proposedLeader = n.getProposedLeaderID();
        this.proposedEpoch = n.getPeerEpoch();
        this.incomingMessages.clear();
        if(this.myPeerServer.getPeerState() == ServerState.OBSERVER){

        }
        else if(this.proposedLeader == this.myPeerServer.getServerId()){
            this.myPeerServer.setPeerState(ZooKeeperPeerServer.ServerState.LEADING);
        }
        else{
            this.myPeerServer.setPeerState(ZooKeeperPeerServer.ServerState.FOLLOWING);
        }

        Vote vote = this.getCurrentVote();
        try {
            this.myPeerServer.setCurrentLeader(vote);
        } catch (IOException e) {
            e.printStackTrace();
        }  
        return vote;
    }

    /*
     * We return true if one of the following three cases hold:
     * 1- New epoch is higher
     * 2- New epoch is the same as current epoch, but server id is higher.
     */
     protected boolean supersedesCurrentVote(long newId, long newEpoch) {
       

         return (newEpoch > this.proposedEpoch) || ((newEpoch == this.proposedEpoch) && (newId > this.proposedLeader));
     }
    /**
     * Termination predicate. Given a set of votes, determines if have sufficient support for the proposal to declare the end of the election round.
     * Who voted for who isn't relevant, we only care that each server has one current vote
     */
    protected boolean haveEnoughVotes(Map<Long, ElectionNotification > votes, Vote proposal){
       // System.out.println("Quorum Size: "+ this.myPeerServer.getQuorumSize());
        int count = 0;
        for (ElectionNotification en : votes.values()) {
            if (en.getProposedLeaderID() == proposal.getProposedLeaderID()) {
                count++;
            }
        }
        //System.out.println("votes for me: "+count);
        if (count> this.myPeerServer.getQuorumSize()){
  
            return true;
        }
        return false;
       //is the number of votes for the proposal > the size of my peer server’s quorum?
    }
}
