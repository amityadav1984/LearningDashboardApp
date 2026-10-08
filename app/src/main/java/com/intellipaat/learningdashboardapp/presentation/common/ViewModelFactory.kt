package com.intellipaat.learningdashboardapp.presentation.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.intellipaat.learningdashboardapp.di.AppContainer
import com.intellipaat.learningdashboardapp.presentation.dashboard.DashboardViewModel
import com.intellipaat.learningdashboardapp.presentation.details.CourseDetailsViewModel
import com.intellipaat.learningdashboardapp.presentation.login.LoginViewModel

class ViewModelFactory(
    private val container: AppContainer,
    private val courseId: Int? = null
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(container.authRepository) as T
            }
            modelClass.isAssignableFrom(DashboardViewModel::class.java) -> {
                DashboardViewModel(container.courseRepository) as T
            }
            modelClass.isAssignableFrom(CourseDetailsViewModel::class.java) -> {
                val id = requireNotNull(courseId) { "courseId is required for CourseDetailsViewModel" }
                CourseDetailsViewModel(id, container.courseRepository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel: ${modelClass.name}")
        }
    }
}
