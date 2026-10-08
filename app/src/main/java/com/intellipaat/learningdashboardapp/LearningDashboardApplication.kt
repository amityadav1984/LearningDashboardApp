package com.intellipaat.learningdashboardapp

import android.app.Application
import com.intellipaat.learningdashboardapp.di.AppContainer

class LearningDashboardApplication : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
