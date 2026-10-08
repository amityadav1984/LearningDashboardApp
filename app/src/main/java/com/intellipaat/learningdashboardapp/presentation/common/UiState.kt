package com.intellipaat.learningdashboardapp.presentation.common

sealed class UiState<out T> {
    data object Idle : UiState<Nothing>()
    data object Loading : UiState<Nothing>()
    data class Success<T>(val data: T, val fromCache: Boolean = false) : UiState<T>()
    data class Empty(val message: String = "Nothing to show yet.") : UiState<Nothing>()
    data class Error(val message: String) : UiState<Nothing>()
}
