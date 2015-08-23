package io.grappl;

import com.google.gson.Gson;
import io.grappl.core.HostData;

public class Host {

    private HostData hostData;

    public HostData getHostData() {
        return hostData;
    }

    public String toJson() {
        Gson gson = Application.getGson();
        return gson.toJson(getHostData());
    }
}
