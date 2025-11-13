package com.tom.todoapp

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.tom.todoapp.TodoDestinationsArgs.TASK_ID_ARG
import com.tom.todoapp.TodoDestinationsArgs.TITLE_ARG
import com.tom.todoapp.TodoDestinationsArgs.USER_MESSAGE_ARG
import com.tom.todoapp.addedittask.AddEditTaskScreen
import com.tom.todoapp.tasks.TaskScreen
import kotlinx.coroutines.CoroutineScope

@Composable
fun TodoNavGraph(
    modifier: Modifier = Modifier,
    navHostController: NavHostController = rememberNavController(),
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    startDestination: String = TodoDestinations.TASKS_ROUTE,
    navAction: TodoNavigationAction = remember(navHostController) {
        TodoNavigationAction(navHostController)
    }
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
            TaskScreen(onAddTask = {
                navAction.navigateToAddEditTask(
                    title = R.string.add_task,
                    taskId = null
                )
            })
        }
        composable(route = TodoDestinations.STATISTIC_ROUTE) {}
        composable(
            route = TodoDestinations.ADD_EDIT_TASK_ROUTE,
            arguments = listOf(navArgument(name = TITLE_ARG) { type = NavType.IntType })
        ) { entry ->
            val taskId = entry.arguments?.getString(TASK_ID_ARG)
            val route = entry.destination.route
            Log.i("tamld7", "TodoNavGraph: taskId = $taskId")
            Log.i("tamld7", "TodoNavGraph: TITLE_ARG = ${entry.arguments?.getInt(TITLE_ARG)}")
            Log.i("tamld7", "TodoNavGraph: rout = $route")
            AddEditTaskScreen(
                topBarTitle = entry.arguments?.getInt(TITLE_ARG)
                    ?: R.string.add_task,
                onBack = { navHostController.popBackStack() }
            )
        }
        composable(
            route = TodoDestinations.TASK_DETAIL_ROUTE,
            arguments = listOf(navArgument(name = TASK_ID_ARG) {
                type = NavType.StringType
            })
        ) { entry ->

        }

    }

}