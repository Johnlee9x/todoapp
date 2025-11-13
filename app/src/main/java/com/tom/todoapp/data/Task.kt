package com.tom.todoapp.data

data class Task(
    val title: String,
    val description: String,
    val isCompleted: Boolean = false,
    val id: String,
) {
    val titleForList: String
        get() = if (title.isEmpty()) title else description

    val isActive
        get() = !isCompleted

    val isEmpty
        get() = title.isEmpty() || description.isEmpty()
}