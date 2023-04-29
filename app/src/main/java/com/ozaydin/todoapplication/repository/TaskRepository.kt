package com.ozaydin.todoapplication.repository

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.room.Query
import com.ozaydin.todoapplication.data.TaskDao
import com.ozaydin.todoapplication.data.Task
import com.ozaydin.todoapplication.data.TaskDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.scopes.ActivityScoped
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@ActivityScoped  // my @Singleton
class TaskRepository @Inject constructor(private val db: TaskDatabase){

    val allTasks = MutableLiveData<List<Task>>()
    private val coroutineScope = CoroutineScope(Dispatchers.Main)
    fun addTask(item: Task) {
        coroutineScope.launch(Dispatchers.IO) {
            db.taskDao().insert(item)
        }
    }
    fun deleteTask(task: Task) = coroutineScope.launch(Dispatchers.IO) {
        db.taskDao().delete(task = task)
    }

    fun updateTask(task:Task) = coroutineScope.launch(Dispatchers.IO) { db.taskDao().update(task) }

     suspend fun getAllTasks() : List<Task>?{
        return db.taskDao().getAllTasks()
    }

     suspend fun search(query: String) : List<Task>? {
        return   db.taskDao().searchDatabase(query)
    }



}