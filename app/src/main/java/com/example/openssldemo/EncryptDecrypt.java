package com.example.openssldemo;

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
    private String alias;

    private String AESkey;
    private String iv;


    static {
        System.loadLibrary("openssldemo");
    }

    public EncryptDecrypt(String alias, String iv) throws KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException, UnrecoverableKeyException {
        KeystoreController keystoreController = new KeystoreController();
        this.alias = alias;
        this.AESkey = keystoreController.getAESKey(alias);
        this.iv = iv;
        Log.d("EncryptDecrypt", "Created ");
        Log.d("EncryptDecrypt", "AES Key: "+this.AESkey);
        Log.d("EncryptDecrypt", "IV: "+iv);
        Log.d("EncryptDecrypt", "Alias: "+alias);
    }
    public String encrypt(String plainData) {
        this.iv = iv;
        String cipherText = encrypt(this.AESkey, this.iv, plainData);
        Log.d("EncryptDecrypt", "Encrypted "+cipherText);
        return cipherText;
    }
    public String decrypt(String cipherText) {
        this.iv = iv;
        String plainData = decrypt(this.AESkey, this.iv, cipherText);
        Log.d("EncryptDecrypt", "Decrypted "+plainData);
        return plainData;
    }


    public native String encrypt(String key, String iv, String plainData);
    public native String decrypt(String key, String iv, String cipherText);
}
