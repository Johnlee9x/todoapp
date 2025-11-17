package com.tom.todoapp.taskdetail

import androidx.lifecycle.ViewModel
import com.tom.todoapp.data.Task
import com.tom.todoapp.data.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class TaskDetailViewModel @Inject constructor(
    private val taskRepository: TaskRepository
) : ViewModel() {
    private val _userMsg: MutableStateFlow<Int?> = MutableStateFlow(null)
    private val _isLoading = MutableStateFlow(false)
    private val _isTaskDeleted = MutableStateFlow(false)

}

data class TaskDetailUiState(
    val task: Task? = null,
    val isLoading: Boolean = false,
    val userMsg: Int? = null,
    val isTaskDeleted: Boolean = false
)