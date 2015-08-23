package io.grappl.server.core;

import io.grappl.server.Application;
import io.grappl.server.host.HostData;
import io.grappl.server.host.exclient.ExClientData;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.PrintStream;
import java.net.Socket;

public class CoreConnection {

    private Socket socket;
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;
    private PrintStream printStream;

    public CoreConnection(Socket socket) {
        this.socket = socket;

        System.out.println("Connected to core");

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

    public void clientConnected(ExClientData exClientData) {
        try {
            dataOutputStream.writeByte(3);
            printStream.println(Application.getGson().toJson(exClientData));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void clientDisconnected(ExClientData exClientData) {
        try {
            dataOutputStream.writeByte(4);
            printStream.println(Application.getGson().toJson(exClientData));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
