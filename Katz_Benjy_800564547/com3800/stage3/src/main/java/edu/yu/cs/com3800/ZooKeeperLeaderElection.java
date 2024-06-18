package edu.yu.cs.com3800;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
//import edu.yu.cs.com3800.*;
public class ZooKeeperLeaderElection implements LoggingServer
{
    /**
     * time to wait once we believe we've reached the end of leader election.
     */
    private final static int finalizeWait = 200;
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
    private final static int maxNotificationInterval = 60000;

    public ZooKeeperLeaderElection(ZooKeeperPeerServer server, LinkedBlockingQueue<Message> incomingMessages)
    {
        this.incomingMessages = incomingMessages;
        this.myPeerServer = server;
        this.proposedLeader = this.myPeerServer.getServerId();
        this.proposedEpoch = this.myPeerServer.getPeerEpoch();
        this.allVotes.put(this.proposedLeader, new ElectionNotification(this.proposedLeader, this.myPeerServer.getPeerState(), this.myPeerServer.getServerId(), this.proposedEpoch));
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
    
    public synchronized Vote lookForLeader()
    {
        try {
            this.logger = initializeLogging( "logs/Election_on_port" + this.myPeerServer.getUdpPort());
        } catch (IOException e) {
            e.printStackTrace();
        }

       this.logger.info("looking for leader"); 
       this.logger.info("My Server Id is: "+this.myPeerServer.getServerId()); 
        //send initial notifications to other peers to get things started
        //sendNotifications();
        this.myPeerServer.sendBroadcast(Message.MessageType.ELECTION, buildMsgContent());
        int currentWait = finalizeWait*2;
        try{
            //Loop, exchanging notifications with other servers until we find a leader
         
            while (this.myPeerServer.getPeerState() == ZooKeeperPeerServer.ServerState.LOOKING){
                
                //Remove next notification from queue, timing out after 2 times the termination time
                //if no notifications received..
                    //..resend notifications to prompt a reply from others..
                    //.and implement exponential back-off when notifications not received..
                
                Message nextReceived = this.incomingMessages.poll(currentWait, TimeUnit.MILLISECONDS);
                
                if(nextReceived == null){
                    currentWait = Math.min(currentWait*2, maxNotificationInterval);
                    //sendNotifications();
                    this.myPeerServer.sendBroadcast(Message.MessageType.ELECTION, buildMsgContent());
                }
                else{
                    
                    ElectionNotification notification =  getNotificationFromMessage(nextReceived);
                    ZooKeeperPeerServer.ServerState state = notification.getState();

                
                
                //if/when we get a message and it's from a valid server and for a valid server..
                //switch on the state of the sender:
                    switch(state){
                        case OBSERVER:
                            continue;
                        case LOOKING: //if the sender is also looking
                            //Vote sendersVote = new Vote(notification.getProposedLeaderID(), notification.getPeerEpoch());
                            this.allVotes.put(notification.getSenderID(), notification);

                            //if the received message has a vote for a leader which supersedes mine, change my vote and tell all my peers what my new vote is.
                            if (supersedesCurrentVote(notification.getProposedLeaderID(), notification.getPeerEpoch())){
                                this.logger.info("found a better leader: "+ notification.getProposedLeaderID()+" is better than "+this.proposedLeader); 
                                this.proposedLeader = notification.getProposedLeaderID();
                                this.proposedEpoch = notification.getPeerEpoch();
                                //change my vote to his vote
                                this.allVotes.put(this.myPeerServer.getServerId(), notification);
                                //sendNotifications();
                                this.myPeerServer.sendBroadcast(Message.MessageType.ELECTION, buildMsgContent());
                            }
                            //keep track of the votes I received and who I received them from.
                            ////if I have enough votes to declare my currently proposed leader as the leader:
                            if (haveEnoughVotes(this.allVotes, getCurrentVote())==true) {
                                Message nextMessage;
                                while ((nextMessage = this.incomingMessages.poll(finalizeWait, TimeUnit.MILLISECONDS)) != null) {
                                    ElectionNotification electionNotification = getNotificationFromMessage(nextMessage);
                                    //if someone else is not looking, a winner is determined
                                    if (electionNotification.getState() == ZooKeeperPeerServer.ServerState.LEADING || electionNotification.getState() == ZooKeeperPeerServer.ServerState.FOLLOWING){  
                                        this.logger.info("found someone who knows the leader "+ electionNotification.getProposedLeaderID());   
                                        return acceptElectionWinner(electionNotification);
                                    }
                                    else if (supersedesCurrentVote(electionNotification.getProposedLeaderID(), electionNotification.getPeerEpoch()) ) {
                                        this.logger.info("found a better leader: "+ notification.getProposedLeaderID()); 
                                        this.incomingMessages.add(nextMessage);
                                        //break to start of the larger while loop
                                        break;
                                    }
                                }
                                //went through all messages
                                if(nextMessage == null){
                                    this.logger.info("finished the messages"); 
                                    //no more messages
                                    ElectionNotification en = new ElectionNotification(this.proposedLeader, notification.getState(), this.myPeerServer.getServerId(), this.proposedEpoch);
                                    return acceptElectionWinner(en);
                                }

                                
                            }
                            
                            break;
                                //first check if there are any new votes for a higher ranked possible leader before I declare a leader. If so, continue in my election loop
                                //If not, set my own state to either LEADING (if I won the election) or FOLLOWING (if someone lese won the election) and exit the election
                        case FOLLOWING: case LEADING: //if the sender is following a leader already or thinks it is the leader
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
            return null;
        }
        return null;
    }

    private Vote acceptElectionWinner(ElectionNotification n)
    {
        this.logger.info("found the winner: "+ n.getProposedLeaderID()); 
        //set my state to either LEADING or FOLLOWING
        //clear out the incoming queue before returning
        this.proposedLeader = n.getProposedLeaderID();
        this.proposedEpoch = n.getPeerEpoch();
        this.incomingMessages.clear();
        if(this.proposedLeader == this.myPeerServer.getServerId()){
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

        int count = 0;
        for (ElectionNotification en : votes.values()) {
            if (en.getProposedLeaderID() == proposal.getProposedLeaderID()) {
                count++;
            }
        }
        if (count> this.myPeerServer.getQuorumSize()){
  
            return true;
        }
        return false;
       //is the number of votes for the proposal > the size of my peer server’s quorum?
    }
}
