package com.intellipaat.learningdashboardapp.presentation.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.intellipaat.learningdashboardapp.domain.model.Course
import com.intellipaat.learningdashboardapp.domain.model.Result
import com.intellipaat.learningdashboardapp.domain.repository.CourseRepository
import com.intellipaat.learningdashboardapp.presentation.common.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class CourseDetailsViewModel(
    private val courseId: Int,
    private val courseRepository: CourseRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<Course>>(UiState.Loading)
    val uiState: StateFlow<UiState<Course>> = _uiState.asStateFlow()

    private val _actionError = MutableStateFlow<String?>(null)
    val actionError: StateFlow<String?> = _actionError.asStateFlow()

    init {
        viewModelScope.launch {
            courseRepository.observeCourse(courseId).collectLatest { course ->
                _uiState.value = when {
                    course != null -> UiState.Success(course)
                    else -> UiState.Error("Course not found. Pull to refresh from the dashboard.")
                }
            }
        }
    }

    fun markLessonCompleted(lessonId: Int) {
        viewModelScope.launch {
            when (val result = courseRepository.markLessonCompleted(courseId, lessonId)) {
                is Result.Success -> {
                    _uiState.value = UiState.Success(result.data)
                    _actionError.value = null
                }
                is Result.Error -> {
                    _actionError.value = result.message
                }
            }
        }
    }

    fun clearActionError() {
        _actionError.value = null
    }
}
