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
    private String alias;

    private String AESkey;
    private final String iv = "2390172847198247190";


    static {
        System.loadLibrary("openssldemo");
    }

    public String convert_hex_string_to_ascii_string(String hex_string) {
        StringBuilder ascii_string = new StringBuilder();
        // Process the hex string in chunks of 2 characters
        for (int i = 0; i < hex_string.length(); i += 2) {
            // Extract 2-character hex chunk
            String hex_byte = hex_string.substring(i, i + 2);
            // Convert hex chunk to integer
            int charCode = Integer.parseInt(hex_byte, 16);
            // Convert integer to character and append
            ascii_string.append((char) charCode);
        }
        return ascii_string.toString();
    }
    public EncryptDecrypt(String alias, Context context) throws KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException, UnrecoverableKeyException {
        KeystoreController keystoreController = new KeystoreController(context);
        this.alias = alias;
        this.AESkey = convert_hex_string_to_ascii_string(keystoreController.getAESKey(alias));
        //this.iv = iv;
        Log.d("EncryptDecrypt", "Created ");
        Log.d("EncryptDecrypt", "AES Key: "+this.AESkey);
        Log.d("EncryptDecrypt", "IV: "+iv);
        Log.d("EncryptDecrypt", "Alias: "+alias);
    }
    public String encrypt(String plainData) {
        //this.iv = iv; ??
        String cipherText = encryptGCM(this.AESkey, this.iv, plainData);
        Log.d("EncryptDecrypt", "Encrypted "+cipherText);
        return cipherText;
    }
    public String decrypt(String cipherText) {
        //this.iv = iv; ??
        String plainData = decryptGCM(this.AESkey, this.iv, cipherText);
        Log.d("EncryptDecrypt", "Decrypted "+plainData);
        return plainData;
    }


    public native String encrypt(String key, String iv, String plainData);
    public native String decrypt(String key, String iv, String cipherText);

    public native String encryptGCM(String key, String iv, String plainData);
    public native String decryptGCM(String key, String iv, String cipherText);
}
