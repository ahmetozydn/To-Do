package com.ozaydin.todoapplication.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ozaydin.todoapplication.data.ToDoDatabase
import com.ozaydin.todoapplication.data.ToDoModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.util.Date

class MainActivityViewModel :ViewModel(){
    private var _title = MutableLiveData<String>()
    val title: LiveData<String> get() = _title
    private var _description = MutableLiveData<String>()
    val description: LiveData<String> get() = _description
    private lateinit var db: ToDoDatabase



    suspend fun insertTask(title : String?, description : String?, date : LocalDateTime?, time : LocalDateTime?, context : Context) {
        val toDoDb = ToDoDatabase(context = context)
        val toDo = ToDoModel(title,description,null,null,false)
        viewModelScope.launch(Dispatchers.IO) {
            toDoDb.toDoDao().insert(toDo)
        }
    }

    suspend fun deleteTask() = viewModelScope.launch(Dispatchers.IO) {  }
    suspend fun readTasks() = viewModelScope.launch (Dispatchers.IO){  }

}