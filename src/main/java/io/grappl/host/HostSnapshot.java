package io.grappl.host;

import io.grappl.Application;

public class HostSnapshot {

    private String address;
    private int port;
    private int clientConnected;

    public HostSnapshot(String address, int port, int clientConnected) {
        this.address = address;
        this.port = port;
        this.clientConnected = clientConnected;
    }

    public String toJson() {
        return Application.getGson().toJson(this);
    }
}
