package com.tom.todoapp.core.data

data class Task(
    val title: String,
    val description: String,
    val isCompleted: Boolean = false,
    val id: String,
    val priority: Priority = Priority.MEDIUM,
) {
    val titleForList: String
        get() = if (title.isNotEmpty()) title else description

    val isActive
        get() = !isCompleted

    val isEmpty
        get() = title.isEmpty() || description.isEmpty()
}
