package com.tom.todoapp.data

import com.tom.todoapp.data.local.LocalTask
import com.tom.todoapp.data.remote.NetworkTask
import com.tom.todoapp.data.remote.TaskStatus

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