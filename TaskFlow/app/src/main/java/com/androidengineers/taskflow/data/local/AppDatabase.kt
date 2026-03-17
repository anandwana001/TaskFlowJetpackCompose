package com.androidengineers.taskflow.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.androidengineers.taskflow.data.local.dao.TaskDao
import com.androidengineers.taskflow.data.local.entity.TaskEntity

@Database(entities = [TaskEntity::class], version = 1, exportSchema = true)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
}
