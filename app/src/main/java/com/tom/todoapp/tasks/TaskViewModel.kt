package com.tom.todoapp.tasks

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tom.todoapp.R
import com.tom.todoapp.data.Task
import com.tom.todoapp.data.TaskRepository
import com.tom.todoapp.util.Async
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskViewModel @Inject constructor(
    val taskRepository: TaskRepository,
    val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _saveFilterType =
        savedStateHandle.getStateFlow(TASKS_FILTER_SAVED_STATE_KEY, TasksFilterType.ALL_TASKS)

    private val _filterUiInfo = _saveFilterType.map { getFilterUiInfo(it) }.distinctUntilChanged()

    private val _userMsg: MutableStateFlow<Int?> = MutableStateFlow(value = null)

    private val _isLoading = MutableStateFlow(value = false)

    private val _filteredTasksAsync =
        combine(
            taskRepository.getTasksStream(),
            _saveFilterType
        ) { tasks, type ->
            Log.i("tamld7", ": tasks =$tasks")
            Log.i("tamld7", ": type =$type")
            filterTasks(tasks, type)
        }.map {
            Async.Success(it)
        }.catch<Async<List<Task>>> { emit(Async.Error(1)) }

    val uiState: StateFlow<TasksUiState> = combine(
        _filterUiInfo, _isLoading, _filteredTasksAsync, _userMsg
    ) { filterUiInfo, isLoading, tasksAsync, userMsg ->
        Log.i("tamld7", ": filterUiInfo = $filterUiInfo")
        Log.i("tamld7", ": isLoading = $isLoading")
        Log.i("tamld7", ": userMsg = $userMsg")
        Log.i("tamld7", ": tasksAsync = $tasksAsync")
        when (tasksAsync) {
            Async.Loading -> {
                TasksUiState(isLoading = true)
            }

            is Async.Success -> {
                TasksUiState(
                    items = tasksAsync.data,
                    filteringUiInfo = filterUiInfo,
                    isLoading = isLoading,
                    userMsg = userMsg
                )
            }

            is Async.Error -> {
                TasksUiState(userMsg = tasksAsync.errMsg)
            }
        }

    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = TasksUiState(isLoading = true)
    )

    private fun filterTasks(tasks: List<Task>, filterType: TasksFilterType): List<Task> {
        val tasksToShow = ArrayList<Task>()
        for (task in tasks) {
            when (filterType) {
                TasksFilterType.ALL_TASKS -> tasksToShow.add(task)
                TasksFilterType.ACTIVE_TASKS -> if (!task.isCompleted) tasksToShow.add(task)
                TasksFilterType.COMPLETED_TASK -> if (task.isCompleted) tasksToShow.add(task)

            }
        }
        return tasksToShow
    }

    private fun getFilterUiInfo(requestType: TasksFilterType): FilteringUiInfo =
        when (requestType) {
            TasksFilterType.ALL_TASKS -> {
                FilteringUiInfo(
                    currentFilteringLabel = R.string.label_all,
                    noTasksLabel = R.string.no_tasks_all,
                    noTaskIconRes = R.drawable.logo_no_fill
                )
            }

            TasksFilterType.ACTIVE_TASKS -> {
                FilteringUiInfo(
                    currentFilteringLabel = R.string.label_active,
                    noTasksLabel = R.string.no_tasks_active,
                    noTaskIconRes = R.drawable.ic_check_circle_96dp
                )
            }

            TasksFilterType.COMPLETED_TASK -> {
                FilteringUiInfo(
                    currentFilteringLabel = R.string.label_completed,
                    noTasksLabel = R.string.no_tasks_completed,
                    noTaskIconRes = R.drawable.ic_verified_user_96dp
                )
            }
        }

    fun refresh() {
        Log.i("tamld7", "refresh:was called ")
        _isLoading.value = true
        viewModelScope.launch {
            taskRepository.refreshTasks()
            _isLoading.value = false
        }
    }

    fun completeTask(task: Task, completed: Boolean) = viewModelScope.launch {
        Log.i("tamld7", "completeTask: task = $task")
        Log.i("tamld7", "completeTask: completed = $completed")
        if (completed) {
            taskRepository.completedTask(taskId = task.id)
        } else {
            taskRepository.updateActiveTask(taskId = task.id)
        }
    }

    fun clearCompletedTasks() {
        viewModelScope.launch {
            //todo clear tasks later
        }
    }

    fun setFiltering(requestType: TasksFilterType) {
        Log.i("tamld7", "setFiltering:requestType =$requestType ")
        savedStateHandle[TASKS_FILTER_SAVED_STATE_KEY] = requestType
    }

    override fun onCleared() {
        super.onCleared()
        Log.i("tamld7", "onCleared: was called")
    }
}

// Used to save the current filtering in SavedStateHandle.
const val TASKS_FILTER_SAVED_STATE_KEY = "TASKS_FILTER_SAVED_STATE_KEY"

data class FilteringUiInfo(
    val currentFilteringLabel: Int = R.string.label_all,
    val noTasksLabel: Int = R.string.no_tasks_all,
    val noTaskIconRes: Int = R.drawable.logo_no_fill
)

data class TasksUiState(
    val items: List<Task> = emptyList(),
    val isLoading: Boolean = false, val filteringUiInfo: FilteringUiInfo = FilteringUiInfo(),
    val userMsg: Int? = null
)

