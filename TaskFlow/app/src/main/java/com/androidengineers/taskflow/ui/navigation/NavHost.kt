package com.androidengineers.taskflow.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.androidengineers.taskflow.ui.screens.addedit.AddEditTaskScreen
import com.androidengineers.taskflow.ui.screens.tasklist.TaskListScreen

sealed class Screen(val route: String) {
    object TaskList : Screen("task_list")
    object AddEditTask : Screen("add_edit_task?taskId={taskId}") {
        fun createRoute(taskId: Long = -1L) = "add_edit_task?taskId=$taskId"
    }
}

@Composable
fun TaskFlowNavHost() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.TaskList.route
    ) {
        composable(Screen.TaskList.route) {
            TaskListScreen(
                onAddTask = {
                    navController.navigate(Screen.AddEditTask.createRoute())
                },
                onEditTask = { taskId ->
                    navController.navigate(Screen.AddEditTask.createRoute(taskId))
                }
            )
        }
        composable(
            route = Screen.AddEditTask.route,
            arguments = listOf(
                navArgument("taskId") {
                    type = NavType.LongType
                    defaultValue = -1L
                }
            )
        ) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getLong("taskId") ?: -1L
            AddEditTaskScreen(
                taskId = taskId,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
