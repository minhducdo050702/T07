package com.example.openssldemo.worker

import android.content.Context
import android.util.Log
import androidx.lifecycle.asLiveData
import androidx.work.CoroutineWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.openssldemo.EncryptDecrypt
import com.example.openssldemo.KeystoreController
import com.example.openssldemo.Register
import com.example.openssldemo.database.data.AppDatabase
import kotlinx.coroutines.runBlocking


class CreateNewKeyWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {
    override suspend fun doWork(): Result {
        val database = AppDatabase.getDatabase(applicationContext)
        Log.d("WORKER", "doWork:")
        val dataDao = database.dataDao()

        val appId = inputData.getString(WORK_NAME)
        Log.d("WORKER", "appId: $appId ")
        appId?.let {
            val register = Register(applicationContext)
            ///register.registerApp(app.id)

            runBlocking {
                val dataList = dataDao.getAll(appId)
                Log.d("WORKER", dataList.size.toString())
                for (data in dataList) {
                    val cryptoModule = EncryptDecrypt(appId, applicationContext)
                    val plainText = cryptoModule.decrypt(data.dataValue)
                    data.dataValue = plainText
                    Log.d("WORKER","${data.dataValue} value")
                }

                register.registerApp(appId)

                for (data in dataList) {
                    val cryptoModule = EncryptDecrypt(appId, applicationContext)
                    val cipherText = cryptoModule.encrypt(data.dataValue)
                    data.dataValue = cipherText
                    dataDao.update(appId, data.dataType, data.dataValue)
                    Log.d("WORKER2","${data.dataValue} value")
                }


            }


        }
        return Result.success()

    }

    companion object {
        const val WORK_NAME = "CreateNewKeyWorker"
    }
}