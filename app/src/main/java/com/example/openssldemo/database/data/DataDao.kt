package com.example.openssldemo.database.data

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow


@Dao
interface DataDao {
    @Query("SELECT * FROM data WHERE app_id = :appId AND data_type != 'packageID' ")
    suspend fun getAll( appId : String): List<Data>

    @Query("SELECT * FROM data WHERE data_type = :dataType AND app_id = :appId")
    suspend fun getByType(dataType: String, appId: String): List<Data>

    @Query("INSERT INTO data (app_id, data_type, data_value) VALUES (:appId, :dataType, :dataValue)")
     fun insert(appId: String, dataType: String, dataValue: String) : Long

    @Query("DELETE FROM data WHERE app_id = :appId")
    suspend fun deleteAll(appId: String)

    @Query("DELETE FROM data WHERE app_id = :appId AND data_type = :dataType")
    suspend fun deleteByType(appId: String, dataType: String)

    @Query("UPDATE data SET data_value = :dataValue WHERE app_id = :appId AND data_type = :dataType")
    suspend fun update(appId: String, dataType: String, dataValue: String)

}