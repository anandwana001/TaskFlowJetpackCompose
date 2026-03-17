package com.androidengineers.taskflow.ui.screens.tasklist

import app.cash.turbine.test
import com.androidengineers.taskflow.data.repository.TaskRepository
import com.androidengineers.taskflow.domain.model.Category
import com.androidengineers.taskflow.domain.model.Priority
import com.androidengineers.taskflow.domain.model.Task
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TaskListViewModelTest {
    private val repository = mockk<TaskRepository>(relaxed = true)
    private lateinit var viewModel: TaskListViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        coEvery { repository.getAllTasks() } returns flowOf(emptyList())
        viewModel = TaskListViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `tasks should initially be empty`() = runTest {
        viewModel.tasks.test {
            assertEquals(emptyList<Task>(), awaitItem())
        }
    }

    @Test
    fun `toggleTaskCompletion should call repository update`() = runTest {
        val task = Task(1, "Test", "", Category.OTHER, Priority.LOW, 0L, false)
        viewModel.toggleTaskCompletion(task)
        testDispatcher.scheduler.advanceUntilIdle()
        coVerify { repository.updateTask(task.copy(isCompleted = true)) }
    }

    @Test
    fun `deleteTask should call repository delete`() = runTest {
        val task = Task(1, "Test", "", Category.OTHER, Priority.LOW, 0L, false)
        viewModel.deleteTask(task)
        testDispatcher.scheduler.advanceUntilIdle()
        coVerify { repository.deleteTask(task) }
    }
}
