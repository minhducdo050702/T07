package com.example.openssldemo;

class Loader {
    private static boolean done = false;

    protected static synchronized void load() {
        if (done)
            return;

        System.loadLibrary("library_name");

        done = true;
    }
}