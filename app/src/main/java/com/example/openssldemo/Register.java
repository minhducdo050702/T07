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
    static {
        System.loadLibrary("openssldemo");
    }
    KeystoreController keystoreController;
    public Register() throws KeyStoreException, CertificateException, IOException, NoSuchAlgorithmException {
        keystoreController=new KeystoreController();
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
        String key=this.genKey();
        //divide the key into 3 parts 256 bits
        String masterKey=key.substring(0,256);
        String AESKey=key.substring(256,512);
        String MACKey=key.substring(512,768);
        String masterKeyHex=convert_binary_string_to_hex_string(masterKey);
        String AESKeyHex=convert_binary_string_to_hex_string(AESKey);
        String MACKeyHex=convert_binary_string_to_hex_string(MACKey);
        keystoreController.setMasterKey(alias,masterKeyHex);
        keystoreController.setAESKey(alias,AESKeyHex);
        keystoreController.setMACKey(alias,MACKeyHex);
        Log.d("Register","App registered successfully");
        String masterKey1=keystoreController.getMasterKey(alias);
        String AESKey1=keystoreController.getAESKey(alias);
        String MACKey1=keystoreController.getMACKey(alias);

        Log.d("Register","Master Key: "+masterKey1);
        Log.d("Register","AES Key: "+AESKey1);
        Log.d("Register","MAC Key: "+MACKey1);

    }
    private native String genKey();

}
