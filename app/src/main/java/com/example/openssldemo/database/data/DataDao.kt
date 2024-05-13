package com.example.openssldemo.database.data

import androidx.room.Dao
import androidx.room.Query


@Dao
interface DataDao {
    @Query("SELECT * FROM data WHERE app_id = :appId")
    fun getAll( appId : String): List<Data>
    @Query("SELECT * FROM data WHERE data_type = :dataType AND app_id = :appId")
    fun getByType(dataType: String, appId: String): List<Data>

    @Query("INSERT INTO data (app_id, data_type, data_value, mac) VALUES (:appId, :dataType, :dataValue, :mac)")
    fun insert(appId: String, dataType: String, dataValue: String, mac: String)

    @Query("DELETE FROM data WHERE app_id = :appId")
    fun deleteAll(appId: String)

    @Query("DELETE FROM data WHERE app_id = :appId AND data_type = :dataType")
    fun deleteByType(appId: String, dataType: String)

    @Query("UPDATE data SET data_value = :dataValue AND mac = :mac WHERE app_id = :appId AND data_type = :dataType")
    fun update(appId: String, dataType: String, dataValue: String, mac: String)

}