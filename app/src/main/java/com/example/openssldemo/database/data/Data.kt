package com.example.openssldemo.database.data

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "data")
data class Data (
    @PrimaryKey(autoGenerate = true) val id : Int,
    @NonNull @ColumnInfo(name = "data_value") val dataValue : String,
    @NonNull @ColumnInfo(name = "data_type") val dataType : String,
    @NonNull @ColumnInfo(name = "app_id") val appId : String,
    @NonNull val mac : String
    ){
}