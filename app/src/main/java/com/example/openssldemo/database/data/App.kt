package com.example.openssldemo.database.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Blob


@Entity(tableName = "app")
data class App(
    @PrimaryKey val id: String,
    val name: String,
    val logo: String
) {
}