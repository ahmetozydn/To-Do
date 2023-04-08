package com.ozaydin.todoapplication.viewmodel

import android.content.Context
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ozaydin.todoapplication.data.TaskDatabase
import com.ozaydin.todoapplication.data.Task
import com.ozaydin.todoapplication.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddTaskViewModel@Inject  constructor(private val taskRepository: TaskRepository) : ViewModel() {
    private var _toDoModel = mutableStateOf<List<Task>>(listOf())
    val title: MutableState<List<Task>> get() = _toDoModel




      fun saveTask(task: Task) {
          viewModelScope.launch (Dispatchers.Default){
              taskRepository.addTask(task)        // is coroutineScope should be used?

          }
     }

}
/*
val listDataState = MutableState<List<Data>> =  mutableStateOf(emptyList<Data>())

//initialize the viewmodel
init {

    viewModelScope.launch {
        val data = fetchData()
        listDataState.value = data

    }
}

suspend fun fetchData() : List<Data>{
    //something like:
    return dataRepository.getData()
}*/
