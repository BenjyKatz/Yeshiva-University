/*
package edu.yu.cs.com3800.stage5;

import edu.yu.cs.com3800.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

public class Stage5DemoProcess {
    private String validClass = "package edu.yu.cs.fall2019.com3800.stage1;\n\npublic class HelloWorld\n{\n    public String run()\n    {\n        return \"Hello world!\";\n    }\n}\n";

    private LinkedBlockingQueue<Message> outgoingMessages;
    private LinkedBlockingQueue<Message> incomingMessages;
    private int[] ports = {8010, 8020, 8030, 8040, 8050, 8060, 8070, 8080};
    //private int[] ports = {8010, 8020};
    private int leaderPort = this.ports[this.ports.length - 1];
    private int myPort = 9999;
    private InetSocketAddress myAddress = new InetSocketAddress("localhost", this.myPort);
    private ArrayList<ZooKeeperPeerServerImpl> servers;
    private List<Process> processes = new ArrayList<>();

    public Stage5DemoProcess() throws Exception {
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
        
        for (int i = 0; i < 20; i++) {
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
        Thread.sleep(20000);
        
        
        

        for (int i = 20; i < 40; i++) {
            
            String code = this.validClass.replace("world!", "world! from code version " + i);
            sendRequests(code);
            //sendMessage(code);
        }
        Thread.sleep(10000);

        stopOneServer(6);
        Thread.sleep(120000);
        printLeaders();

        for (int i = 40; i < 60; i++) {
            String code = this.validClass.replace("world!", "world! from code version " + i);
            sendRequests(code);
            //sendMessage(code);
        }

       // stopServers();
    }

    private void printLeaders() {
        for (ZooKeeperPeerServer server : this.servers) {
            Vote leader = server.getCurrentLeader();
            if (leader != null) {
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
    private void createProcessServers() throws IOException {
        for (Integer i = 0; i < 8; i++) {
            ProcessBuilder builder = new ProcessBuilder("java", "-cp", "target/classes", "edu.yu.cs.com3800.stage5.StartServer", i.toString());
            Process process = builder.inheritIO().start();
            processes.add(process);
            final int current = i;
            Runtime.getRuntime().addShutdownHook(new Thread(() -> processes.get(current).destroy()));
        }
    }
    

    private void sendRequests(String code){
        String url = "http://localhost:8000/compileandrun";

        try {
            // Create a URL object
            URL apiUrl = new URL(url);

            // Open a connection to the URL
            HttpURLConnection connection = (HttpURLConnection) apiUrl.openConnection();

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

            // Get the response code
            //here it is blocking because it is not properly sending to Round robin
            int responseCode = connection.getResponseCode();
            System.out.println("Response Code: " + responseCode);

            // Read the response content
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            StringBuilder responseContent = new StringBuilder();

            while ((line = reader.readLine()) != null) {
                responseContent.append(line);
            }
            reader.close();

            // Print the response content
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
        new Stage5Demo();
    }
    
}
*/