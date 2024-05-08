package com.example.myapplication2

import android.R.attr.password
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import java.io.FileInputStream
import java.io.FileOutputStream
import java.security.KeyStore


class MainService: Service(){

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
       Log.d("MainService", "Service started")
        if (intent != null && intent.hasExtra("id")) {
            // Retrieve the ID from the intent extras
            val id = intent.getIntExtra("id", -1)

            // Now you have the ID from the intent, you can use it as needed

            // For example, log the ID
            Log.d("MyService", "Received ID from intent: $id")
        }

        return START_STICKY
    }
    override fun onCreate() {
        Log.d("MainService", "Service created")
        val ks = KeyStore.getInstance(KeyStore.getDefaultType())
        ks.load(null, null)
        //save the keystore to a file in downloads directory
//        val password = "password"
//        FileOutputStream("/sdcard/Download/keystore.jks").use { fos -> ks.store(fos, password.toCharArray()) }
//        ks.load(FileInputStream("/sdcard/Download/keystore.jks"), password.toCharArray())
        //create a keystore
//        val keyStore = KeyStore.getInstance("AndroidKeyStore")
//        val pwdArray = "password".toCharArray()
//        keyStore.load(null)
//        FileOutputStream("newKeyStoreFileName2.jks").use { fos -> keyStore.store(fos, pwdArray) }
        super.onCreate()
        // Do something
    }



}