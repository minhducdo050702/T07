package com.example.openssldemo;

import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

public class HMac {
    public String alias;
    public String MACkey;
    static {
        System.loadLibrary("openssldemo");
    }
    public HMac(String alias, Context context) throws CertificateException, KeyStoreException, IOException, NoSuchAlgorithmException, UnrecoverableKeyException {
        KeystoreController keystoreController = new KeystoreController(context);
        this.alias = alias;
        this.MACkey = keystoreController.getMACKey(alias);
        Log.d("HMac", "Created "+this.MACkey);
    }
    public String genHmac(String data) {
        Log.d("HMac", "asdjiasjda");
        String hmac = genHmac(data, this.MACkey);
        Log.d("HMac", "Generated:"+hmac);
        return hmac;
    }
    public int verifyHmac(String data, String hmac) {
        int result = verifyHmac(data, this.MACkey, hmac);
        Log.d("HMac", "Verified:"+result);
        return result;
    }
    public native String genHmac(String data, String key);
    public native int verifyHmac(String data,String key, String hmac);

}
