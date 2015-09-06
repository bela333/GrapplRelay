package io.grappl.server.host;

import io.grappl.server.Application;

public class UserData {

    private int port;
    private String username;

    public UserData(int port, String username) {
        this.port = port;
        this.username = username;
    }

    public int getPort() {
        return port;
    }

    public String getUsername() {
        return username;
    }

    public String toJson() {
        return Application.getGson().toJson(this);
    }
}
