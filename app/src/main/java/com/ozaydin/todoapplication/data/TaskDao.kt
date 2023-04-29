package com.ozaydin.todoapplication.data

import android.content.Context
import androidx.room.*
import com.ozaydin.todoapplication.repository.TaskRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Dao
interface TaskDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(task: Task)

    @Query("SELECT * FROM task")
    suspend fun getAllTasks(): List<Task>?

    @Query("SELECT * FROM task WHERE title LIKE :searchQuery OR description LIKE :searchQuery")
    suspend fun searchDatabase(searchQuery: String): List<Task>?

    @Delete
    suspend fun delete(task: Task)

   /* @Query("UPDATE task SET " +
            "title = :task.title , " +
            "description = :task.description , " +
            "date = :task.date , " +
            "time = :task.time , " +
            "isDone = :task.isDone , " +
            "priority = :task.priority , " +
            "alarmId = :task.alarmId , "+
            "WHERE id = :task.id; ")
    suspend fun update(task: Task)
*/
    @Update
   suspend fun update(task: Task)

}




