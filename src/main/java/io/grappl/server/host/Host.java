package io.grappl.server.host;

import com.google.gson.Gson;
import io.grappl.server.Application;
import io.grappl.server.Relay;
import io.grappl.server.host.exclient.ExClient;
import io.grappl.server.logging.Log;

import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Host {

    private HostData hostData;
    private Relay relay;
    private ServerSocket applicationSocket;
    private ServerSocket messageSocket;
    private boolean isOpen = false;
    private String associatedUser;
    private Socket controlSocket;
    private int port;
    private String user = "Anonymous";
    private long heartBeatTime;
    private List<ExClient> exClientList = new ArrayList<ExClient>();

    public Host(Relay relay, Socket authSocket, String associatedUser) {
        this.relay = relay;
        this.controlSocket = authSocket;
        this.associatedUser = associatedUser;
    }

    public Relay getRelay() {
        return relay;
    }

    public HostData getHostData() {
        return hostData;
    }

    public void openServer() {
        final Host host = this;

        port = getRelay().getPortAllocator().getPort(controlSocket.getInetAddress().toString());
        hostData = new HostData(associatedUser, controlSocket.getInetAddress().getHostAddress().toString(), port);

        PrintStream printStream = null;
        try {
            printStream = new PrintStream(getControlSocket().getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        printStream.println(port + "");
        final PrintStream theStream = printStream;

        try {
            // Initialize associated servers
            applicationSocket = new ServerSocket(port);
            messageSocket = new ServerSocket(port + 1);

            Log.debug("Host hosting @ [" + port + "|" + (port + 1) + "]");
            Log.debug(getHostSnapshot().toJson());

            isOpen = true;
            Thread watchingThread = new Thread(new Runnable() {
                @Override
                public void run() {
                try {
//                    System.out.println("started");

                    while(true) {
//                        System.out.println("In loop");
                        Socket socket = applicationSocket.accept();

                        ExClient exClient = new ExClient(host, socket);
                        theStream.println(socket.getInetAddress().toString());
                        exClientList.add(exClient);
                        exClient.start();
                    }
                } catch (Throwable e) {
//                    System.out.println("Exception fired");
                    closeHost();
                }
                }
            });
            watchingThread.start();

//            System.out.println("completely");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void closeHost() {
        if(isOpen) {
            isOpen = false;

            Log.debug("Closing server at " + getPort());

            try {
                applicationSocket.close();
                messageSocket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            getRelay().removeHost(this);
        }
    }

    public boolean isOpen() {
        return isOpen;
    }

    public String toJson() {
        Gson gson = Application.getGson();
        return gson.toJson(getHostData());
    }

    public int getPort() {
        return port;
    }

    public ServerSocket getApplicationSocket() {
        return applicationSocket;
    }

    public void beatHeart() {
        heartBeatTime = System.currentTimeMillis();
    }

    public void disassociate(ExClient exClient) {
        exClientList.remove(exClient);
    }

    public Socket getControlSocket() {
        return controlSocket;
    }

    public List<ExClient> getExClientList() {
        return exClientList;
    }

    public int getExClientCount() {
        return exClientList.size();
    }

    public HostSnapshot getHostSnapshot() {
        return new HostSnapshot(user, controlSocket.getInetAddress().getHostAddress(), getApplicationSocket().getLocalPort(), getExClientCount());
    }

    public ServerSocket getMessageSocket() {
        return messageSocket;
    }

    public String getAssociatedUser() {
        return associatedUser;
    }
}
