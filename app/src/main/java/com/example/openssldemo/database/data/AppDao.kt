package com.example.openssldemo.database.data

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface AppDao {
    @Query("SELECT * FROM app")
    fun getAll(): List<App>


    @Query("SELECT * FROM app WHERE id = :id")
    fun getAppByID(id: Int): App
    @Transaction
    @Query("SELECT * FROM app WHERE id = :id")
    fun getAppData(id: Int) : List<AppWithData>
}