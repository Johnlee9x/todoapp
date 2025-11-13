package com.tom.todoapp.di

import android.content.Context
import androidx.room.Room
import com.tom.todoapp.data.DefaultRepository
import com.tom.todoapp.data.TaskRepository
import com.tom.todoapp.data.local.TodoDataBase
import com.tom.todoapp.data.remote.NetworkDataSource
import com.tom.todoapp.data.remote.NetworkDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Singleton
    @Binds
    abstract fun bindTaskRepository(repository: DefaultRepository): TaskRepository
}

@Module
@InstallIn(SingletonComponent::class)
abstract class DataNetworkModule {

    @Singleton
    @Binds
    abstract fun bindNetworkDataSource(networkDataSource: NetworkDataSourceImpl): NetworkDataSource
}

@Module
@InstallIn(SingletonComponent::class)
object DataLocalSourceModule {

    @Singleton
    @Provides
    fun provideDataBase(@ApplicationContext context: Context): TodoDataBase {
        return Room.databaseBuilder(
            context.applicationContext,
            TodoDataBase::class.java,
            name = "Tasks.db"
        ).build()
    }

    @Provides
    fun provideTaskDao(dataBase: TodoDataBase) = dataBase.taskDao()
}