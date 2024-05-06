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
    //KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
    private String password;
    private String plainData;
    private String key;
    private String iv;
    private String cipherText;

    static {
        Loader.load();
    }

    public EncryptDecrypt(String password, String plainData, String key, String iv, String cipherText) throws KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            //load key store from file in downloads directory
//            keyStore.load(new FileInputStream("/sdcard/Download/keystore.jks"), password.toCharArray());
//            Log.d("Register", "Keystore loaded successfully");
//        }
        this.password = password;
        this.plainData = plainData;
        this.key = key;
        this.iv = iv;
        this.cipherText = cipherText;
    }

    public EncryptDecrypt(String plainData, String key, String iv) throws KeyStoreException {
        this.plainData = plainData;
        this.key = key;
        this.iv = iv;
    }

    public EncryptDecrypt() throws KeyStoreException {
        super();
    }

    public native String encrypt(String key, String iv, String plainText);
    public native String decrypt(String key, String iv, String cipherText);
}
