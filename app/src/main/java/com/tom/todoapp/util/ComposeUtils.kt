package com.tom.todoapp.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun LoadingContent(
    loading: Boolean, empty: Boolean,
    emptyContent: @Composable () -> Unit,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {

    if (empty){
        emptyContent()
    }else{
    }

}