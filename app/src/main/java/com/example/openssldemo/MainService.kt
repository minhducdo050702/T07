package com.example.openssldemo

import android.app.Service
import android.content.Intent
import android.graphics.Color
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import java.io.FileInputStream
import java.io.FileOutputStream
import java.security.KeyStore
import kotlin.random.Random


class MainService: Service(){

    companion object {
        const val TAG = "MainService"
        const val PACKAGE_ID = "packageID"
        const val REGISTER_ACTION = "com.example.openssldemo.REGISTER"
        const val STORAGE_ACTION = "com.example.openssldemo.STORAGE"
        const val LOAD_ACTION = "com.example.openssldemo.LOAD"
    }
    private val binder = object : IMyAidlInterface.Stub() {
        override fun register(packageID: String?) {
           if(packageID != null) {
               Log.d(TAG, "REGISTERING $packageID")
               registerStoreData(packageID)
           }else {
               Log.d(TAG, "REGISTERING NULL")
           }
        }

        override fun test(x: Int): Int {
            return x * 2
        }
        override fun noti() : String {
            return "HELLO"
        }
        override fun getColor() : Int {
            val rd = Random(1)
            val color = Color.argb(255, rd.nextInt(256), rd.nextInt(256), rd.nextInt(256))
            Log.d(TAG, "getColor: $color");
            return color
        }
    }

    override fun onBind(intent: Intent?): IBinder {

        intent?.getStringExtra(PACKAGE_ID).let {
            Log.d(TAG, "BINDING TO ${it.toString()}")
        }
        return binder
    }


    private fun registerStoreData(packageID : String) {
        val keyManager = Register()
        keyManager.registerApp(packageID)
    }

}