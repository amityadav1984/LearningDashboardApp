package com.intellipaat.learningdashboardapp.domain.repository

import com.intellipaat.learningdashboardapp.domain.model.Result

interface AuthRepository {
    suspend fun login(email: String, password: String): Result<Unit>
}
