package com.example.openssldemo.database.data

import androidx.room.Embedded
import androidx.room.Relation

data class LogWithAppAndData(
    @Embedded
    val log: Log,
    @Relation(
        parentColumn = "app_id",
        entityColumn = "id"
    )
    val app: App

) {
}