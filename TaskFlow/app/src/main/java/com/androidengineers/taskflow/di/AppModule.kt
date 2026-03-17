package com.androidengineers.taskflow.di

import androidx.room.Room
import com.androidengineers.taskflow.data.local.AppDatabase
import com.androidengineers.taskflow.data.repository.TaskRepository
import com.androidengineers.taskflow.ui.screens.addedit.AddEditTaskViewModel
import com.androidengineers.taskflow.ui.screens.tasklist.TaskListViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single {
        Room.databaseBuilder(
            androidContext(),
            AppDatabase::class.java,
            "task_flow_db"
        ).build()
    }

    single { get<AppDatabase>().taskDao() }
    single { TaskRepository(get()) }

    viewModel { TaskListViewModel(get()) }
    viewModel { AddEditTaskViewModel(get()) }
}
