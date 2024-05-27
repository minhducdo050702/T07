package com.example.openssldemo.database.data

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow
import java.util.Date


@Dao
interface LogDao {

    @Transaction
    @Query("SELECT * FROM log")
    fun getAll(): Flow<List<LogWithAppAndData>>


    @Query("INSERT INTO log (app_id, `action`, log_date, status) VALUES (:appID, :ac, :logDate, :status)")
    fun insert(appID: String,  ac: String, logDate: Date, status : String)
}