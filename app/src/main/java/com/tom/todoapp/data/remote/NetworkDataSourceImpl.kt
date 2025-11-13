package com.tom.todoapp.data.remote

import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject

class NetworkDataSourceImpl @Inject constructor() : NetworkDataSource {
    private val accessMutex = Mutex()
    private var listNetworkTasks = listOf(
        NetworkTask(
            id = "PISA",
            title = "Build tower in Pisa",
            shortDescription = "Ground looks good, no foundation work required."
        ),
        NetworkTask(
            id = "TACOMA",
            title = "Finish bridge in Tacoma",
            shortDescription = "Found awesome girders at half the cost!"
        )
    )


    override suspend fun loadTasks(): List<NetworkTask> = accessMutex.withLock {
        delay(3000L)
        return listNetworkTasks
    }

    override suspend fun saveTasks(tasks: List<NetworkTask>) = accessMutex.withLock {
        delay(3000L)
        listNetworkTasks = tasks
    }
}