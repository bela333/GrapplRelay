package io.grappl.host;

import com.google.gson.Gson;
import io.grappl.Application;
import io.grappl.Relay;
import io.grappl.core.HostData;
import io.grappl.host.exclient.ExClient;

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
    private List<ExClient> exClientList = new ArrayList<ExClient>();

    public Host(Relay relay, Socket authSocket) {
        this.relay = relay;
        this.controlSocket = authSocket;
    }

    public Relay getRelay() {
        return relay;
    }

    public HostData getHostData() {
        return hostData;
    }

    public void openServer() {
        final Host host = this;

        int port = getRelay().getPortAllocator().getPort();
        hostData = new HostData(associatedUser, controlSocket.getInetAddress().getHostAddress().toString(), port);

        PrintStream printStream = null;
        try {
            printStream = new PrintStream(getControlSocket().getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        printStream.println(port +"");
        final PrintStream theStream = printStream;

        try {
            // Initialize associated servers
            applicationSocket = new ServerSocket(port);
            messageSocket = new ServerSocket(port + 1);

            System.out.println("Client connected @ " + port);

            Thread watchingThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while(true) {
                        try {
                            Socket socket = applicationSocket.accept();

                            ExClient exClient = new ExClient(host, socket);
                            theStream.println(socket.getInetAddress().toString());
                            exClient.start();
                            exClientList.add(exClient);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            watchingThread.start();

            isOpen = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void closeServer() {
        try {
            applicationSocket.close();
            messageSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        getRelay().removeHost(this);

        isOpen = false;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public String toJson() {
        Gson gson = Application.getGson();
        return gson.toJson(getHostData());
    }

    public Socket getControlSocket() {
        return controlSocket;
    }

    public ServerSocket getMessageSocket() {
        return messageSocket;
    }
}
