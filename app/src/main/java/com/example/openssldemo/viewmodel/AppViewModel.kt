package com.example.openssldemo.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.openssldemo.database.data.AppDao
import com.example.openssldemo.database.data.DataDao
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch

//


class AppViewModel(private val dataDao: DataDao,private  val appDao: AppDao) : ViewModel() {


    fun createNew(id: String, type: String, value: String) {
        insert(id,type,value)
    }

    private fun insert(id: String, type: String, value: String) {
        viewModelScope.launch {
            appDao.insert(id,type,value)

        }
    }



    class AppViewModelFactory(private val dataDao: DataDao, private val appDao: AppDao) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(AppViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return AppViewModel(dataDao, appDao) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}