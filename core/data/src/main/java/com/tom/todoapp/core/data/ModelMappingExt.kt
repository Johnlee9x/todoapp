package com.tom.todoapp.core.data

import com.tom.todoapp.core.data.local.LocalTask
import com.tom.todoapp.core.data.remote.NetworkTask
import com.tom.todoapp.core.data.remote.TaskStatus

fun Task.toLocal() = LocalTask(
    id = this.id,
    title = this.title,
    description = this.description,
    isCompleted = this.isCompleted,
)

fun NetworkTask.toLocal() = LocalTask(
    id = this.id,
    title = this.title,
    description = this.shortDescription,
    isCompleted = (this.status == TaskStatus.COMPLETE),
)

fun LocalTask.toExternal() = Task(
    id = this.id,
    title = this.title,
    description = this.description,
    isCompleted = this.isCompleted
)

fun List<LocalTask>.toExternal() = map(LocalTask::toExternal)

fun List<NetworkTask>.toLocalTasks() = map(NetworkTask::toLocal)
