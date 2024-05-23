package com.example.openssldemo.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.openssldemo.database.data.App
import com.example.openssldemo.database.data.AppDao
import com.example.openssldemo.database.data.AppDatabase
import com.example.openssldemo.database.data.DataDao
import com.example.openssldemo.database.data.LogDao
import com.example.openssldemo.database.data.LogWithAppAndData
import com.example.openssldemo.worker.CreateNewKeyWorker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

//


class AppViewModel(
    private val application: Application,
    private val appDao: AppDao,
    private val logDao: LogDao
) : ViewModel() {


    val appList: LiveData<List<App>> = appDao.getAll().asLiveData()
    val logList: LiveData<List<LogWithAppAndData>> = logDao.getAll().asLiveData()
    private val workerManager = WorkManager.getInstance(application)
    //val dataList = AppDatabase.getDatabase(application.applicationContext).dataDao().getAll("com.example.clientapp").asLiveData()

    fun scheduleWorker(
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
//    suspend  fun insert( id : String,  name: String,  image: String) : Long {
//        val job = viewModelScope.async {
//            appDao.insert(id, name, image)
//        }
//
//        return job.await()
//    }


    class AppViewModelFactory(
        private val application: Application,
        private val appDao: AppDao,
        private val logDao: LogDao
    ) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(AppViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return AppViewModel(application, appDao, logDao) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}