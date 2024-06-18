package edu.yu.cs.com3800.stage5;

import edu.yu.cs.com3800.*;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

public class Stage5Demo {
    private String validClass = "package edu.yu.cs.fall2019.com3800.stage1;\n\npublic class HelloWorld\n{\n    public String run()\n    {\n        return \"Hello world!\";\n    }\n}\n";

    private LinkedBlockingQueue<Message> outgoingMessages;
    private LinkedBlockingQueue<Message> incomingMessages;
    private int[] ports = {8010, 8020, 8030, 8040, 8050, 8060, 8070, 8080};
    //private int[] ports = {8010, 8020};
    private int leaderPort = this.ports[this.ports.length - 1];
    private int myPort = 9999;
    private InetSocketAddress myAddress = new InetSocketAddress("localhost", this.myPort);
    private ArrayList<ZooKeeperPeerServerImpl> servers;
    FileWriter myWriter;
    public Stage5Demo() {
        
    }
    public void runDemo() throws Exception{
        try{
            this.myWriter = new FileWriter("output.txt");
        }
        catch(IOException e){
            //
        }

        //step 1: create sender & sending queue
        this.outgoingMessages = new LinkedBlockingQueue<>();
        UDPMessageSender sender = new UDPMessageSender(this.outgoingMessages,this.myPort);
        //step 2: create servers
        createServers();
        //step2.1: wait for servers to get started
        try {
            Thread.sleep(5000);
        }
        catch (InterruptedException e) {
        }
        printLeaders();
        //step 3: since we know who will win the election, send requests to the leader, this.leaderPort
        //for (int i = 0; i < this.ports.length; i++) {
        
        for (int i = 0; i < 8; i++) {
            String code = this.validClass.replace("world!", "world! from code version " + i);
            sendRequests(code);
            //sendMessage(code);
        }
        
        //Util.startAsDaemon(sender, "Sender thread");
        //this.incomingMessages = new LinkedBlockingQueue<>();
        //UDPMessageReceiver receiver = new UDPMessageReceiver(this.incomingMessages, this.myAddress, this.myPort,null);
        //Util.startAsDaemon(receiver, "Receiver thread");
        //step 4: validate responses from leader
        //System.out.println("about to print responses");
       // printResponses();

        //step 5: stop servers
        stopOneServer(5);
        Thread.sleep(90000);
        
        
        

        for (int i = 8; i < 17; i++) {
            
            String code = this.validClass.replace("world!", "world! from code version " + i);
            sendRequests(code);
            //sendMessage(code);
        }
        Thread.sleep(10000);

        //kill the leader
        stopOneServer(6);
        
        Thread.sleep(90000);
        
        printLeaders();
        for (int i = 17; i < 26; i++) {
            String code = this.validClass.replace("world!", "world! from code version " + i);
            sendRequests(code);
            //sendMessage(code);
        }
        
        printLeaders();

        stopServers();

    }

    private void printLeaders() {
        for (ZooKeeperPeerServer server : this.servers) {
            Vote leader = server.getCurrentLeader();
            if (leader != null) {
                try{
                    this.myWriter.write("Server on port " + server.getAddress().getPort() + " whose ID is " + server.getServerId() + " has the following ID as its leader: " + leader.getProposedLeaderID() + " and its state is " + server.getPeerState().name()+"\n");
                    this.myWriter.close();
                }
                catch(IOException e){
                    //trouble writing
                }
                System.out.println("Server on port " + server.getAddress().getPort() + " whose ID is " + server.getServerId() + " has the following ID as its leader: " + leader.getProposedLeaderID() + " and its state is " + server.getPeerState().name());
            }
        }
        
    }

    private void stopServers() {
        for (ZooKeeperPeerServer server : this.servers) {
            server.shutdown();
        }
    }
    private void stopOneServer(int x){
        this.servers.remove(x).shutdown();
    }

