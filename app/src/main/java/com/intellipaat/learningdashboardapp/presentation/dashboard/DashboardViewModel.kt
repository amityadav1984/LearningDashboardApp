package com.intellipaat.learningdashboardapp.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.intellipaat.learningdashboardapp.domain.model.Course
import com.intellipaat.learningdashboardapp.domain.model.Result
import com.intellipaat.learningdashboardapp.domain.repository.CourseRepository
import com.intellipaat.learningdashboardapp.presentation.common.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class DashboardUiModel(
    val courses: List<Course> = emptyList(),
    val fromCache: Boolean = false
)

class DashboardViewModel(
    private val courseRepository: CourseRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<DashboardUiModel>>(UiState.Loading)
    val uiState: StateFlow<UiState<DashboardUiModel>> = _uiState.asStateFlow()

    val cachedCourses: StateFlow<List<Course>> = courseRepository.observeCourses()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            when (val result = courseRepository.refreshCourses()) {
                is Result.Success -> {
                    if (result.data.isEmpty()) {
                        _uiState.value = UiState.Empty("No courses enrolled yet.")
                    } else {
                        _uiState.value = UiState.Success(
                            DashboardUiModel(
                                courses = result.data,
                                fromCache = result.fromCache
                            )
                        )
                    }
                }
                is Result.Error -> {
                    val local = cachedCourses.value
                    if (local.isNotEmpty()) {
                        _uiState.value = UiState.Success(
                            DashboardUiModel(courses = local, fromCache = true)
                        )
                    } else {
                        _uiState.value = UiState.Error(result.message)
                    }
                }
            }
        }
    }
}
