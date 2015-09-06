package io.grappl.server.port;

import io.grappl.server.Relay;

import java.net.ServerSocket;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class PortAllocator {

    private Set<Integer> occupiedPorts = new HashSet<Integer>();
    private Relay relay;

    public PortAllocator(Relay relay) {
        this.relay = relay;
    }

    public int getPort(String address) {
        if(relay.getAssociationMap().containsKey(address)) {
            int port = relay.getAssociationMap().get(address);

            if(port == -1) {
                int portNum = new Random().nextInt(60000);

                if(!isPortTaken(portNum) && !isPortTaken(portNum + 1)) {
                    return portNum;
                } else {
                    return getPort(address);
                }
            }

            return port;
        }

        int portNum = new Random().nextInt(60000);

        if(!isPortTaken(portNum) && !isPortTaken(portNum + 1)) {
            return portNum;
        } else {
            return getPort(address);
        }
    }

    public boolean isPortTaken(int port) {
        ServerSocket socket = null;

        try {
            socket = new ServerSocket(port);
        } catch (Exception e) {
            return true;
        }

        try {
            socket.close();
        } catch (Exception f) {
        }

        return occupiedPorts.contains(port);
    }
}
