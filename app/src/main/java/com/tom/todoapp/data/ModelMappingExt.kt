package com.tom.todoapp.data

import com.tom.todoapp.data.local.LocalTask

fun Task.toLocal() = LocalTask(
    id = this.id,
    title = this.title,
    description = this.description,
    isCompleted = this.isCompleted,
)


fun LocalTask.toExternal() = Task(
    id = this.id,
    title = this.title,
    description = this.description,
    isCompleted = this.isCompleted
)