package io.grappl;

import com.google.gson.Gson;
import io.grappl.core.CoreConnection;
import io.grappl.logging.Log;

import java.io.IOException;
import java.net.Socket;

public class Application {

    private Relay relay;
    private CoreConnection coreConnection;

    private static Gson gson;

    public static void main(String[] args) {

        Application application = new Application();

        if(args.length == 1) {
            final String connectToCore = args[0];

            if (connectToCore.equalsIgnoreCase("-core")) {
                application.connectToCore();
            }
        }
    }

    public static Gson getGson() {
        if(gson == null) gson = new Gson();

        return gson;
    }

    public Application() {
        relay = new Relay();
        relay.openRelay();
    }

    public Relay getRelay() {
        return relay;
    }

    public void connectToCore() {
        Log.log("Connecting to core server...");

        try {
            Socket socket = new Socket("grappl.io", 44444);
            coreConnection = new CoreConnection(socket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public CoreConnection getCoreConnection() {
        return coreConnection;
    }
}
