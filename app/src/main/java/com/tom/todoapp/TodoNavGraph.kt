package com.tom.todoapp

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.tom.todoapp.tasks.TaskScreen
import kotlinx.coroutines.CoroutineScope

@Composable
fun TodoNavGraph(
    modifier: Modifier = Modifier,
    navHostController: NavHostController = rememberNavController(),
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    startDestination: String = TodoDestinations.TASKS_ROUTE,
) {

    val currNavBackStackEntry by navHostController.currentBackStackEntryAsState()
    val currentRoute = currNavBackStackEntry?.destination?.route ?: TodoDestinations.TASKS_ROUTE

    NavHost(
        navController = navHostController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(route = TodoDestinations.TASKS_ROUTE) {
            TaskScreen()
        }
        composable(route = TodoDestinations.STATISTIC_ROUTE) {}
        composable(route = TodoDestinations.TASK_DETAIL_ROUTE) {}
        composable(route = TodoDestinations.ADD_EDIT_TASK_ROUTE) { }

    }


}