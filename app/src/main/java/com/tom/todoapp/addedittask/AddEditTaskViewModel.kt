package com.tom.todoapp.addedittask

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tom.todoapp.R
import com.tom.todoapp.TodoDestinationsArgs
import com.tom.todoapp.data.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditTaskViewModel @Inject constructor(
    private val taskRepository: TaskRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val taskId: String? = savedStateHandle[TodoDestinationsArgs.TASK_ID_ARG]

    private val _title = MutableStateFlow("")
    private val _description = MutableStateFlow("")
    private val _isLoading = MutableStateFlow(false)
    private val _userMsg: MutableStateFlow<Int?> = MutableStateFlow(null)
    private val _isTaskSaved = MutableStateFlow(false)

    val uiState: StateFlow<AddEditTaskUiState> = combine(
        _title, _description, _isLoading, _userMsg, _isTaskSaved
    ) { title, description, isLoading, userMsg, isTaskSaved ->
        AddEditTaskUiState(
            title = title,
            description = description,
            isLoading = isLoading,
            userMsg = userMsg,
            isTaskSaved = isTaskSaved
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = AddEditTaskUiState(isLoading = taskId != null)
    )

    init {
        if (taskId != null) {
            loadTask(taskId)
        }
    }

    private fun loadTask(taskId: String) {
        _isLoading.value = true
        viewModelScope.launch {
            val task = taskRepository.getTask(taskId)
            if (task != null) {
                _title.value = task.title
                _description.value = task.description
            } else {
                _userMsg.value = R.string.task_not_found
            }
            _isLoading.value = false
        }
    }

    fun setTitle(title: String) {
        _title.value = title
    }

    fun setDescription(description: String) {
        _description.value = description
    }

    fun saveTask() {
        if (_title.value.isEmpty() || _description.value.isEmpty()) {
            _userMsg.value = R.string.empty_task_message
            return
        }
        viewModelScope.launch {
            if (taskId == null) {
                taskRepository.createTask(title = _title.value, description = _description.value)
            } else {
                taskRepository.updateTask(
                    taskId = taskId,
                    title = _title.value,
                    description = _description.value
                )
            }
            _isTaskSaved.value = true
        }
    }

    fun userMsgShown() {
        _userMsg.value = null
    }
}

data class AddEditTaskUiState(
    val title: String = "",
    val description: String = "",
    val isLoading: Boolean = false,
    val userMsg: Int? = null,
    val isTaskSaved: Boolean = false
)