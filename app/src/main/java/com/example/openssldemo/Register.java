package com.example.openssldemo;

import android.annotation.SuppressLint;
import android.os.Build;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class Register {
    KeyStore  keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
    String password = "password";

    static {
        Loader.load();
    }

    public Register() throws KeyStoreException, CertificateException, IOException, NoSuchAlgorithmException {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //load key store from file in downloads directory
            keyStore.load(new FileInputStream("/sdcard/Download/keystore.jks"), password.toCharArray());
            Log.d("Register","Keystore loaded successfully");
        }
    }
    public void registerApp(String alias) throws KeyStoreException, UnrecoverableKeyException, NoSuchAlgorithmException {
        String alias1=alias+" MasterKey";
        String alias2=alias+" AESKey";
        String alias3=alias+" MACKey";
        String key=this.genKey();
        //divide the key into 3 parts 256 bits
        String masterKey=key.substring(0,256);
        String AESKey=key.substring(256,512);
        String MACKey=key.substring(512,768);
        //store the keys in the keystore
//        SecretKey secretKey = new SecretKeySpec(masterKey.getBytes(), "AES");
//        KeyStore.SecretKeyEntry secret
//                = new KeyStore.SecretKeyEntry(secretKey);
//        KeyStore.ProtectionParameter password2
//                = new KeyStore.PasswordProtection("".toCharArray());
//        keyStore.setEntry("db-encryption-secret", secret, password2);
        SecretKey masterSecretKey = new SecretKeySpec(masterKey.getBytes(), "AES");
        SecretKey AESSecretKey = new SecretKeySpec(AESKey.getBytes(), "AES");
        SecretKey MACSecretKey = new SecretKeySpec(MACKey.getBytes(), "AES");
        KeyStore.SecretKeyEntry masterSecretEntry
                = new KeyStore.SecretKeyEntry(masterSecretKey);
        KeyStore.SecretKeyEntry AESSecretEntry
                = new KeyStore.SecretKeyEntry(AESSecretKey);
        KeyStore.SecretKeyEntry MACSecretEntry
                = new KeyStore.SecretKeyEntry(MACSecretKey);
        KeyStore.ProtectionParameter password_keystore
                = new KeyStore.PasswordProtection("".toCharArray());
        keyStore.setEntry(alias1, masterSecretEntry, password_keystore);
        keyStore.setEntry(alias2, AESSecretEntry, password_keystore);
        keyStore.setEntry(alias3, MACSecretEntry, password_keystore);

//        Key masterKey1=keyStore.getKey(alias1,"".toCharArray());
//        Key AESKey1=keyStore.getKey(alias2,"".toCharArray());
//        Key MACKey1=keyStore.getKey(alias3,"".toCharArray());
//
//        Log.d("Register",alias1+": "+new String(masterKey1.getEncoded()));
//        Log.d("Register",alias2+": "+new String(AESKey1.getEncoded()));
//        Log.d("Register",alias3+": "+new String(MACKey1.getEncoded()));
//        Log.d("Register","Master Key: "+masterKey);
//        Log.d("Register","AES Key: "+AESKey);
//        Log.d("Register","MAC Key: "+MACKey);

    }
    private native String genKey();

}
