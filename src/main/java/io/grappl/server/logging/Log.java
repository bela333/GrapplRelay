package io.grappl.server.logging;

public class Log {

    public static boolean displayDetailed = true;

    public static void debug(String debug) {
        if(displayDetailed) log(debug);
    }

    public static void log(String log) {
        System.out.println(log);

    }
}
