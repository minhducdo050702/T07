package com.example.openssldemo.database.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
@Database(entities = [Data::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun dataDao() : DataDao
    companion object {
        @Volatile
        private var INSTANCE : AppDatabase? = null
        fun getDatabase(context : Context) : AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context,
                    AppDatabase::class.java,
                    "app_database")
                    //.createFromAsset("databases/app_database.db")
                    .build()
                INSTANCE = instance
                instance

            }
        }
    }
}