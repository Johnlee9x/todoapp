package com.tom.todoapp.core.data.remote

interface NetworkDataSource {
    suspend fun loadTasks(): List<NetworkTask>
    suspend fun saveTasks(tasks: List<NetworkTask>)
}
