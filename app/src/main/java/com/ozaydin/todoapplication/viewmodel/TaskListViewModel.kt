package com.ozaydin.todoapplication.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ozaydin.todoapplication.data.Task
import com.ozaydin.todoapplication.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject


@HiltViewModel
class TaskListViewModel @Inject constructor(private val taskRepository: TaskRepository) :
    ViewModel() {
    //var _taskList = mutableStateOf<List<Task>>(listOf()) // MutableState<List<Task>>
    //var taskList: MutableState<List<Task>> = _taskList
    //private var listToSearch: List<Task> = ArrayList()
    //private var anotherlist: MutableState<List<Task>> = mutableStateOf<List<Task>>(emptyList())

    private lateinit var listToSearch : List<Task>
    private var anotherlist = mutableStateOf<List<Task>>(listOf())
    var _taskList = mutableStateOf<List<Task>>(listOf()) //shapshotstatelist()
    //val taskList : List<Task> = _taskList

    var cryptoList = mutableStateOf<List<Task>>(listOf())
    /* var _taskList = mutableStateOf<List<Task>>(listOf())
     private var anotherlist =  mutableStateOf<List<Task>>(listOf())
     private var listToSearch =  mutableStateListOf<Task>()
     //val taskList: LiveData<List<Task>> = taskRepository.allTasks
     private var isSearchStarting = true*/

    /* private var listToSearch = mutableStateListOf<Task>()
   private var anotherlist = mutableStateListOf<Task>()
   var _taskList = mutableStateListOf<Task>()*/
    init {
        println("inside init block")
        fetchTasks()
    }

    fun fetchTasks() {
        viewModelScope.launch {
            println("inside fetchTasks() ********************")
            _taskList.value = taskRepository.getAllTasks()!!
            listToSearch = _taskList.value
        }
    }

    fun deleteTask(task: Task) {
        taskRepository.deleteTask(task)
    }


    fun searchList(query: String) {
        viewModelScope.launch(Dispatchers.Default) {
            println("inside searchlist() **************************")
            if (query.isEmpty()) {
                _taskList.value = taskRepository.getAllTasks()!!
                return@launch
            }

            val results = listToSearch.filter {
                it.title!!.contains(query.trim(), ignoreCase = true) || it.description!!.contains(
                    query.trim(),
                    ignoreCase = true
                )
            }
            _taskList.value = results
            // _taskList.toMutableStateList()

        }


/*
        viewModelScope.launch(Dispatchers.Default) {
            //val temporary = _taskList.value
            if(query.isEmpty()){
                _taskList.value = listToSearch
            }
            val results = listToSearch.filter {
                it.title!!.contains(query.trim(), ignoreCase = true) || it.description!!.contains(
                    query.trim(),
                    ignoreCase = true)
            }
            _taskList.value = results
        }
*/
        /*val listToSearch = if(isSearchStarting) {
            _taskList.value
        } else {
            initialCryptoList
        }
        viewModelScope.launch(Dispatchers.Default) {

            if (query.isEmpty()) {
                println("sout : empty query")
                 _taskList.value = initialCryptoList
                isSearchStarting = true
                return@launch
            }
            val results = listToSearch.filter {
                it.title!!.contains(query.trim(), ignoreCase = true) || it.description!!.contains(
                    query.trim(),
                    ignoreCase = true
                )
            }
            if(isSearchStarting) {
                initialCryptoList = _taskList.value
                isSearchStarting = false
            }
            println("sout : the result is: $results")
            _taskList.value = results
        }*/


    }

    fun saveTask(task: Task) {

        runBlocking {
            viewModelScope.launch(Dispatchers.Default) {
                taskRepository.addTask(task)
                anotherlist.value =
                    taskRepository.getAllTasks()!!// is coroutineScope should be used?
                _taskList.value.forEach {
                    println("viewModel: " + it.title + " ")
                }
            }
        }
    }
}




