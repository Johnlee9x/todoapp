package com.tom.todoapp

import android.util.Log
import androidx.navigation.NavHostController
import com.tom.todoapp.TodoDestinations.ADD_EDIT_TASK_ROUTE
import com.tom.todoapp.TodoDestinationsArgs.TASK_ID_ARG
import com.tom.todoapp.TodoScreen.ADD_EDIT_TASK_SCREEN
import com.tom.todoapp.TodoScreen.TASK_DETAIL_SCREEN

private object TodoScreen {
    const val TASKS_SCREEN = "tasks"
    const val STATISTICS_SCREEN = "statistics"
    const val TASK_DETAIL_SCREEN = "task"
    const val ADD_EDIT_TASK_SCREEN = "addEditTask"
}

object TodoDestinationsArgs {
    const val USER_MESSAGE_ARG = "userMessage"
    const val TASK_ID_ARG = "taskId"
    const val TITLE_ARG = "title"
}


object TodoDestinations {
    const val TASKS_ROUTE =
        "${TodoScreen.TASKS_SCREEN}?${TodoDestinationsArgs.USER_MESSAGE_ARG}={${TodoDestinationsArgs.USER_MESSAGE_ARG}}"
    const val STATISTIC_ROUTE = TodoScreen.STATISTICS_SCREEN
    const val ADD_EDIT_TASK_ROUTE =
        "${ADD_EDIT_TASK_SCREEN}/{${TodoDestinationsArgs.TITLE_ARG}}?${TASK_ID_ARG}={${TASK_ID_ARG}"
    const val TASK_DETAIL_ROUTE =
        "${TASK_DETAIL_SCREEN}/{${TASK_ID_ARG}}"
}

class TodoNavigationAction(private val navController: NavHostController) {
    fun navigateToTasks(userMessage: Int = 0) {

    }

    fun navigateToStatistic() {}
    fun navigateToAddEditTask(title: Int, taskId: String?) {
        val route = "${ADD_EDIT_TASK_SCREEN}/$title".let {
            if (taskId != null) "$it?$TASK_ID_ARG=$taskId" else it
        }
        Log.i("tamld7", "navigateToAddEditTask: title = $title taskId = $taskId")
        Log.i("tamld7", "navigateToAddEditTask: route = $route")

        navController.navigate(route = route)
    }

    fun navigateToDetailTask(taskId: String) {
        navController.navigate(route = "${TASK_DETAIL_SCREEN}/$taskId")
    }

}


