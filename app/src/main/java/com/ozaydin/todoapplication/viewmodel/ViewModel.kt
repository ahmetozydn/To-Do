package com.ozaydin.todoapplication.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ozaydin.todoapplication.data.Task
import com.ozaydin.todoapplication.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ViewModel @Inject constructor(private val taskRepository: TaskRepository) :
    ViewModel() {
    //var _taskList = mutableStateOf<List<Task>>(listOf()) // MutableState<List<Task>>
    //var taskList: MutableState<List<Task>> = _taskList
    //private var listToSearch: List<Task> = ArrayList()
    //private var anotherlist: MutableState<List<Task>> = mutableStateOf<List<Task>>(emptyList())
    private lateinit var listToSearch: List<Task>
    private var anotherlist = mutableStateOf<List<Task>>(listOf())
    var _taskList =
        mutableStateOf<List<Task>>(listOf()) //shapshotstatelist() // can be nullable and used to show empty message with this

    //val taskList : List<Task> = _taskList
    val filteredItems: State<List<Task>> = _taskList
    var isListEmpty = mutableStateOf<Boolean?>(value = null)//_taskList.value.isEmpty()
    var cryptoList = mutableStateOf<List<Task>>(listOf())
    var isSeachListEmpty = mutableStateOf<Boolean?>(null)

    var clickedTask = mutableStateOf<Task?>(null)
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
        //fetchTasks()
    }

    fun fetchTasks() {
        viewModelScope.launch {
            println("inside fetchTasks() ********************")
            _taskList.value = taskRepository.getAllTasks()!!
            listToSearch = _taskList.value
            isListEmpty.value = _taskList.value.isEmpty()
        }
    }

    fun deleteTask(task: Task) {
        taskRepository.deleteTask(task)
        //_taskList.value = _taskList.value.filter { it != task }
        _taskList.value = _taskList.value.minus(task)
        isListEmpty.value = _taskList.value.isEmpty()

    }


    fun searchList(query: String) {
        //listToSearch = _taskList.value
        viewModelScope.launch(Dispatchers.Default) {
            println("inside searchlist() **************************")
            if (query.isEmpty()) {
                _taskList.value = taskRepository.getAllTasks()!!
                isSeachListEmpty.value = false
                return@launch
            }
            val filteredList = taskRepository.getAllTasks()?.filter {
                it.title!!.contains(query.trim(), ignoreCase = true) || it.description!!.contains(
                    query.trim(),
                    ignoreCase = true
                )
            }
            isSeachListEmpty.value = filteredList?.isEmpty()
            if (filteredList != null) {
                _taskList.value = filteredList
            }
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
        viewModelScope.launch(Dispatchers.Default) {
            taskRepository.addTask(task)
            anotherlist.value =
                taskRepository.getAllTasks()!!// is coroutineScope should be used?
            _taskList.value = _taskList.value.plus(task)
            isListEmpty.value = false
        }
    }
    fun updateTask(task:Task) = viewModelScope.launch(Dispatchers.IO) { taskRepository.updateTask(task) }

    fun setClickedItem(task: Task?) {
        clickedTask.value = task
    }
}




