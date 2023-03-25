package com.ozaydin.todoapplication.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [ToDoModel::class], version = 1)
abstract class ToDoDatabase : RoomDatabase(){
    abstract fun toDoDao() : ToDoDao
    companion object{
        //Singleton
        @Volatile private var instance : ToDoDatabase? = null
        private val lock = Any()
        operator fun invoke(context : Context) = instance ?: synchronized(lock) {
            instance ?: makeDatabase(context).also {
                instance = it
            }
        }
        private fun makeDatabase(context : Context) = Room.databaseBuilder(
            context.applicationContext, ToDoDatabase::class.java,"to_do_database"
        ).build()
    }
}