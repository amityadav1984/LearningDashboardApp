package com.intellipaat.learningdashboardapp.presentation.navigation

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.intellipaat.learningdashboardapp.LearningDashboardApplication
import com.intellipaat.learningdashboardapp.presentation.common.ViewModelFactory
import com.intellipaat.learningdashboardapp.presentation.dashboard.DashboardScreen
import com.intellipaat.learningdashboardapp.presentation.dashboard.DashboardViewModel
import com.intellipaat.learningdashboardapp.presentation.details.CourseDetailsScreen
import com.intellipaat.learningdashboardapp.presentation.details.CourseDetailsViewModel
import com.intellipaat.learningdashboardapp.presentation.login.LoginScreen
import com.intellipaat.learningdashboardapp.presentation.login.LoginViewModel

sealed interface Screen {
    data object Login : Screen
    data object Dashboard : Screen
    data class Details(val courseId: Int) : Screen
}

@Composable
fun AppNavGraph() {
    val activity = LocalContext.current as ComponentActivity
    val app = activity.application as LearningDashboardApplication
    val container = remember(app) { app.container }

    var screen by remember { mutableStateOf<Screen>(Screen.Login) }

    when (val current = screen) {
        Screen.Login -> {
            val factory = remember(container) { ViewModelFactory(container) }
            val viewModel = rememberViewModel<LoginViewModel>(
                activity = activity,
                key = "login",
                factory = factory
            )
            LoginScreen(
                viewModel = viewModel,
                onLoginSuccess = { screen = Screen.Dashboard }
            )
        }

        Screen.Dashboard -> {
            val factory = remember(container) { ViewModelFactory(container) }
            val viewModel = rememberViewModel<DashboardViewModel>(
                activity = activity,
                key = "dashboard",
                factory = factory
            )
            DashboardScreen(
                viewModel = viewModel,
                onCourseClick = { courseId -> screen = Screen.Details(courseId) }
            )
        }

        is Screen.Details -> {
            val factory = remember(container, current.courseId) {
                ViewModelFactory(container, current.courseId)
            }
            val viewModel = rememberViewModel<CourseDetailsViewModel>(
                activity = activity,
                key = "details_${current.courseId}",
                factory = factory
            )
            CourseDetailsScreen(
                viewModel = viewModel,
                onBack = { screen = Screen.Dashboard }
            )
        }
    }
}

@Composable
private inline fun <reified VM : ViewModel> rememberViewModel(
    activity: ComponentActivity,
    key: String,
    factory: ViewModelProvider.Factory
): VM {
    return remember(key) {
        ViewModelProvider(activity.viewModelStore, factory)[key, VM::class.java]
    }
}
