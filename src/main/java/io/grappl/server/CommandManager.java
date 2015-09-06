package io.grappl.server;

import io.grappl.server.host.Host;
import io.grappl.server.logging.Log;

import java.util.List;
import java.util.Scanner;

public class CommandManager {

    private Relay relay;

    public CommandManager(Relay relay) {
        this.relay = relay;
    }

    public void executeCommand(String fullCommand) {
        String[] parts = fullCommand.split("\\s+");

        final String commandName = parts[0];

        if(commandName.equalsIgnoreCase("inspect")) {
            final int port = Integer.parseInt(parts[1]);

            Host host = relay.getHostByPort(port);
            Log.log(host.getHostSnapshot().toJson());
        }

        else if(commandName.equalsIgnoreCase("close")) {
            final int port = Integer.parseInt(parts[1]);

            Host host = relay.getHostByPort(port);
            host.closeHost();
        }

        else if(commandName.equalsIgnoreCase("hosts")) {
            Log.log(relay.getHostList().size() + " hosts open");
        }

        /* Begin imported old code */
        else if(commandName.equalsIgnoreCase("hostlist")) {
            List<Host> hosts = relay.getHostList();

            String output = hosts.size() + " host(s): ";

            for (int i = 0; i < hosts.size(); i++) {
                if(i != 0) {
                    output += " - ";
                }
                Host host = hosts.get(i);

                output += host.getControlSocket().getInetAddress().toString() + ":" + host.getPort();
            }

            Log.log(output);
        }
        /* End old code */

        else if(commandName.equalsIgnoreCase("quit")) {
            System.exit(0);
        }

        else {
            Log.log("Command not found");
        }
    }

    public void startCommandThread() {
        Thread commandThread = new Thread(new Runnable() {
            @Override
            public void run() {

                Scanner scanner = new Scanner(System.in);
                while(true) {
                    String line = scanner.nextLine();
                    executeCommand(line);
                }
            }
        });
        commandThread.start();
    }

    public Relay getRelay() {
        return relay;
    }
}
