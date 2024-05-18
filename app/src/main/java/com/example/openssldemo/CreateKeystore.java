package com.example.openssldemo;

import android.os.Build;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

public class CreateKeystore {
    KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
    private static final String KEYSTORE_FILE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Download/keystore.txt";
    String password = "password";
    static {
        System.loadLibrary("openssldemo");
    }

    public  CreateKeystore() throws KeyStoreException, CertificateException, IOException, NoSuchAlgorithmException {

        try {
            Log.d("create keystore", "Creating keystore");
            // Delete the keystore file if it exists
            File keystoreFile = new File(KEYSTORE_FILE_PATH);
            Log.d("create keystore", String.valueOf(keystoreFile.delete()));
            keyStore.load(null, password.toCharArray());
            Log.d("create keystore", "ccccc");
            // Store the keystore in the file system
            try (FileOutputStream fos = new FileOutputStream(KEYSTORE_FILE_PATH)) {
                keyStore.store(fos, password.toCharArray());
                Log.d("create keystore", "Keystore stored successfully");
            }
        } catch (Exception e) {
            Log.e("create keystore", "Error storing keystore: " + e.getMessage());
            e.printStackTrace();
        }



    }
}
