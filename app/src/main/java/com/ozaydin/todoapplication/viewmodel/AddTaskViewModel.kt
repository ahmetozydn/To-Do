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

class DateTimePickerViewModel : ViewModel() {
    private var _toDoModel = MutableLiveData<ToDoModel>()
    val title: LiveData<ToDoModel> get() = _toDoModel

     fun insert(toDoModel: ToDoModel, context : Context) = viewModelScope.launch(Dispatchers.IO) {
        val db = ToDoDatabase.invoke(context)
         println("Task : $toDoModel")
        db.toDoDao().insert(toDoModel = toDoModel)
    }



}