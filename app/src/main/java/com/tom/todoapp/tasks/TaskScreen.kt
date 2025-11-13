package com.tom.todoapp.tasks

import android.util.Log
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.tom.todoapp.R
import com.tom.todoapp.util.TasksTopAppBar

@Composable
fun TaskScreen(
    @StringRes userMsg: Int = 0,
    onAddTask: () -> Unit = {},
    onTaskClick: () -> Unit = {},
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
                onFilterAllTasks = {},
                onFilterActiveTasks = {},
                onFilterCompletedTasks = {},
                onClearCompletedTasks = {},
                onRefresh = {})
        },
        floatingActionButton = {
            SmallFloatingActionButton(onClick = onAddTask) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = stringResource(R.string.add_task)
                )
            }
        }) { paddingValues ->
        Log.i("tamld7", "TaskScreen: paddingValue = $paddingValues")
        TaskContent()

    }

}

@Composable
fun TaskContent(
    loading: Boolean = false,
    tasks: List<String> = emptyList(),
    onTaskClick: (String) -> Unit = {},
    onRefresh: () -> Unit = {},
    onTaskCheckChange: (String, Boolean) -> Unit = { _, _ -> Unit },
    modifier: Modifier = Modifier
) {

}