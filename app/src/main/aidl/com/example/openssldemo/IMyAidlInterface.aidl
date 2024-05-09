// IMyAidlInterface.aidl
package com.example.openssldemo;

// Declare any non-default types here with import statements

interface IMyAidlInterface {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */

    void register(String packageID);
    int test(int x);
    String noti();
    int getColor();
}