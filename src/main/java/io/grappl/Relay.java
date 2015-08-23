package io.grappl;

import io.grappl.host.Host;
import io.grappl.logging.Log;
import io.grappl.port.PortAllocator;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Relay {

    private List<Host> hostList = new ArrayList<Host>();

    private ServerSocket relayControlServer;
    private ServerSocket heartBeatServer;

    private Map<InetAddress, Host> hostByAddress = new HashMap<InetAddress, Host>();

    // The port allocator is the source of host's exposed ports
    private PortAllocator portAllocator;

    public Relay() {
        portAllocator = new PortAllocator();
    }

    /**
     * Creates two servers, the message server (25564) and the heartbeat server (25570).
     *
     * The message server receives incoming requests from Grappl clients (hosts).
     * The heartbeat server is used to handle heartbeat connections between this relay and various clients.
     */
    public void openRelay() {
        final Relay relayServer = this;

        try {
            relayControlServer = new ServerSocket(Globals.MESSAGING_PORT);
            Log.log("Started messaging server @ " + Globals.MESSAGING_PORT);
            heartBeatServer = new ServerSocket(Globals.HEARTBEAT_PORT);
            Log.log("Started heartbeat server @ " + Globals.HEARTBEAT_PORT);

            Thread relayListener = new Thread(new Runnable() {
                @Override
                public void run() {
                    while(true) {
                        try {
                            Socket relayConnection = relayControlServer.accept();
                            Host host = new Host(relayServer, relayConnection);
                            host.openServer();
                            addHost(host);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            relayListener.start();

            Thread heartBeatListener = new Thread(new Runnable() {
                @Override
                public void run() {
                    while(true) {
                        try {
                            Socket relayConnection = heartBeatServer.accept();
                            //TODO: handle heartbeat
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            heartBeatListener.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Closes relay. Does not close open tunnels.
     */
    public void closeRelay() {
        try {
            relayControlServer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addHost(Host host) {
        hostList.add(host);
        hostByAddress.put(host.getControlSocket().getInetAddress(), host);
    }

    public void removeHost(Host host) {
        hostList.remove(host);
        hostByAddress.remove(host.getControlSocket().getInetAddress(), host);
    }

    public Host getHostByAddress(InetAddress inetAddress) {
        return hostByAddress.get(inetAddress);
    }

    public List<Host> getHostList() {
        return hostList;
    }

    public PortAllocator getPortAllocator() {
        return portAllocator;
    }

    public ServerSocket getRelayControlServer() {
        return relayControlServer;
    }

    public ServerSocket getHeartBeatServer() {
        return heartBeatServer;
    }
}
