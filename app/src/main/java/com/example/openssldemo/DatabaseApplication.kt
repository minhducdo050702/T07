package com.example.openssldemo

import android.app.Application
import com.example.openssldemo.database.data.AppDatabase

class DatabaseApplication : Application() {

    val database : AppDatabase by lazy { AppDatabase.getDatabase(this) }
}