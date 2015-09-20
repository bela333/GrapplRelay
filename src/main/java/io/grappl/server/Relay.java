package io.grappl.server;

import io.grappl.server.core.CoreConnection;
import io.grappl.server.core.RelayData;
import io.grappl.server.host.Host;
import io.grappl.server.host.UserData;
import io.grappl.server.logging.Log;
import io.grappl.server.port.PortAllocator;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The {@code Relay} class represents a single relay server.
 * It contains methods to interact with the state of that
 * server, manage the server's connection with the core,
 * and manage all the Grappl hosts connected to this relay.
 */
public class Relay {

    /** A list of all Grappl hosts currently connected. */
    private List<Host> hostList = new ArrayList<Host>();
    private Map<InetAddress, Host> hostByAddress = new HashMap<InetAddress, Host>();
    private Map<Integer, Host> hostByPort = new HashMap<Integer, Host>();

    /** Server socket for relay control connections */
    private ServerSocket relayControlServer;

    /** Server socket for heartbeat connection */
    private ServerSocket heartBeatServer;

    /** A map of associations between IPs and ports. Used primarily for static ports. */
    private Map<String, Integer> associationMap = new HashMap<String, Integer>();

    private Map<InetAddress, UserData> userAssociations = new HashMap<InetAddress, UserData>();

    // The port allocator is the source of host's exposed ports
    private PortAllocator portAllocator;

    /** The process this relay is associated with */
    private Application application;

    /** The type of relay this is (private, or core integrated) */
    private RelayType relayType;

    public Relay(Application application, RelayType relayType) {
        this.application = application;
        this.relayType = relayType;
        portAllocator = new PortAllocator(this);
    }

    public RelayType getRelayType() {
        return relayType;
    }

    public Application getApplication() {
        return application;
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
                            Host host = new Host(relayServer, relayConnection, "Anonymous");
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

                            /* Start imported old code */
                            final Socket heartBeatClient = heartBeatServer.accept();
//                            System.out.println("accepted heartbeat conncetion");

                            new Thread(new Runnable() {
                                @Override
                                public void run() {

                                    final InetAddress server = heartBeatClient.getInetAddress();
                                    try {
                                        Thread.sleep(350);
                                        DataInputStream dataInputStream = new DataInputStream(heartBeatClient
                                            .getInputStream());

//                                        System.out.println("in");
                                        while(true) {
                                            int time = dataInputStream.readInt();

//                                            System.out.println(server + " hearbeat");
                                            hostByAddress.get(server).beatHeart();

                                            try {
                                                Thread.sleep(50);
                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    } catch (Exception e) {
                                        try {
                                            hostByAddress.get(server).closeHost();
                                        } catch (Exception ignore) {

                                        }
                                    }
                                }
                            }).start();
                            /* End imported old code */


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
        hostByPort.put(host.getApplicationSocket().getLocalPort(), host);

        if(getRelayType() == RelayType.CORE) {
            CoreConnection coreConnection = getApplication().getCoreConnection();
            coreConnection.serverConnected(host.getHostData());
        }
    }

    public void removeHost(Host host) {
        hostList.remove(host);
        hostByAddress.remove(host.getControlSocket().getInetAddress());
        hostByPort.remove(host.getApplicationSocket().getLocalPort());

        if(getRelayType() == RelayType.CORE) {
            CoreConnection coreConnection = getApplication().getCoreConnection();
            coreConnection.serverDisconnected(host.getHostData());
        }
    }

    public void associate(String ip, int port) {
        Log.log("Associating ip with port: " + port);

        try {
            InetAddress inetAddress = InetAddress.getByName(ip.substring(1, ip.length()));
            Host host = getHostByAddress(inetAddress);
            if (host != null) {
                host.closeHost();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        associationMap.put(ip, port);
    }

    public Host getHostByAddress(InetAddress inetAddress) {
        return hostByAddress.get(inetAddress);
    }

    public Host getHostByPort(int port) {
        return hostByPort.get(port);
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

    public Map<String, Integer> getAssociationMap() {
        return associationMap;
    }

    public RelayData getRelayData() {
        return new RelayData("nope", "avi");
    }
}
