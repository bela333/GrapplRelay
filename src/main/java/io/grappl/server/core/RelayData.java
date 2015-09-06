package io.grappl.server.core;

import io.grappl.server.Application;

public class RelayData {
    private String name;
    private String subdomain;

    public RelayData(String name, String subdomain) {
        this.name = name;
        this.subdomain = subdomain;
    }

    public String toJson() {
        return Application.getGson().toJson(this);
    }
}
