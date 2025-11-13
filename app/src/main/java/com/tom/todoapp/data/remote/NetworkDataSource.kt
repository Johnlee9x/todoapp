package com.tom.todoapp.data.remote

interface NetworkDataSource {
    suspend fun loadTasks(): List<NetworkTask>
    suspend fun saveTasks(tasks: List<NetworkTask>)
}