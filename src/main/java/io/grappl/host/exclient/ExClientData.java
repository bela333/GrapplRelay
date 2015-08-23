package io.grappl.host.exclient;

import java.util.UUID;

public class ExClientData {

    private UUID uuid;
    private String userHosting;
    private String hostAddress;
    private int portNum;
    private long timeStarted;
    private boolean isOpen;
    private long timeOpenFor = 0;

    public ExClientData(String userHosting, String hostAddress, int portNum) {
        this.uuid = UUID.randomUUID();
        this.userHosting = userHosting;
        this.hostAddress = hostAddress;
        this.portNum = portNum;

        isOpen = true;

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

    public void close() {
        timeOpenFor = getTimeUp();

        isOpen = false;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public long getTimeUp() {
        if(isOpen) {
            return System.currentTimeMillis() - timeStarted;
        } else {
            return timeOpenFor;
        }
    }
}
