package com.tom.todoapp.core.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.tom.todoapp.core.data.Priority


@Entity(tableName = "tasks")
data class LocalTask(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val isCompleted: Boolean,
    val priority: String = Priority.MEDIUM.name,
)
