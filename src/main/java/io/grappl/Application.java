package io.grappl;

import com.google.gson.Gson;

import java.io.IOException;
import java.net.Socket;

public class Application {

    private static Relay relay;
    private static Gson gson;

    public static void main(String[] args) {

        if(args.length == 1) {
            final String connectToCore = args[0];

            if (connectToCore.equalsIgnoreCase("-core")) {
                connectToCore();
            }
        }

        relay = new Relay();
    }

    public static void connectToCore() {
        Log.log("Connecting to core server...");

        try {
            Socket socket = new Socket("grappl.io", 44444);


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Gson getGson() {
        if(gson == null) gson = new Gson();

        return gson;
    }
}
