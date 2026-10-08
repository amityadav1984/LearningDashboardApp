package com.intellipaat.learningdashboardapp.domain.repository

import com.intellipaat.learningdashboardapp.domain.model.Course
import com.intellipaat.learningdashboardapp.domain.model.Result
import kotlinx.coroutines.flow.Flow

interface CourseRepository {
    fun observeCourses(): Flow<List<Course>>
    fun observeCourse(courseId: Int): Flow<Course?>
    suspend fun refreshCourses(): Result<List<Course>>
    suspend fun markLessonCompleted(courseId: Int, lessonId: Int): Result<Course>
}
