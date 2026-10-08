package com.intellipaat.learningdashboardapp.data.repository

import com.intellipaat.learningdashboardapp.data.local.CourseLocalDataSource
import com.intellipaat.learningdashboardapp.data.remote.ApiException
import com.intellipaat.learningdashboardapp.data.remote.MockCourseApi
import com.intellipaat.learningdashboardapp.data.remote.NetworkMonitor
import com.intellipaat.learningdashboardapp.data.remote.toDomain
import com.intellipaat.learningdashboardapp.domain.model.Course
import com.intellipaat.learningdashboardapp.domain.model.Result
import com.intellipaat.learningdashboardapp.domain.repository.CourseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CourseRepositoryImpl(
    private val courseApi: MockCourseApi,
    private val localDataSource: CourseLocalDataSource,
    private val networkMonitor: NetworkMonitor
) : CourseRepository {

    override fun observeCourses(): Flow<List<Course>> = localDataSource.courses

    override fun observeCourse(courseId: Int): Flow<Course?> {
        return localDataSource.courses.map { courses ->
            courses.firstOrNull { it.id == courseId }
        }
    }

    override suspend fun refreshCourses(): Result<List<Course>> {
        if (!networkMonitor.isOnline()) {
            return cachedOrError("You're offline. Showing saved courses when available.")
        }

        return try {
            val remote = courseApi.fetchCourses().map { it.toDomain() }
            val merged = mergeWithLocalProgress(remote)
            localDataSource.saveCourses(merged)
            Result.Success(merged, fromCache = false)
        } catch (e: Exception) {
            cachedOrError(e.toUserMessage())
        }
    }

    override suspend fun markLessonCompleted(courseId: Int, lessonId: Int): Result<Course> {
        val updated = localDataSource.markLessonCompleted(courseId, lessonId)
            ?: return Result.Error("Lesson not found.")
        return Result.Success(updated)
    }

    private suspend fun cachedOrError(fallbackMessage: String): Result<List<Course>> {
        val cached = localDataSource.getCourses()
        return if (cached.isNotEmpty()) {
            Result.Success(cached, fromCache = true)
        } else {
            Result.Error(fallbackMessage)
        }
    }

    private suspend fun mergeWithLocalProgress(remote: List<Course>): List<Course> {
        val localCompletedIds = localDataSource.getCourses()
            .flatMap { it.lessons }
            .filter { it.isCompleted }
            .map { it.id }
            .toSet()

        if (localCompletedIds.isEmpty()) return remote

        return remote.map { course ->
            course.copy(
                lessons = course.lessons.map { lesson ->
                    if (lesson.id in localCompletedIds) {
                        lesson.copy(isCompleted = true)
                    } else {
                        lesson
                    }
                }
            )
        }
    }

    private fun Exception.toUserMessage(): String {
        return when (this) {
            is ApiException -> message ?: "Failed to load courses."
            else -> message ?: "Failed to load courses."
        }
    }
}
