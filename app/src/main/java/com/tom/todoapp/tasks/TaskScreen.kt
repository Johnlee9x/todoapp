package com.tom.todoapp.tasks

import android.util.Log
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tom.todoapp.R
import com.tom.todoapp.data.Task
import com.tom.todoapp.util.TasksTopAppBar

@Composable
fun TaskScreen(
    @StringRes userMsg: Int = 0,
    onAddTask: () -> Unit = {},
    onTaskClick: (Task) -> Unit = {},
    onUserMsgDisplayed: () -> Unit = {},
    modifier: Modifier = Modifier,
    openDrawer: () -> Unit = {},
    taskViewModel: TaskViewModel = hiltViewModel(),
    snackBarHostState: SnackbarHostState = remember { SnackbarHostState() }
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
        topBar = {
            TasksTopAppBar(
                openDrawer = openDrawer,
                onFilterAllTasks = { taskViewModel.setFiltering(TasksFilterType.ALL_TASKS) },
                onFilterActiveTasks = { taskViewModel.setFiltering(TasksFilterType.ACTIVE_TASKS) },
                onFilterCompletedTasks = { taskViewModel.setFiltering(TasksFilterType.COMPLETED_TASK) },
                onClearCompletedTasks = { taskViewModel.clearCompletedTasks() },
                onRefresh = { taskViewModel.refresh() }
            )
        },
        floatingActionButton = {
            SmallFloatingActionButton(onClick = onAddTask) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = stringResource(R.string.add_task)
                )
            }
        }) { paddingValues ->
        val uiState by taskViewModel.uiState.collectAsStateWithLifecycle()

        // Show snackbar when user message changes
        val userMsg = uiState.userMsg
        if (userMsg != null) {
            val msgText = stringResource(userMsg)
            LaunchedEffect(msgText) {
                snackBarHostState.showSnackbar(msgText)
                taskViewModel.userMsgShown()
            }
        }

        Log.i("tamld7", "TaskScreen: paddingValue = $paddingValues")
        TaskContent(
            loading = uiState.isLoading,
            tasks = uiState.items,
            onTaskClick = onTaskClick,
            onRefresh = { },
            onTaskCheckChange = taskViewModel::completeTask,
            onTaskDelete = taskViewModel::deleteTask,
            modifier = modifier,
            currentFilteringLabel = uiState.filteringUiInfo.currentFilteringLabel,
            noTasksLabel = uiState.filteringUiInfo.noTasksLabel,
            noTasksIconRes = uiState.filteringUiInfo.noTaskIconRes,
        )
    }

}

@Composable
fun TaskContent(
    loading: Boolean = false,
    tasks: List<Task> = emptyList(),
    onTaskClick: (Task) -> Unit = {},
    @StringRes currentFilteringLabel: Int,
    @StringRes noTasksLabel: Int,
    @DrawableRes noTasksIconRes: Int,
    onRefresh: () -> Unit = {},
    onTaskCheckChange: (Task, Boolean) -> Unit = { _, _ -> },
    onTaskDelete: (Task) -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = dimensionResource(id = R.dimen.horizontal_margin))
    ) {
        Text(
            text = stringResource(currentFilteringLabel), modifier = Modifier.padding(
                horizontal = dimensionResource(id = R.dimen.list_item_padding),
                vertical = dimensionResource(R.dimen.vertical_margin)
            ), style = MaterialTheme.typography.headlineSmall
        )

        LazyColumn {
            items(tasks, key = { it.id }) { task ->
                SwipeableTaskItem(
                    task = task,
                    onTaskClick = onTaskClick,
                    onCheckChange = { onTaskCheckChange(task, it) },
                    onDelete = { onTaskDelete(task) }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeableTaskItem(
    task: Task,
    onTaskClick: (Task) -> Unit = {},
    onCheckChange: (Boolean) -> Unit = {},
    onDelete: () -> Unit = {},
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (value == SwipeToDismissBoxValue.EndToStart) {
                onDelete()
                true
            } else false
        },
        positionalThreshold = { totalDistance -> totalDistance * 0.4f }
    )
    SwipeToDismissBox(
        state = dismissState,
        enableDismissFromStartToEnd = false,
        backgroundContent = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Red)
                    .padding(end = dimensionResource(R.dimen.horizontal_margin)),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = stringResource(R.string.menu_delete_task),
                    tint = Color.White
                )
            }
        }
    ) {
        TaskItem(task = task, onTaskClick = onTaskClick, onCheckChange = onCheckChange)
    }
}

@Composable
fun TaskItem(
    task: Task, onTaskClick: (Task) -> Unit = {}, onCheckChange: (Boolean) -> Unit = {}
) {
    Log.i("tamld7", "TaskItem: task = $task")
    Row(
        verticalAlignment = Alignment.CenterVertically, modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = dimensionResource(id = R.dimen.horizontal_margin),
                vertical = dimensionResource(id = R.dimen.vertical_margin)
            )
            .clickable { onTaskClick(task) }) {
        Checkbox(checked = task.isCompleted, onCheckedChange = onCheckChange)
        Text(
            text = task.titleForList,
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(start = dimensionResource(id = R.dimen.horizontal_margin)),
            textDecoration = if (task.isCompleted) {
                TextDecoration.LineThrough
            } else {
                null
            }
        )

    }
}