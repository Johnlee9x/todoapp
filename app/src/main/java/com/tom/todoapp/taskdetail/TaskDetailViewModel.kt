package com.tom.todoapp.taskdetail

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tom.todoapp.R
import com.tom.todoapp.TodoDestinationsArgs
import com.tom.todoapp.data.Task
import com.tom.todoapp.data.TaskRepository
import com.tom.todoapp.util.Async
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskDetailViewModel @Inject constructor(
    private val taskRepository: TaskRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    val taskId: String = savedStateHandle[TodoDestinationsArgs.TASK_ID_ARG]!!

    private val _userMsg: MutableStateFlow<Int?> = MutableStateFlow(null)
    private val _isLoading = MutableStateFlow(false)
    private val _isTaskDeleted = MutableStateFlow(false)

    private val _taskAsync = taskRepository.getTaskById(taskId = taskId).map { task ->
        Log.i("tamld7", ": _taskAsync = $task")
        handleTask(task = task)
    }.catch { emit(value = Async.Error(errMsg = R.string.loading_task_error)) }

    val uiState: StateFlow<TaskDetailUiState> = combine(
        _userMsg, _isLoading, _isTaskDeleted, _taskAsync
    ) { userMsg, isLoading, isTaskDeleted, taskAsync ->
        when (taskAsync) {
            Async.Loading -> {
                TaskDetailUiState(isLoading = true)
            }

            is Async.Error -> {
                TaskDetailUiState(
                    userMsg = taskAsync.errMsg,
                    isTaskDeleted = isTaskDeleted
                )
            }

            is Async.Success -> {
                TaskDetailUiState(
                    task = taskAsync.data,
                    isLoading = isLoading,
                    userMsg = userMsg,
                    isTaskDeleted = isTaskDeleted
                )
            }
        }
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(500),
            initialValue = TaskDetailUiState(isLoading = true)
        )

    fun deleteTask() {
        viewModelScope.launch {
            taskRepository.deleteTask(taskId = taskId)
            _isTaskDeleted.value = true
        }
    }

    fun setCompleted(completed: Boolean) {
        viewModelScope.launch {
            val task = uiState.value.task ?: return@launch
            if (completed) {
                taskRepository.completedTask(taskId = task.id)
                //todo display snackbar
            } else {
                taskRepository.updateActiveTask(taskId = task.id)
                //todo display snackbar
            }
        }
    }

    private fun handleTask(task: Task?): Async<Task?> {
        if (task == null) {
            return Async.Error(errMsg = R.string.task_not_found)
        }
        return Async.Success(data = task)
    }

}

data class TaskDetailUiState(
    val task: Task? = null,
    val isLoading: Boolean = false,
    val userMsg: Int? = null,
    val isTaskDeleted: Boolean = false
)