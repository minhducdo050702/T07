package com.example.openssldemo;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

public class EncryptDecrypt {


    private String AESkey;
    private final String iv = "2390172847198247190";


    static {
        System.loadLibrary("openssldemo");
    }

    public EncryptDecrypt(String alias, Context context) throws KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException, UnrecoverableKeyException {
        KeystoreController keystoreController = new KeystoreController(context);

        this.AESkey = keystoreController.getAESKey(alias);
        Log.d("EncryptDecrypt", "Created ");
        Log.d("EncryptDecrypt", "AES Key: "+this.AESkey);
        Log.d("EncryptDecrypt", "IV: "+iv);
        Log.d("EncryptDecrypt", "Alias: "+alias);
    }
    public String encrypt(String plainData) {

        String cipherText = encryptGCM(this.AESkey, this.iv, plainData);
        Log.d("EncryptDecrypt", "Encrypted "+cipherText);
        return cipherText;
    }
    public String decrypt(String cipherText) {
        Log.d("EncryptDecrypt", "AES: " + this.AESkey);
        String plainData = decryptGCM(this.AESkey, this.iv, cipherText);
        Log.d("EncryptDecrypt", "Decrypted "+ plainData);
        return plainData;
    }


    private native String encrypt(String key, String iv, String plainData);
    private native String decrypt(String key, String iv, String cipherText);

    private native String encryptGCM(String key, String iv, String plainData);
    private native String decryptGCM(String key, String iv, String cipherText);
}
