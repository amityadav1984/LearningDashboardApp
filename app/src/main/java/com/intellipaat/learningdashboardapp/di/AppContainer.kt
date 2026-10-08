package com.intellipaat.learningdashboardapp.di

import android.content.Context
import com.intellipaat.learningdashboardapp.data.local.CourseLocalDataSource
import com.intellipaat.learningdashboardapp.data.remote.MockAuthApi
import com.intellipaat.learningdashboardapp.data.remote.MockCourseApi
import com.intellipaat.learningdashboardapp.data.remote.NetworkMonitor
import com.intellipaat.learningdashboardapp.data.repository.AuthRepositoryImpl
import com.intellipaat.learningdashboardapp.data.repository.CourseRepositoryImpl
import com.intellipaat.learningdashboardapp.domain.repository.AuthRepository
import com.intellipaat.learningdashboardapp.domain.repository.CourseRepository

/**
 * Lightweight manual DI container. Keeps dependencies explicit without Hilt boilerplate.
 */
class AppContainer(context: Context) {

    private val localDataSource = CourseLocalDataSource(context)
    private val networkMonitor = NetworkMonitor(context)

    private val authApi = MockAuthApi()
    val courseApi = MockCourseApi()

    val authRepository: AuthRepository = AuthRepositoryImpl(authApi)
    val courseRepository: CourseRepository = CourseRepositoryImpl(
        courseApi = courseApi,
        localDataSource = localDataSource,
        networkMonitor = networkMonitor
    )
}
