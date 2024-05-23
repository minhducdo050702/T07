package com.example.openssldemo;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Arrays;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class KeystoreController {
    KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
    String path ;
   // String path = "/sdcard/Download/keystore.jks";
    String password = "password";
    static {
        System.loadLibrary("openssldemo");
    }
    //@SuppressLint("SdCardPath")
    public KeystoreController(Context context) throws KeyStoreException, CertificateException, IOException, NoSuchAlgorithmException {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            path =  ContextCompat.getExternalFilesDirs(context, null)[0].getAbsolutePath() + "/keystore.jks";
            //load key store from file in downloads directory
            //keyStore.load(new FileInputStream("/sdcard/Download/keystore.jks"), password.toCharArray());
            Log.d("AA", path);
            File f = new File(path);
            if(f.exists()) {
                keyStore.load(Files.newInputStream(Paths.get(path)), password.toCharArray());
                Log.d("Register","Keystore loaded successfully");
            }else {
                Log.d("Register","Keystore not found");
                keyStore.load(null, null);
            }

       }


    }
    public String getMasterKey() throws KeyStoreException, UnrecoverableKeyException, NoSuchAlgorithmException {
        String alias=" MasterKey";
        SecretKey masterKey = (SecretKey) keyStore.getKey(alias, "".toCharArray());
        if(masterKey == null) {
            return null;
        }else {
            return new String(masterKey.getEncoded());
        }

    }
    public String getAESKey(String alias) throws KeyStoreException, UnrecoverableKeyException, NoSuchAlgorithmException {
        String alias2=alias+" AESKey";
        SecretKey AESKey = (SecretKey) keyStore.getKey(alias2, "".toCharArray());
        return new String(AESKey.getEncoded());
    }


    public void setMasterKey(String key) throws KeyStoreException {
        String alias=" MasterKey";
        SecretKey masterSecretKey = new SecretKeySpec
                (key.getBytes(), "AES");
        KeyStore.SecretKeyEntry masterSecretEntry=
                new KeyStore.SecretKeyEntry(masterSecretKey);
        keyStore.setEntry(alias, masterSecretEntry, null);
        //save key store to file in downloads directory
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                keyStore.store(Files.newOutputStream(Paths.get(path)), password.toCharArray());
            }
        } catch (IOException | NoSuchAlgorithmException | CertificateException e) {
            e.printStackTrace();
        }
    }
    public void setAESKey(String alias, String key) throws KeyStoreException {
        String alias2=alias+" AESKey";
        SecretKey AESSecretKey = new SecretKeySpec
                (key.getBytes(), "AES");
        KeyStore.SecretKeyEntry AESSecretEntry=
                new KeyStore.SecretKeyEntry(AESSecretKey);
        keyStore.setEntry(alias2, AESSecretEntry, null);
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                keyStore.store(Files.newOutputStream(Paths.get(path)), password.toCharArray());
            }
        } catch (IOException | NoSuchAlgorithmException | CertificateException e) {
            e.printStackTrace();
        }
    }


    public boolean isRegistered(String alias) throws UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException {
        String alias1 = " MasterKey";
        String alias2 = alias + " AESKey";

        SecretKey masterKey = (SecretKey) keyStore.getKey(alias1, "".toCharArray());
        SecretKey AESKey = (SecretKey) keyStore.getKey(alias2, "".toCharArray());

        return masterKey != null && AESKey != null;
    }

}
