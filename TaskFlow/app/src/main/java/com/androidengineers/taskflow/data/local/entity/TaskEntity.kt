package com.androidengineers.taskflow.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.androidengineers.taskflow.domain.model.Category
import com.androidengineers.taskflow.domain.model.Priority
import com.androidengineers.taskflow.domain.model.Task

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String,
    val category: Category,
    val priority: Priority,
    val dueDate: Long,
    val isCompleted: Boolean = false
)

fun TaskEntity.toDomain(): Task = Task(
    id = id,
    title = title,
    description = description,
    category = category,
    priority = priority,
    dueDate = dueDate,
    isCompleted = isCompleted
)

fun Task.toEntity(): TaskEntity = TaskEntity(
    id = id,
    title = title,
    description = description,
    category = category,
    priority = priority,
    dueDate = dueDate,
    isCompleted = isCompleted
)
