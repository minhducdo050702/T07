package com.example.openssldemo.database.data

import androidx.room.Embedded
import androidx.room.Relation

data class AppWithData(
    @Embedded val app: App,
    @Relation(
        parentColumn = "id",
        entityColumn = "app_id"
    )
    val data: List<Data>
) {
}