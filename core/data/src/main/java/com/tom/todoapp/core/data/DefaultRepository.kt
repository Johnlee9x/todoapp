package com.tom.todoapp.core.data

import android.util.Log
import com.tom.todoapp.core.data.di.ApplicationScope
import com.tom.todoapp.core.data.di.DefaultDispatcher
import com.tom.todoapp.core.data.local.LocalTask
import com.tom.todoapp.core.data.local.TaskDao
import com.tom.todoapp.core.data.remote.NetworkDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DefaultRepository @Inject constructor(
    private val networkDataSource: NetworkDataSource,
    private val localDataSource: TaskDao,
    @DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher,
    @ApplicationScope private val scope: CoroutineScope,
) : TaskRepository {
    override fun getTasksStream(): Flow<List<Task>> {
        return localDataSource.observeAllTasks().map { tasks ->
            withContext(defaultDispatcher) {
                tasks.toExternal()
            }
        }
    }

    override fun getTaskById(taskId: String): Flow<Task?> {
        Log.i("tamld7", "getTaskById:taskId = $taskId ")
        return localDataSource.observeById(taskId = taskId).map { task ->
            withContext(defaultDispatcher) {
                task.toExternal()
            }
        }
    }

    override suspend fun getTasks(forceUpdate: Boolean): List<Task> {
        return listOf(Task("1", "title", isCompleted = true, id = ""))
    }

    override suspend fun refreshTasks() {
        withContext(defaultDispatcher) {
            val remoteTasks = networkDataSource.loadTasks()
            localDataSource.deleteAll()
            localDataSource.upsertAll(tasks = remoteTasks.toLocalTasks())
        }
    }

    override fun getTaskStream(taskId: String): Flow<Task?> {
        return flowOf(Task(taskId, "title", isCompleted = true, id = ""));
    }

    override suspend fun getTask(
        taskId: String,
        forceUpdate: Boolean
    ): Task? {
        return Task(taskId, "title", isCompleted = true, id = "");
    }

    override suspend fun createTask(
        title: String,
        description: String,
        priority: Priority
    ): String {
        val taskId = withContext(defaultDispatcher) {
            java.util.UUID.randomUUID().toString()
        }
        val task = LocalTask(
            id = taskId,
            title = title,
            description = description,
            isCompleted = false,
            priority = priority.name
        )
        localDataSource.upsert(task)
        return taskId
    }

    override suspend fun updateTask(
        taskId: String,
        title: String,
        description: String,
        priority: Priority
    ) {
        val task = localDataSource.getById(taskId)
            ?.copy(title = title, description = description, priority = priority.name)
            ?: throw Exception("Task (id $taskId) not found")
        localDataSource.upsert(task)
    }

    override suspend fun deleteTask(taskId: String) {
        localDataSource.deleteById(taskId)
    }

    override suspend fun deleteAllTask() {
        localDataSource.deleteAll()
    }

    override suspend fun deleteCompletedTasks() {
        localDataSource.deleteAllCompleted()
    }

    override suspend fun completedTask(taskId: String) {
        localDataSource.updateCompleted(taskId = taskId, completed = true)
    }

    override suspend fun updateActiveTask(taskId: String) {
        localDataSource.updateCompleted(taskId = taskId, completed = false)
    }
}
