package com.ozaydin.todoapplication.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity
data class TaskModel(
    var title:String? = null,
    var description:String? = null,
    var date: LocalDateTime? = null,
    var time: LocalDateTime? = null,
    var isDone: Boolean? = null,
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
)
/*
    category, color sections can be added to the database.
 */