package com.example.openssldemo.database.data

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Ignore
import androidx.room.PrimaryKey


@Entity(tableName = "log", foreignKeys = [
    ForeignKey(
        entity = Data::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("data_id"),
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE
    ),
    ForeignKey(
        entity = App::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("app_id"),
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE
    )
])

data class Log(
    @PrimaryKey @ColumnInfo(name = "log_id") val logID: Int,
    @ColumnInfo(name = "log_date") val logDate: String,
    val action: String,
    @ColumnInfo(name = "data_id")val dataId : Int,
    @ColumnInfo(name = "app_id")val appId : Int
) {
}