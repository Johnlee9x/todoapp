package com.tom.todoapp.taskdetail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tom.todoapp.R
import com.tom.todoapp.data.Task
import com.tom.todoapp.util.DetailTaskTopAppBar

@Composable
fun TaskDetailScreen(
    onEditTask: (String) -> Unit = {},
    onBack: () -> Unit = {},
    onDeleteTask: () -> Unit = {},
    viewModel: TaskDetailViewModel = hiltViewModel(),
    snackBarHostState: SnackbarHostState = remember { SnackbarHostState() }
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.isTaskDeleted) {
        if (uiState.isTaskDeleted) {
            onDeleteTask()
        }
    }

    val userMsg = uiState.userMsg
    if (userMsg != null) {
        val msgText = stringResource(userMsg)
        LaunchedEffect(msgText) {
            snackBarHostState.showSnackbar(msgText)
            viewModel.userMsgShown()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackBarHostState) },
        topBar = {
            DetailTaskTopAppBar(
                onBack = onBack,
                onDelete = viewModel::deleteTask
            )
        },
        floatingActionButton = {
            SmallFloatingActionButton(onClick = {
                uiState.task?.let { onEditTask(it.id) }
            }) {
                Icon(
                    imageVector = Icons.Filled.Edit,
                    contentDescription = stringResource(R.string.edit_task)
                )
            }
        }) { paddingValues ->
        EditTaskContent(
            loading = uiState.isLoading,
            empty = uiState.task == null && !uiState.isLoading,
            task = uiState.task,
            onTaskChange = viewModel::setCompleted,
            modifier = Modifier.padding(paddingValues)
        )
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
    when {
        loading -> {
            Column(
                modifier = modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator()
            }
        }

        empty || task == null -> {
            Column(
                modifier = modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = stringResource(R.string.no_data),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }

        else -> {
            Column(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(all = dimensionResource(id = R.dimen.horizontal_margin))
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Checkbox(
                        checked = task.isCompleted,
                        onCheckedChange = onTaskChange
                    )
                    Text(
                        text = task.title,
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.padding(start = dimensionResource(R.dimen.horizontal_margin)),
                        textDecoration = if (task.isCompleted) TextDecoration.LineThrough else null
                    )
                }
                Text(
                    text = task.description,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(
                        start = dimensionResource(R.dimen.horizontal_margin),
                        top = dimensionResource(R.dimen.vertical_margin)
                    )
                )
            }
        }
    }
}


