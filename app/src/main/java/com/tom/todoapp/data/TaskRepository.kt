package com.tom.todoapp.data

import kotlinx.coroutines.flow.Flow

interface TaskRepository {
    fun getTasksStream(): Flow<List<Task>>

    suspend fun getTasks(forceUpdate: Boolean = false): List<Task>

    suspend fun refreshTasks()

    fun getTaskStream(taskId: String): Flow<Task?>

    suspend fun getTask(taskId: String, forceUpdate: Boolean = false): Task?

    suspend fun createTask(title: String, description: String): String

    suspend fun updateTask(taskId: String, title: String, description: String)

    suspend fun deleteTask(taskId: String)

    suspend fun deleteAllTask()

    suspend fun completedTask(taskId: String)

    suspend fun updateActiveTask(taskId: String)

}