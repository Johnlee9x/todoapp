package com.tom.todoapp.core.data

import com.tom.todoapp.core.data.local.LocalTask
import com.tom.todoapp.core.data.remote.NetworkTask
import com.tom.todoapp.core.data.remote.TaskStatus

fun Task.toLocal() = LocalTask(
    id = this.id,
    title = this.title,
    description = this.description,
    isCompleted = this.isCompleted,
    priority = this.priority.name,
)

fun NetworkTask.toLocal() = LocalTask(
    id = this.id,
    title = this.title,
    description = this.shortDescription,
    isCompleted = (this.status == TaskStatus.COMPLETE),
    priority = (Priority.entries.getOrNull(this.priority ?: 1) ?: Priority.MEDIUM).name,
)

fun LocalTask.toExternal() = Task(
    id = this.id,
    title = this.title,
    description = this.description,
    isCompleted = this.isCompleted,
    priority = Priority.valueOf(this.priority),
)

fun List<LocalTask>.toExternal() = map(LocalTask::toExternal)

fun List<NetworkTask>.toLocalTasks() = map(NetworkTask::toLocal)
