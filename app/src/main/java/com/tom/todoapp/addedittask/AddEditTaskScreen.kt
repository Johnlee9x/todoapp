package com.tom.todoapp.addedittask

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tom.todoapp.R
import com.tom.todoapp.util.AddEditTaskTopAppBar

@Composable
fun AddEditTaskScreen(
    topBarTitle: Int = R.string.add_task,
    onTaskUpdate: () -> Unit = {},
    onBack: () -> Unit = {},
    modifier: Modifier = Modifier,
    addEditTaskViewModel: AddEditTaskViewModel = hiltViewModel(),
    snackBarHostState: SnackbarHostState = remember { SnackbarHostState() }
) {
    val uiState by addEditTaskViewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.isTaskSaved) {
        if (uiState.isTaskSaved) {
            onTaskUpdate()
        }
    }

    val userMsg = uiState.userMsg
    if (userMsg != null) {
        val msgText = stringResource(userMsg)
        LaunchedEffect(msgText) {
            snackBarHostState.showSnackbar(msgText)
            addEditTaskViewModel.userMsgShown()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackBarHostState) },
        topBar = { AddEditTaskTopAppBar(title = topBarTitle, onBack = onBack) },
        floatingActionButton = {
            SmallFloatingActionButton(onClick = { addEditTaskViewModel.saveTask() }) {
                Icon(
                    imageVector = Icons.Filled.Done,
                    contentDescription = stringResource(R.string.cd_save_task)
                )
            }
        }) { paddingValues ->
        AddEditContent(
            loading = uiState.isLoading,
            title = uiState.title,
            description = uiState.description,
            onTitleChange = addEditTaskViewModel::setTitle,
            onDescriptionChange = addEditTaskViewModel::setDescription,
            modifier = Modifier.padding(paddingValues = paddingValues)
        )
    }
}

@Composable
fun AddEditContent(
    loading: Boolean = false,
    title: String = "",
    description: String = "",
    onTitleChange: (String) -> Unit = {},
    onDescriptionChange: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var isRefreshing by remember { mutableStateOf(false) }
    val refreshingState = rememberPullToRefreshState()
    if (loading) {
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            state = refreshingState,
            onRefresh = {},
            content = {})
    } else {
        Column(
            modifier
                .fillMaxWidth()
                .padding(all = dimensionResource(id = R.dimen.horizontal_margin))
                .verticalScroll(state = rememberScrollState())
        ) {

            OutlinedTextField(
                value = title,
                modifier = Modifier.fillMaxWidth(),
                onValueChange = onTitleChange,
                placeholder = { Text(text = stringResource(id = R.string.title_hint)) },
                textStyle = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                maxLines = 1,
            )
            OutlinedTextField(
                value = description,
                modifier = Modifier
                    .height(350.dp)
                    .fillMaxWidth(),
                onValueChange = onDescriptionChange,
                placeholder = { Text(text = stringResource(id = R.string.description_hint)) },
            )

        }
    }
}