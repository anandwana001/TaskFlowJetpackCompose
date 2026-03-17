package com.androidengineers.taskflow.ui.screens.addedit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androidengineers.taskflow.data.repository.TaskRepository
import com.androidengineers.taskflow.domain.model.Category
import com.androidengineers.taskflow.domain.model.Priority
import com.androidengineers.taskflow.domain.model.Task
import androidx.compose.runtime.Immutable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AddEditTaskViewModel(private val repository: TaskRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(AddEditTaskUiState())
    val uiState = _uiState.asStateFlow()

    fun onTitleChange(title: String) {
        _uiState.value = _uiState.value.copy(title = title)
    }

    fun onDescriptionChange(description: String) {
        _uiState.value = _uiState.value.copy(description = description)
    }

    fun onCategoryChange(category: Category) {
        _uiState.value = _uiState.value.copy(category = category)
    }

    fun onPriorityChange(priority: Priority) {
        _uiState.value = _uiState.value.copy(priority = priority)
    }

    fun onDueDateChange(dueDate: Long) {
        _uiState.value = _uiState.value.copy(dueDate = dueDate)
    }

    fun loadTask(taskId: Long) {
        if (taskId == -1L) return
        viewModelScope.launch {
            repository.getTaskById(taskId)?.let { task ->
                _uiState.value = AddEditTaskUiState(
                    id = task.id,
                    title = task.title,
                    description = task.description,
                    category = task.category,
                    priority = task.priority,
                    dueDate = task.dueDate,
                    isCompleted = task.isCompleted
                )
            }
        }
    }

    fun saveTask(onSuccess: () -> Unit) {
        viewModelScope.launch {
            val state = _uiState.value
            val task = Task(
                id = state.id ?: 0,
                title = state.title,
                description = state.description,
                category = state.category,
                priority = state.priority,
                dueDate = state.dueDate,
                isCompleted = state.isCompleted
            )
            if (state.id == null) {
                repository.insertTask(task)
            } else {
                repository.updateTask(task)
            }
            onSuccess()
        }
    }
}

@Immutable
data class AddEditTaskUiState(
    val id: Long? = null,
    val title: String = "",
    val description: String = "",
    val category: Category = Category.OTHER,
    val priority: Priority = Priority.LOW,
    val dueDate: Long = System.currentTimeMillis(),
    val isCompleted: Boolean = false
)
