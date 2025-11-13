package com.tom.todoapp

import androidx.navigation.NavHostController
import com.tom.todoapp.TodoDestinationsArgs.TASK_ID_ARG

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
        "${TodoScreen.ADD_EDIT_TASK_SCREEN}/{${TodoDestinationsArgs.TITLE_ARG}}?${TodoDestinationsArgs.TASK_ID_ARG}={${TodoDestinationsArgs.TASK_ID_ARG}"
    const val TASK_DETAIL_ROUTE =
        "${TodoScreen.TASK_DETAIL_SCREEN}/{${TodoDestinationsArgs.TASK_ID_ARG}}"
}

class TodoNavigation(private val navController: NavHostController) {
    fun navigateToTasks(userMessage: Int = 0) {

    }

    fun navigateToStatistic() {}
    fun navigateToAddEditTask(title: Int, taskId: String) {
        navController.navigate(route = "${TodoScreen.TASK_DETAIL_SCREEN}/$title".let {
            "$it?$TASK_ID_ARG=$taskId"
        })
    }

    fun navigateToDetailTask(taskId: String) {
        navController.navigate(route = "${TodoScreen.TASK_DETAIL_SCREEN}/$taskId")
    }

}


