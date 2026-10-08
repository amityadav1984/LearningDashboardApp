package com.intellipaat.learningdashboardapp.data.remote

import kotlinx.coroutines.delay

/**
 * Mock authentication API.
 * Demo credentials: student@demo.com / password123
 * Any other valid email + password (>= 6 chars) also succeeds.
 * Use fail@demo.com to force an API error.
 */
class MockAuthApi {

    suspend fun login(email: String, password: String): AuthResponse {
        delay(NETWORK_DELAY_MS)

        if (email.equals(FORCE_FAIL_EMAIL, ignoreCase = true)) {
            throw ApiException("Authentication service unavailable. Please try again.")
        }

        val normalized = email.trim().lowercase()
        val isDemoUser = normalized == DEMO_EMAIL && password == DEMO_PASSWORD
        val isGenericValid = password.length >= 6

        if (isDemoUser || isGenericValid) {
            return AuthResponse(token = "mock-token-$normalized")
        }

        throw ApiException("Invalid email or password.")
    }

    data class AuthResponse(val token: String)

    companion object {
        const val DEMO_EMAIL = "student@demo.com"
        const val DEMO_PASSWORD = "password123"
        private const val FORCE_FAIL_EMAIL = "fail@demo.com"
        private const val NETWORK_DELAY_MS = 900L
    }
}

class ApiException(message: String) : Exception(message)
