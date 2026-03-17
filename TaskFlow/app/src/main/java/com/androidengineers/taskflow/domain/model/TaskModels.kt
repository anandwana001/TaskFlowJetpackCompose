package com.androidengineers.taskflow.domain.model

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color

@Stable
enum class Priority(val level: String, val color: Color) {
    LOW("Low", Color(0xFF4CAF50)),
    MEDIUM("Medium", Color(0xFFFFC107)),
    HIGH("High", Color(0xFFF44336))
}

@Stable
enum class Category(val title: String, val color: Color) {
    WORK("Work", Color(0xFF2196F3)),
    PERSONAL("Personal", Color(0xFF9C27B0)),
    SHOPPING("Shopping", Color(0xFFFF9800)),
    HEALTH("Health", Color(0xFFE91E63)),
    OTHER("Other", Color(0xFF9E9E9E))
}

@Immutable
data class Task(
    val id: Long = 0,
    val title: String,
    val description: String,
    val category: Category,
    val priority: Priority,
    val dueDate: Long,
    val isCompleted: Boolean = false
)
