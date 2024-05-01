package com.example.openssldemo;

import android.os.Build;
import android.util.Log;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

public class EncryptDecrypt {
    KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
    String password = "password";
    static {
        System.loadLibrary("openssldemo");
    }

    public EncryptDecrypt() throws KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //load key store from file in downloads directory
            keyStore.load(new FileInputStream("/sdcard/Download/keystore.jks"), password.toCharArray());
            Log.d("Register","Keystore loaded successfully");
        }
    }
    private native String encrypt(String alias, String data);
    private native String decrypt(String alias, String data);
}
