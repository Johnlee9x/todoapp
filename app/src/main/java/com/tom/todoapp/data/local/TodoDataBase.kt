package com.tom.todoapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase


@Database(entities = [LocalTask::class], version = 1, exportSchema = false)
abstract class TodoDataBase : RoomDatabase() {

    abstract fun taskDao(): TaskDao
}