package com.example.openssldemo;
import android.os.Build;
import android.util.Log;
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
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class KeystoreController {
    KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
    String password = "password";
    static {
        System.loadLibrary("openssldemo");
    }
    public KeystoreController() throws KeyStoreException, CertificateException, IOException, NoSuchAlgorithmException {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //load key store from file in downloads directory
            keyStore.load(new FileInputStream("/sdcard/Download/keystore.jks"), password.toCharArray());
            Log.d("Register","Keystore loaded successfully");
        }


    }
    public String getMasterKey(String alias) throws KeyStoreException, UnrecoverableKeyException, NoSuchAlgorithmException {
        String alias1=alias+" MasterKey";
        SecretKey masterKey = (SecretKey) keyStore.getKey(alias1, "".toCharArray());
        return new String(masterKey.getEncoded());
    }
    public String getAESKey(String alias) throws KeyStoreException, UnrecoverableKeyException, NoSuchAlgorithmException {
        String alias2=alias+" AESKey";
        SecretKey AESKey = (SecretKey) keyStore.getKey(alias2, "".toCharArray());
        return new String(AESKey.getEncoded());
    }
    public String getMACKey(String alias) throws KeyStoreException, UnrecoverableKeyException, NoSuchAlgorithmException {
        String alias3=alias+" MACKey";
        SecretKey MACKey = (SecretKey) keyStore.getKey(alias3, "".toCharArray());
        return new String(MACKey.getEncoded());
    }
    public void setMasterKey(String alias, String key) throws KeyStoreException {
        String alias1=alias+" MasterKey";
        SecretKey masterSecretKey = new SecretKeySpec
                (key.getBytes(), "AES");
        KeyStore.SecretKeyEntry masterSecretEntry=
                new KeyStore.SecretKeyEntry(masterSecretKey);
        keyStore.setEntry(alias1, masterSecretEntry, null);
        //save key store to file in downloads directory
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                keyStore.store(Files.newOutputStream(Paths.get("/sdcard/Download/keystore.jks")), password.toCharArray());
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
                keyStore.store(Files.newOutputStream(Paths.get("/sdcard/Download/keystore.jks")), password.toCharArray());
            }
        } catch (IOException | NoSuchAlgorithmException | CertificateException e) {
            e.printStackTrace();
        }
    }

    public void setMACKey(String alias, String key) throws KeyStoreException {
        String alias3=alias+" MACKey";
        SecretKey MACSecretKey = new SecretKeySpec
                (key.getBytes(), "AES");
        KeyStore.SecretKeyEntry MACSecretEntry=
                new KeyStore.SecretKeyEntry(MACSecretKey);
        keyStore.setEntry(alias3, MACSecretEntry, null);
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                keyStore.store(Files.newOutputStream(Paths.get("/sdcard/Download/keystore.jks")), password.toCharArray());
            }
        } catch (IOException | NoSuchAlgorithmException | CertificateException e) {
            e.printStackTrace();
        }
    }

}
