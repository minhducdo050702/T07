package com.example.openssldemo.database.data

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Ignore
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import java.util.Date


@Entity(tableName = "log", foreignKeys = [

    ForeignKey(
        entity = App::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("app_id"),
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE
    )
])

data class Log(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "log_id") val logID: Int,
    @ColumnInfo(name = "app_id", index = true)val appId : String,
    val action: String,
    @ColumnInfo(name = "log_date") val logDate: Date,
    val status: String
) {
}


class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long) : Date {
        return Date(value)
    }

    @TypeConverter
    fun dateToTimestamp(date: Date) : Long {
        return date.time
    }
}