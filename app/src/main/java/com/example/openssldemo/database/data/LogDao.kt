package com.example.openssldemo.database.data

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction


@Dao
interface LogDao {

    @Transaction
    @Query("SELECT * FROM log")
    fun getAll(): List<LogWithAppAndData>
}