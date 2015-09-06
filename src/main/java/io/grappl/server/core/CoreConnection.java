package io.grappl.server.core;

import io.grappl.server.Application;
import io.grappl.server.Relay;
import io.grappl.server.host.HostData;
import io.grappl.server.host.exclient.ExClientData;
import io.grappl.server.logging.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;

public class CoreConnection {

    private Socket socket;
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;
    private PrintStream printStream;

    private boolean isUp = false;

    private Relay relay;

    public CoreConnection(Relay relay, Socket socket) {
        this.socket = socket;
        this.relay = relay;

        isUp = true;
        Log.log("Connected to core");

        try {
            dataInputStream = new DataInputStream(socket.getInputStream());
            dataOutputStream = new DataOutputStream(socket.getOutputStream());
            printStream = new PrintStream(socket.getOutputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }

        Thread receptionThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    byte code = dataInputStream.readByte();
                    Log.log("Received message: code: " + code);

                    // Incoming auth message
                    if(code == 0) {
                        String message = dataInputStream.readLine();
                        String[] spl = message.split("\\s+");

                        String ip = spl[0];
                        String port = spl[1];
//                        String username = spl[2];

                        Log.log("Associating " + ip + " with " + port);
                        int thePort = Integer.parseInt(port);

                        getRelay().associate(ip, thePort);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        receptionThread.start();
    }

    public Socket getSocket() {
        return socket;
    }

    public void serverConnected(HostData hostData) {
        try {
            dataOutputStream.writeByte(1);
            printStream.println(Application.getGson().toJson(hostData));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void serverDisconnected(HostData hostData) {
        try {
            dataOutputStream.writeByte(2);
            printStream.println(Application.getGson().toJson(hostData));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void serverUpdate(HostData hostData) {
        try {
            dataOutputStream.writeByte(3);
            printStream.println(Application.getGson().toJson(hostData));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void clientConnected(ExClientData exClientData) {
        try {
            dataOutputStream.writeByte(4);
            printStream.println(Application.getGson().toJson(exClientData));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void clientDisconnected(ExClientData exClientData) {
        try {
            dataOutputStream.writeByte(5);
            printStream.println(Application.getGson().toJson(exClientData));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Relay getRelay() {
        return relay;
    }

    public boolean isUp() {
        return isUp;
    }
}
