package com.example.openssldemo.database.data

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {
    @Query("SELECT * FROM app")
    fun getAll(): Flow<List<App>>

    @Query("INSERT INTO app (id, name ,logo) VALUES (:id, :name, :logo)")
    fun insert(id: String, name: String, logo: String) : Long

    @Query("SELECT * FROM app WHERE id = :id")
    fun getAppByID(id: Int): App
    @Transaction
    @Query("SELECT * FROM app WHERE id = :id")
    fun getAppData(id: Int) : Flow<List<AppWithData>>
}