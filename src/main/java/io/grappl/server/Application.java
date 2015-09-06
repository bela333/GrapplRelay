package io.grappl.server;

import com.google.gson.Gson;
import io.grappl.server.core.CoreConnection;
import io.grappl.server.logging.Log;

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
        relay = new Relay(this, RelayType.CORE);

        CommandManager commandManager = new CommandManager(relay);
        commandManager.startCommandThread();

        relay.openRelay();
    }

    public Relay getRelay() {
        return relay;
    }

    public void connectToCore() {
        Log.log("Connecting to core server...");

        try {
            Socket socket = new Socket(Globals.CORE_SERVER_LOC, Globals.RELAY_CONTROL_PORT);
            coreConnection = new CoreConnection(getRelay(), socket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public CoreConnection getCoreConnection() {
        return coreConnection;
    }
}
