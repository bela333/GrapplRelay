package io.grappl.server.logging;

import javax.swing.*;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;

public class Log {

    public static boolean displayDetailed = true;

    public static void debug(String debug) {
        if(displayDetailed) log(debug);
    }

    public static void log(String toBeLogged) {
        String tag = DateFormat.getDateTimeInstance().format(new Date(System.currentTimeMillis()));
        String theS = "[" + tag + "] " + toBeLogged;
        System.out.println(theS);
    }
}
