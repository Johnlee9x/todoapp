package com.tom.todoapp.taskdetail

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
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
import com.tom.todoapp.data.Task

@Composable
fun TaskDetailScreen(
    onEditTask: (String) -> Unit = {},
    onBack: () -> Unit = {},
    onDeleteTask: () -> Unit = {},
    viewModel: TaskDetailViewModel = hiltViewModel(),
    snackBarHostState: SnackbarHostState = remember { SnackbarHostState() }
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackBarHostState) },
        floatingActionButton = {
            SmallFloatingActionButton(onClick = {
                //todo later
            }) {
                Icon(
                    imageVector = Icons.Filled.Edit,
                    contentDescription = stringResource(R.string.edit_task)
                )
            }
        }) { paddingValues ->
        EditTaskContent()

    }
}

@Composable
fun EditTaskContent(
    loading: Boolean = false,
    empty: Boolean = false,
    task: Task? = null,
    onTitleChange: (String) -> Unit = {},
    onDescriptionChange: (String) -> Unit = {},
    onDateChange: (Long) -> Unit = {},
    onTimeChange: (Long) -> Unit = {},
    onPriorityChange: (String) -> Unit = {},
    onTaskChange: (Boolean) -> Unit = {},
    onRefresh: () -> Unit = {},
    modifier: Modifier = Modifier
) {


}


