package com.ozaydin.todoapplication.data

import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

interface ToDoDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(toDoModel: ToDoModel)

    @Query("SELECT * FROM to_do_database")
    suspend fun getEntities() : List<ToDoModel>?
}