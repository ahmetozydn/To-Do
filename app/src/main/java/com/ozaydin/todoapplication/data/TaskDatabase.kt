package com.ozaydin.todoapplication.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Task::class], version = 1)
abstract class TaskDatabase : RoomDatabase(){
    abstract fun taskDao() : TaskDao
   /* companion object{
        //Singleton
        @Volatile private var instance : TaskDatabase? = null
        private val lock = Any()
        operator fun invoke(context : Context) = instance ?: synchronized(lock) {
            instance ?: makeDatabase(context).also {
                instance = it
            }
        }
        private fun makeDatabase(context : Context) = Room.databaseBuilder(
            context.applicationContext, TaskDatabase::class.java,"task_database"
        ).build()
    }*/
}