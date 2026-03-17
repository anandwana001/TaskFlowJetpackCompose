package com.androidengineers.taskflow.data.local

import androidx.room.TypeConverter
import com.androidengineers.taskflow.domain.model.Category
import com.androidengineers.taskflow.domain.model.Priority

class Converters {
    @TypeConverter
    fun fromPriority(priority: Priority): String = priority.name

    @TypeConverter
    fun toPriority(priority: String): Priority = Priority.valueOf(priority)

    @TypeConverter
    fun fromCategory(category: Category): String = category.name

    @TypeConverter
    fun toCategory(category: String): Category = Category.valueOf(category)
}
