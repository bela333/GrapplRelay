package io.grappl.core;

import java.util.UUID;

public class HostData {

    private UUID uuid;
    private String userHosting;
    private String hostAddress;
    private int portNum;
    private long timeStarted;

    public HostData(String userHosting, String hostAddress, int portNum) {
        this.uuid = UUID.randomUUID();
        this.userHosting = userHosting;
        this.hostAddress = hostAddress;
        this.portNum = portNum;

        timeStarted = System.currentTimeMillis();
    }

    public String getUserHosting() {
        return userHosting;
    }

    public String getHostAddress() {
        return hostAddress;
    }

    public int getPortNum() {
        return portNum;
    }

    public long getTimeStarted() {
        return timeStarted;
    }

    public UUID getUUID() {
        return uuid;
    }

    public long getTimeUp() {
        return System.currentTimeMillis() - timeStarted;
    }
}
