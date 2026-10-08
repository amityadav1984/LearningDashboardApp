package com.intellipaat.learningdashboardapp.domain.model

data class Course(
    val id: Int,
    val title: String,
    val instructor: String,
    val lessons: List<Lesson>
) {
    val progressPercent: Int
        get() = ProgressCalculator.calculate(lessons)

    val lessonCount: Int
        get() = lessons.size
}

data class Lesson(
    val id: Int,
    val courseId: Int,
    val title: String,
    val isCompleted: Boolean
)

object ProgressCalculator {
    fun calculate(lessons: List<Lesson>): Int {
        if (lessons.isEmpty()) return 0
        val completed = lessons.count { it.isCompleted }
        return (completed * 100) / lessons.size
    }
}
