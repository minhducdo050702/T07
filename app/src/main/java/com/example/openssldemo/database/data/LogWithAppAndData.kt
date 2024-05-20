package com.example.openssldemo.database.data

import androidx.room.Embedded
import androidx.room.Relation

data class LogWithAppAndData(
    @Embedded
    val log: Log,
    @Relation(
        parentColumn = "id",
        entityColumn = "app_id"
    )
    val app: App,
    @Relation(
        parentColumn = "id",
        entityColumn = "data_id"
    )
    val data: Data
) {
}