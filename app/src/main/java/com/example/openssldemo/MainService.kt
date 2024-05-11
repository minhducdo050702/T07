package com.example.openssldemo

import android.app.Service
import android.content.Intent
import android.graphics.Color
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.openssldemo.database.data.AppDatabase
import com.example.openssldemo.database.data.Data
import com.example.openssldemo.database.data.DataDao
import java.io.FileInputStream
import java.io.FileOutputStream
import java.security.KeyStore
import kotlin.random.Random


class MainService: Service(){

    companion object {
        const val TAG = "MainService"
        const val PACKAGE_ID = "packageID"

    }
    private lateinit var dataDao: DataDao
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

        override fun store(packageID: String?, dataValue: String?, dataType: String?) {
            Log.d(TAG, "STORE DATA WITH($packageID, $dataValue, $dataType)" )
            if(dataType != null && packageID != null && dataValue != null) {
                val listData = dataDao.getByType(dataType ,packageID )
                Log.d(TAG, "DATA SIZE: ${listData.size}")
                if(listData.isEmpty()) {
                    dataDao.insert(packageID, dataType, dataValue )
                }else {
                    Log.d(TAG, "EXIST DATA")
                }

            }else {
                Log.d(TAG, "REQUIRE DATA")
            }
        }

        override fun load(packageID: String?, dataType: String?): String {
            if(packageID != null && dataType != null) {
                val data = dataDao.getByType(dataType,packageID)
                Log.d(TAG, "LOAD DATA SIZE ${data.size}")
                if(data.isNotEmpty()) {
                    return data[0].dataValue
                }
                return null.toString()

            }else {
                return null.toString()
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder {
        dataDao = AppDatabase.getDatabase(applicationContext).dataDao()
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