package com.tom.todoapp

import androidx.navigation.NavHostController

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
    const val TASKS_ROUTE = "tasks_route"
    const val STATISTIC_ROUTE = "s"
    const val ADD_EDIT_TASK_ROUTE = "a"
    const val TASK_DETAIL_ROUTE = "t"
}

class TodoNavigation(private val navController: NavHostController) {
    fun navigateToTasks() {}

    fun navigateToStatistic() {}
    fun navigateToAddEditTask() {}
    fun navigateToDetailTask() {}

}


