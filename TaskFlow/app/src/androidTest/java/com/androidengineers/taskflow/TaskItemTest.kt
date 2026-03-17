package com.androidengineers.taskflow

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.androidengineers.taskflow.domain.model.Category
import com.androidengineers.taskflow.domain.model.Priority
import com.androidengineers.taskflow.domain.model.Task
import com.androidengineers.taskflow.ui.components.TaskItem
import com.androidengineers.taskflow.ui.theme.TaskFlowTheme
import org.junit.Rule
import org.junit.Test

class TaskItemTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun taskItem_displaysTitleAndCategory() {
        val task = Task(
            title = "Test Task",
            description = "Test Description",
            category = Category.WORK,
            priority = Priority.HIGH,
            dueDate = System.currentTimeMillis()
        )

        composeTestRule.setContent {
            TaskFlowTheme {
                TaskItem(
                    task = task,
                    onTaskClick = {},
                    onDeleteClick = {},
                    onCompleteClick = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Test Task").assertExists()
        composeTestRule.onNodeWithText("Work").assertExists()
        composeTestRule.onNodeWithText("High").assertExists()
    }
}
