package com.androidengineers.taskflow.data.repository

import com.androidengineers.taskflow.data.local.dao.TaskDao
import com.androidengineers.taskflow.data.local.entity.toDomain
import com.androidengineers.taskflow.data.local.entity.toEntity
import com.androidengineers.taskflow.domain.model.Task
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TaskRepository(private val taskDao: TaskDao) {
    fun getAllTasks(): Flow<List<Task>> = taskDao.getAllTasks().map { entities ->
        entities.map { it.toDomain() }
    }

    suspend fun getTaskById(id: Long): Task? = taskDao.getTaskById(id)?.toDomain()

    suspend fun insertTask(task: Task) = taskDao.insertTask(task.toEntity())

    suspend fun updateTask(task: Task) = taskDao.updateTask(task.toEntity())

    suspend fun deleteTask(task: Task) = taskDao.deleteTask(task.toEntity())
}
