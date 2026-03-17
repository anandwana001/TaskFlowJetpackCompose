package com.androidengineers.taskflow.ui.screens.addedit

import com.androidengineers.taskflow.data.repository.TaskRepository
import com.androidengineers.taskflow.domain.model.Category
import com.androidengineers.taskflow.domain.model.Priority
import com.androidengineers.taskflow.domain.model.Task
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AddEditTaskViewModelTest {
    private val repository = mockk<TaskRepository>(relaxed = true)
    private lateinit var viewModel: AddEditTaskViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = AddEditTaskViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `onTitleChange should update uiState`() {
        viewModel.onTitleChange("New Title")
        assertEquals("New Title", viewModel.uiState.value.title)
    }

    @Test
    fun `loadTask should update uiState with task data`() = runTest {
        val task = Task(1, "Loaded", "Desc", Category.WORK, Priority.HIGH, 100L, false)
        coEvery { repository.getTaskById(1L) } returns task
        
        viewModel.loadTask(1L)
        testDispatcher.scheduler.advanceUntilIdle()
        
        val state = viewModel.uiState.value
        assertEquals(1L, state.id)
        assertEquals("Loaded", state.title)
        assertEquals("Desc", state.description)
        assertEquals(Category.WORK, state.category)
        assertEquals(Priority.HIGH, state.priority)
        assertEquals(100L, state.dueDate)
    }

    @Test
    fun `saveTask should insert if id is null`() = runTest {
        viewModel.onTitleChange("New Task")
        viewModel.saveTask {}
        testDispatcher.scheduler.advanceUntilIdle()
        coVerify { repository.insertTask(any()) }
    }
}
