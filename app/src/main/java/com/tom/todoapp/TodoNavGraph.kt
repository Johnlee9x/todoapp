package com.tom.todoapp

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.tom.todoapp.TodoDestinationsArgs.USER_MESSAGE_ARG
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
        navController = navHostController, startDestination = startDestination, modifier = modifier
    ) {
        composable(
            route = TodoDestinations.TASKS_ROUTE,
            arguments = listOf(navArgument(name = USER_MESSAGE_ARG) {
                type = NavType.IntType; defaultValue = 0
            })
        ) { entry ->
            TaskScreen()
        }
        composable(route = TodoDestinations.STATISTIC_ROUTE) {}
        composable(
            route = TodoDestinations.ADD_EDIT_TASK_ROUTE,
            arguments = listOf(navArgument(name = TodoDestinationsArgs.TASK_ID_ARG) {
                type = NavType.StringType; nullable = true
            }, navArgument(name = TodoDestinationsArgs.TITLE_ARG) { type = NavType.IntType })
        ) { entry ->


        }
        composable(
            route = TodoDestinations.TASK_DETAIL_ROUTE,
            arguments = listOf(navArgument(name = TodoDestinationsArgs.TASK_ID_ARG) {
                type = NavType.StringType
            })
        ) { entry ->

        }


    }


}