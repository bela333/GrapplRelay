package io.grappl.server.host;

import io.grappl.server.Application;

public class HostSnapshot {

    private String address;
    private int port;
    private int clientConnected;
    private String user;

    public HostSnapshot(String user, String address, int port, int clientConnected) {
        this.user = user;
        this.address = address;
        this.port = port;
        this.clientConnected = clientConnected;
    }

    public String toJson() {
        return Application.getGson().toJson(this);
    }
}
