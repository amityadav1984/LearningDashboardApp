package com.intellipaat.learningdashboardapp.domain.model

sealed class Result<out T> {
    data class Success<T>(val data: T, val fromCache: Boolean = false) : Result<T>()
    data class Error(val message: String) : Result<Nothing>()
}
