package com.ozaydin.todoapplication.di

import android.content.Context
import androidx.room.Room
import com.ozaydin.todoapplication.data.TaskDao
import com.ozaydin.todoapplication.data.TaskDatabase
import com.ozaydin.todoapplication.repository.TaskRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object AppModule {
    @Provides
    fun provideChannelDao(db: TaskDatabase): TaskDao {
        return db.taskDao()
    }
/*    @ApplicationContext
    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context) = Room.databaseBuilder(context = context,
        TaskDatabase::class.java,
        "task_database").build()*/
    @Provides
    @Singleton // ensure only one instance would be created
    fun provideAppDatabase(@ApplicationContext appContext: Context): TaskDatabase {
        return Room.databaseBuilder(
            appContext,
            TaskDatabase::class.java,
            "task_database"
        ).build()
    }
    @Provides
    fun provideRepo(db:TaskDatabase) =
        TaskRepository(db);
}

