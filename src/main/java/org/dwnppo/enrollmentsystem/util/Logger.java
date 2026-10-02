package org.dwnppo.enrollmentsystem.util;

public class Logger {
    private final String from;

    public Logger(String from) {
        this.from = from;
    }

    public void log(String message) {
        System.out.println("[" + from + "]: " + message);
    }
}