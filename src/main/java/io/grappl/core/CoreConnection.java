package io.grappl.core;

import io.grappl.Host;

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

    public CoreConnection() {
        socket = new Socket();

        try {
            dataInputStream = new DataInputStream(socket.getInputStream());
            dataOutputStream = new DataOutputStream(socket.getOutputStream());
            printStream = new PrintStream(socket.getOutputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Socket getSocket() {
        return socket;
    }

    public void updateStats(Host host) {
        try {
            dataOutputStream.writeByte(1);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void serverConnected(HostData hostData) {

    }

    public void serverDisconnected(HostData hostData) {

    }

    public void clientConnected() {

    }

    public void clientDisconnected() {

    }
}
