package io.grappl.port;

import java.net.ServerSocket;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class PortAllocator {

    private Set<Integer> occupiedPorts = new HashSet<Integer>();

    public int getPort() {
        int portNum = new Random().nextInt(60000);

        if(!isPortTaken(portNum) && !isPortTaken(portNum + 1)) {
            return portNum;
        } else {
            return getPort();
        }
    }

    public boolean isPortTaken(int port) {
        ServerSocket socket = null;

        try {
            socket = new ServerSocket(port);
        } catch (Exception e) {
            e.printStackTrace();

            try {
                socket.close();
            } catch (Exception f) {
                f.printStackTrace();
            }

            return true;
        }

        return occupiedPorts.contains(port);
    }
}
