package io.grappl;

import io.grappl.port.PortAllocator;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Relay {

    private List<Host> hostList = new ArrayList<Host>();

    private ServerSocket serverSocket;
    private PortAllocator portAllocator;

    public Relay() {
        portAllocator = new PortAllocator();
    }

    public void openRelay() {
        try {
            serverSocket = new ServerSocket();

            while(true) {
                Socket connection = serverSocket.accept();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while(true) {
                            //TODO: handle connection
                        }
                    }
                }).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Closes relay. Does not close open tunnels.
     */
    public void closeRelay() {
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handleConnection(Socket connection) {

    }

    public List<Host> getHostList() {
        return hostList;
    }

    public PortAllocator getPortAllocator() {
        return portAllocator;
    }
}
