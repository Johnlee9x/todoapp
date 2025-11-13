package com.tom.todoapp.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks")
    fun observeAllTasks(): Flow<List<LocalTask>>

    @Query("SELECT * FROM tasks WHERE id = :taskId")
    fun observeById(taskId: String): Flow<LocalTask>

    @Query(value = "SELECT * FROM tasks")
    suspend fun getAll(): List<LocalTask>

    @Query(value = "SELECT * FROM tasks WHERE id = :taskId")
    suspend fun getById(taskId: String): LocalTask?

    @Query(value = "DELETE FROM tasks")
    suspend fun deleteAll()

    @Upsert
    suspend fun upsertAll(tasks: List<LocalTask>)

}


