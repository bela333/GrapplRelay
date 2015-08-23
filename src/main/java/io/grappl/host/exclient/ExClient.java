package io.grappl.host.exclient;

import java.io.*;
import java.net.Socket;

public class ExClient {

    private Socket socket;
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;
    private PrintStream printStream;
    private boolean isClosed;
    private ExClientData exClientData;

    public ExClient(Socket socket) {
        this.socket = socket;

        try {
            dataInputStream = new DataInputStream(socket.getInputStream());
            dataOutputStream = new DataOutputStream(socket.getOutputStream());
            printStream = new PrintStream(socket.getOutputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void start() {
    }

    public void close() {
        isClosed = true;
    }

    /**
     * Determines if the connection is still alive or not
     */
    public boolean ping() {
        try {
            try {
                dataOutputStream.writeByte(0);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public boolean isClosed() {
        return isClosed;
    }

    public Socket getSocket() {
        return socket;
    }
}
