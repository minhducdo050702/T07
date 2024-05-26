package com.example.openssldemo.database.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters

@Database(entities = [Data::class, App::class, Log::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun dataDao() : DataDao
    abstract fun appDao() : AppDao
    abstract fun logDao() : LogDao
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

        fun getDbPath(context: Context) : String {
            return if(INSTANCE == null) {
                ""
            }else {
                context.getDatabasePath("app_database.db").absolutePath
            }
        }
    }
}