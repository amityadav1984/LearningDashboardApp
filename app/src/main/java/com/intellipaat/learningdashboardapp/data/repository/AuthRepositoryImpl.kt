package com.intellipaat.learningdashboardapp.data.repository

import com.intellipaat.learningdashboardapp.data.remote.ApiException
import com.intellipaat.learningdashboardapp.data.remote.MockAuthApi
import com.intellipaat.learningdashboardapp.domain.model.Result
import com.intellipaat.learningdashboardapp.domain.repository.AuthRepository

class AuthRepositoryImpl(
    private val authApi: MockAuthApi
) : AuthRepository {

    override suspend fun login(email: String, password: String): Result<Unit> {
        return try {
            authApi.login(email, password)
            Result.Success(Unit)
        } catch (e: ApiException) {
            Result.Error(e.message ?: "Login failed.")
        } catch (e: Exception) {
            Result.Error(e.message ?: "Unexpected login error.")
        }
    }
}
