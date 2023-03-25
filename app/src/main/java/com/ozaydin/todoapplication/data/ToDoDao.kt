package com.ozaydin.todoapplication.data

import androidx.room.Insert
import androidx.room.Query

interface ToDoDao {
    @Insert
    suspend fun insert(toDoModel: ToDoModel)

    @Query("SELECT * FROM to_do_database")
    suspend fun getEntities() : List<ToDoModel>?
}