    private void printResponses() throws Exception {
        String completeResponse = "";
        //for (int i = 0; i < this.ports.length; i++) {
        for (int i = 0; i < 20; i++) {
            Message msg = this.incomingMessages.take();
            String response = new String(msg.getMessageContents());
            completeResponse += "Response to request " + msg.getRequestID() + ":\n" + response + "\n\n";
        }
        System.out.println(completeResponse);
    }

    private void sendMessage(String code) throws InterruptedException {
        Message msg = new Message(Message.MessageType.WORK, code.getBytes(), this.myAddress.getHostString(), this.myPort, "localhost", this.leaderPort);
        this.outgoingMessages.put(msg);
    }

    private void createServers() throws IOException {
        //create IDs and addresses
        HashMap<Long, InetSocketAddress> peerIDtoAddress = new HashMap<>(8);
        for (int i = 0; i < this.ports.length; i++) {
            peerIDtoAddress.put(Integer.valueOf(i).longValue(), new InetSocketAddress("localhost", this.ports[i]));
        }
        //create servers
        
        this.servers = new ArrayList<>(3);
        boolean createGateway = true;
        for (Map.Entry<Long, InetSocketAddress> entry : peerIDtoAddress.entrySet()) {
            HashMap<Long, InetSocketAddress> map = (HashMap<Long, InetSocketAddress>) peerIDtoAddress.clone();
            map.remove(entry.getKey());
            if(createGateway){
                GatewayPeerServerImpl gps = new GatewayPeerServerImpl(entry.getValue().getPort(), 0, entry.getKey(), map);
                GatewayServer gs = new GatewayServer(8000, gps);
                this.servers.add(gps);
                gps.start();
                gs.start();
                createGateway = false;
                continue;
            }
            ZooKeeperPeerServerImpl server = new ZooKeeperPeerServerImpl(entry.getValue().getPort(), 0, entry.getKey(), map, (long)0);
            this.servers.add(server);
            server.start();
        }
    }
    

    private void sendRequests(String code){
        System.out.println("Demo Making requests");
        String url = "http://localhost:8000/compileandrun";

        try {
            // Create a URL object
            URL apiUrl = new URL(url);

            // Open a connection to the URL
            HttpURLConnection connection = (HttpURLConnection) apiUrl.openConnection();
           // connection.setReadTimeout(3000);
            // Set the request method to POST
            connection.setRequestMethod("POST");
            // Enable input/output streams for the connection
            connection.setDoInput(true);
            connection.setDoOutput(true);

            // Set the Content-Type header to "text/x-java-source"
            connection.setRequestProperty("Content-Type", "text/x-java-source");

            // Get the output stream of the connection
            try (OutputStream outputStream = connection.getOutputStream()) {
                // Write the Java source code to the output stream
                outputStream.write(code.getBytes());
            }
            catch(SocketTimeoutException e){
                System.out.println("DEMO took too long making request");
                return;
            }

            // Get the response code
            //here it is blocking because it is not properly sending to Round robin
            
            int responseCode;
            try{
                responseCode = connection.getResponseCode();
            }
            catch(SocketTimeoutException e){
                System.out.println("DEMO took too long making request");
                return;
            }
            
            System.out.println("Response Code: " + responseCode);
            try{
                this.myWriter.write("Response Code: " + responseCode+"\n");
                this.myWriter.close();
            }
            catch(IOException e){
                //trouble writing
            }

            // Read the response content
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            StringBuilder responseContent = new StringBuilder();

            while ((line = reader.readLine()) != null) {
                responseContent.append(line);
            }
            reader.close();

            // Print the response content
            try{
                this.myWriter.write("Response Content: \n" + responseContent.toString()+"\n");
                this.myWriter.close();
            }
            catch(IOException e){
                //trouble writing
            }
            System.out.println("Response Content:");
            System.out.println(responseContent.toString());

            // Disconnect the connection
            connection.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        //new Stage3PeerServerRunnerTest();
       // new Stage5Demo();
    }
}
