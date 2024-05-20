package com.example.openssldemo

import android.app.Service
import android.content.Intent
import android.graphics.Color
import android.graphics.YuvImage
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.openssldemo.MainService.Companion.PACKAGE_ID
import com.example.openssldemo.database.data.AppDao
import com.example.openssldemo.database.data.AppDatabase
import com.example.openssldemo.database.data.Data
import com.example.openssldemo.database.data.DataDao
import java.io.FileInputStream
import java.io.FileOutputStream
import java.security.KeyStore
import kotlin.random.Random


class MainService : Service() {

    companion object {
        const val TAG = "MainService"
        const val PACKAGE_ID = "packageID"
    }


    private lateinit var dataDao: DataDao
    private lateinit var appDao: AppDao
    private val binder = object : IMyAidlInterface.Stub() {
        override fun register(packageID: String?, image: String): String {
            if (packageID != null) {
                val keystoreController = KeystoreController(applicationContext)
                val isRegistered = keystoreController.isRegistered(packageID)
                //appDao.insert(packageID, packageID, image)
                Log.d(TAG, "REGISTER $packageID, $isRegistered")
                if (isRegistered) {
                    return "You have already registered."
                } else {
                    Log.d(TAG, "REGISTERING $packageID")
                    registerStoreData(packageID)
                    return "You have registered successful."
                }

            } else {
                Log.d(TAG, "packageID null")
                return "You must submit identification information to register"
            }

        }

        override fun store(packageID: String?, dataValue: String?, dataType: String?): String {
            Log.d(TAG, "STORE DATA REQUEST WITH($packageID, $dataValue, $dataType)")
            val keystoreController = KeystoreController(applicationContext)
            val isRegistered = keystoreController.isRegistered(packageID)
            if (!isRegistered) {
                return "You are not registered yet, please register to store data!"
            } else {
                if (dataType != null && packageID != null && dataValue != null) {

                    val cryptoModule = EncryptDecrypt(packageID, applicationContext)
                    val hMac = HMac(packageID , applicationContext)
                    val encryptedData = cryptoModule.encrypt(dataValue)
                    val hmac_data = hMac.genHmac(dataValue)

                    val listData = dataDao.getByType(dataType, packageID)
                    Log.d(TAG, "DATA SIZE: ${listData.size}")
                    if (listData.isEmpty()) {
                         //dataDao.insert(packageID, dataType, encryptedData, hmac_data)
                        return "You have stored data successful"
                    } else {
                        Log.d(TAG, "EXIST DATA")
                        return "You have stored this type of data. If you want to change the value of data, please use edit request"

                    }

                } else {
                    Log.d(TAG, "REQUIRE DATA")
                    var responseMessage = "Requested data is missing "
                    if (dataType == null) {
                        responseMessage += "data type, "
                    }
                    if (packageID == null) {
                        responseMessage += "identification information, "
                    }
                    if (dataValue == null) {
                        responseMessage += "data value"
                    }
                    return responseMessage
                }
            }


        }

        override fun load(packageID: String?, dataType: String?): String {
            if (packageID != null && dataType != null) {
                val keystoreController = KeystoreController(applicationContext)
                val isRegistered = keystoreController.isRegistered(packageID)
                if (!isRegistered) {
                    return "You are not registered yet, please register to load data!"
                }

                val data = dataDao.getByType(dataType, packageID)
                if(data.isEmpty()) {
                    return "You have not stored this type of data"
                }

                Log.d(TAG, "LOAD DATA SIZE ${data.size}")
                val cryptoModule = EncryptDecrypt(packageID, applicationContext)
                val plaintext = cryptoModule.decrypt(data[0].dataValue)
                Log.d(TAG, "PLAINTEXT $plaintext")
                val hMac = HMac(packageID , applicationContext)
                val hmac_data = hMac.genHmac(plaintext)

                if(hmac_data != data[0].mac)  {
                    return "Data is corrupted"
                }
                return plaintext
            } else {
               return "Requested data is missing!"
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder {
        dataDao = AppDatabase.getDatabase(applicationContext).dataDao()
        appDao = AppDatabase.getDatabase(applicationContext).appDao()
        intent?.getStringExtra(PACKAGE_ID).let {
            Log.d(TAG, "BINDING TO ${it.toString()}")
        }

        return binder
    }


    private fun registerStoreData(packageID: String) {
        val keyManager = Register(applicationContext)
        keyManager.registerApp(packageID)
    }

}