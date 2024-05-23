package com.example.openssldemo

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.lifecycle.asLiveData
import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.openssldemo.database.data.AppDao
import com.example.openssldemo.database.data.AppDatabase
import com.example.openssldemo.database.data.DataDao
import com.example.openssldemo.database.data.LogDao
import com.example.openssldemo.worker.CreateNewKeyWorker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.Calendar
import java.util.Date
import java.util.concurrent.TimeUnit


class MainService : Service() {

    companion object {
        const val TAG = "MainService"
        const val PACKAGE_ID = "packageID"
    }
    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + job)
    private lateinit var workerManager : WorkManager
    private lateinit var dataDao: DataDao
    private lateinit var appDao: AppDao
    private lateinit var logDao: LogDao
    private val binder = object : IMyAidlInterface.Stub() {
        override fun register(packageID: String?, image: String): String {
            if (packageID != null) {
                val keystoreController = KeystoreController(applicationContext)
                val isRegistered = keystoreController.isRegistered(packageID)
                Log.d(TAG, "REGISTER $packageID, $isRegistered")
                try {
                    if (isRegistered) {

                        val currentTime = Date()
                        logDao.insert(packageID,"REGISTER",currentTime, "Fail" )
                        return "You have already registered."
                    } else {
                        Log.d(TAG, "REGISTERING $packageID")
                        registerStoreData(packageID)
                        val rowId = appDao.insert(packageID, packageID, image)
                        Log.d(TAG, "APP ID $rowId")
                        val dataID = dataDao.insert(packageID, "packageID", packageID)
                        val currentTime = Date()
                        logDao.insert(packageID,  "REGISTER",currentTime, "Successful" )
                        scheduleWorker(15, TimeUnit.MINUTES, packageID)
                        return "You have registered successful."
                    }

                }catch (e: Exception) {
                    return e.message.toString()
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

                    val encryptedData = cryptoModule.encrypt(dataValue)
                    return runBlocking {
                        val listData = dataDao.getByType(dataType, packageID)
                        Log.d(TAG, "DATA SIZE: ${listData.size}")
                        if (listData.isEmpty()) {

                            dataDao.insert(packageID, dataType, encryptedData)
                            logDao.insert(packageID, "STORE", Calendar.getInstance().time, "Successful")
                             "You have stored data successful"
                        } else {
                            Log.d(TAG, "EXIST DATA")
                            logDao.insert(packageID, "STORE", Calendar.getInstance().time, "Failed")
                             "You have stored this type of data. If you want to change the value of data, please use edit request"

                        }
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
                    if(packageID != null) {
                        logDao.insert(packageID, "STORE",Calendar.getInstance().time, "Successful")
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
                    logDao.insert(packageID, "LOAD", Calendar.getInstance().time, "Failed")
                    return "You are not registered yet, please register to load data!"
                }

                return runBlocking {
                    val data = dataDao.getByType(dataType, packageID)
                    if(data.isEmpty()) {
                        logDao.insert(packageID, "STORE", Calendar.getInstance().time, "Failed")
                        "You have not stored this type of data"
                    }else {
                        Log.d(TAG, "LOAD DATA SIZE ${data.size}")
                        val cryptoModule = EncryptDecrypt(packageID, applicationContext)
                        val plaintext = cryptoModule.decrypt(data[0].dataValue)
                        logDao.insert(packageID, "LOAD", Calendar.getInstance().time, "Successful")
                        Log.d(TAG, "PLAINTEXT $plaintext")
                        plaintext
                    }


                }
            } else {
                if(packageID != null ) {
                    logDao.insert(packageID, "STORE", Calendar.getInstance().time, "Failed")
                }
               return "Requested data is missing!"
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder {
        dataDao = AppDatabase.getDatabase(applicationContext).dataDao()
        appDao = AppDatabase.getDatabase(applicationContext).appDao()
        logDao = AppDatabase.getDatabase(applicationContext).logDao()
        workerManager = WorkManager.getInstance(applicationContext)
        intent?.getStringExtra(PACKAGE_ID).let {
            Log.d(TAG, "BINDING TO ${it.toString()}")
        }

        return binder
    }


    private fun registerStoreData(packageID: String) {
        val keyManager = Register(applicationContext)
        keyManager.registerApp(packageID)
    }
    private fun scheduleWorker(
        duration: Long,
        unit: TimeUnit,
        appId: String
    ) {

        val data = Data.Builder()
            .putString(CreateNewKeyWorker.WORK_NAME, appId)
            .build()

        val workRequest = PeriodicWorkRequestBuilder<CreateNewKeyWorker>(duration, unit)
            .setInputData(data)
            .build()
        workerManager.enqueueUniquePeriodicWork(appId, ExistingPeriodicWorkPolicy.UPDATE, workRequest)


    }
}