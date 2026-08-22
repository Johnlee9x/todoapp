package com.tom.todoapp.core.data

import com.tom.todoapp.core.data.local.LocalTask
import com.tom.todoapp.core.data.remote.NetworkTask
import com.tom.todoapp.core.data.remote.TaskStatus
import org.junit.Assert.assertEquals
import org.junit.Test

class ModelMappingExtTest {

    @Test
    fun `Task toLocal round-trips priority for every value`() {
        Priority.entries.forEach { priority ->
            val task = Task(title = "t", description = "d", id = "1", priority = priority)
            assertEquals(priority.name, task.toLocal().priority)
        }
    }

    @Test
    fun `LocalTask toExternal round-trips priority for every value`() {
        Priority.entries.forEach { priority ->
            val local = LocalTask(id = "1", title = "t", description = "d", isCompleted = false, priority = priority.name)
            assertEquals(priority, local.toExternal().priority)
        }
    }

    @Test
    fun `NetworkTask toLocal maps priority ordinal to the matching enum value`() {
        val cases = mapOf(0 to Priority.LOW, 1 to Priority.MEDIUM, 2 to Priority.HIGH)
        cases.forEach { (ordinal, expected) ->
            val network = networkTask(priority = ordinal)
            assertEquals(expected.name, network.toLocal().priority)
        }
    }

    @Test
    fun `NetworkTask toLocal falls back to MEDIUM when priority is null`() {
        val network = networkTask(priority = null)
        assertEquals(Priority.MEDIUM.name, network.toLocal().priority)
    }

    @Test
    fun `NetworkTask toLocal falls back to MEDIUM when priority is out of range`() {
        listOf(-1, 99).forEach { outOfRange ->
            val network = networkTask(priority = outOfRange)
            assertEquals(Priority.MEDIUM.name, network.toLocal().priority)
        }
    }

    private fun networkTask(priority: Int?) = NetworkTask(
        id = "1",
        title = "t",
        shortDescription = "d",
        priority = priority,
        status = TaskStatus.ACTIVE,
    )
}
