/*package edu.yu.cs.com3800.stage5;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;

public class GateWayServerStarter {
    private static int[] ports = {8010, 8020, 8030, 8040, 8050, 8060, 8070, 8080};
    //private int[] ports = {8010, 8020};
    private int leaderPort = this.ports[this.ports.length - 1];
    
    public static void main(String[] args) throws IOException {
        HashMap<Long, InetSocketAddress> peerIDtoAddress = new HashMap<>(8);
        for (int i = 0; i < ports.length; i++) {
            peerIDtoAddress.put(Integer.valueOf(i).longValue(), new InetSocketAddress("localhost", this.ports[i]));
        }
                GatewayPeerServerImpl gps = new GatewayPeerServerImpl(ports[Integer.parseInt(args[0])], 0, entry.getKey(), map);
                GatewayServer gs = new GatewayServer(8000, gps);
                gps.start();
                gs.start();
    }
}
*/
