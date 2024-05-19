package com.example.openssldemo;

import android.annotation.SuppressLint;
import android.content.Context;
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
    static {
        System.loadLibrary("openssldemo");
    }
    KeystoreController keystoreController;
    public Register(Context context) throws KeyStoreException, CertificateException, IOException, NoSuchAlgorithmException {
        keystoreController = new KeystoreController(context);
    }
    public String convert_binary_string_to_hex_string(String binary_string) {
        StringBuilder hex_string = new StringBuilder();
        for (int i = 0; i < binary_string.length(); i += 8) {
            String byte_string = binary_string.substring(i, i + 8);
            int charCode = Integer.parseInt(byte_string, 2);
            hex_string.append(String.format("%02x", charCode));
        }
        return hex_string.toString();
    }
    public void registerApp(String alias) throws KeyStoreException, UnrecoverableKeyException, NoSuchAlgorithmException {
        String key=this.genKey(keystoreController.getMasterKey());
        //divide the key into 3 parts 256 bits
        String AESKey=key;
        String AESKeyHex=convert_binary_string_to_hex_string(AESKey);
        keystoreController.setAESKey(alias,AESKeyHex);
        Log.d("Register","App registered successfully");
        String AESKey1= keystoreController.getAESKey(alias);
        Log.d("Register","AES Key: "+AESKey1);

    }
    //ham nay chi duoc goi 1 lan luc cai dat service
    public String genMasterKeyOnce() throws KeyStoreException {
        String masterKey = genMasterKey();
        keystoreController.setMasterKey(masterKey);
        return masterKey;
    }
    private native String genKey(String masterKey_);
    private native String genMasterKey();

}
