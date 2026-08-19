package com.tom.todoapp.core.data.remote

data class NetworkTask(
    val id: String,
    val title: String,
    val shortDescription: String,
    val priority: Int? = null,
    val status: TaskStatus = TaskStatus.ACTIVE,
)

enum class TaskStatus {
    ACTIVE,
    COMPLETE
}
