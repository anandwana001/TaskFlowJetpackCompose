package com.androidengineers.taskflow

import android.app.Application
import com.androidengineers.taskflow.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class TaskFlowApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@TaskFlowApplication)
            modules(appModule)
        }
    }
}
