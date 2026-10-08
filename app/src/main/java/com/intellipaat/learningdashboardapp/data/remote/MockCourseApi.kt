package com.intellipaat.learningdashboardapp.data.remote

import com.intellipaat.learningdashboardapp.domain.model.Course
import com.intellipaat.learningdashboardapp.domain.model.Lesson
import kotlinx.coroutines.delay

/**
 * Mock course API backed by in-memory JSON-equivalent data.
 * Set [forceFailure] to simulate API outages while testing offline cache.
 */
class MockCourseApi(
    private var forceFailure: Boolean = false
) {

    fun setForceFailure(enabled: Boolean) {
        forceFailure = enabled
    }

    suspend fun fetchCourses(): List<CourseDto> {
        delay(NETWORK_DELAY_MS)
        if (forceFailure) {
            throw ApiException("Unable to reach course service.")
        }
        return SAMPLE_COURSES
    }

    data class CourseDto(
        val id: Int,
        val title: String,
        val instructor: String,
        val lessons: List<LessonDto>
    )

    data class LessonDto(
        val id: Int,
        val title: String,
        val isCompleted: Boolean
    )

    companion object {
        private const val NETWORK_DELAY_MS = 800L

        /**
         * Seeded so initial progress matches the brief examples:
         * Python 65% (13/20), Generative AI 37%≈40% (6/16), Full Stack 25% (7/28).
         */
        private val SAMPLE_COURSES = listOf(
            CourseDto(
                id = 1,
                title = "Python Programming",
                instructor = "John Smith",
                lessons = buildLessons(
                    courseId = 1,
                    titles = listOf(
                        "Introduction",
                        "Variables & Data Types",
                        "Operators",
                        "Control Flow",
                        "Functions",
                        "Lists & Tuples",
                        "Dictionaries",
                        "Sets",
                        "Loops Mastery",
                        "Error Handling",
                        "Modules",
                        "File I/O",
                        "OOP Basics",
                        "Inheritance",
                        "Decorators",
                        "Generators",
                        "Comprehensions",
                        "Testing",
                        "Packaging",
                        "Project Workshop"
                    ),
                    completedCount = 13
                )
            ),
            CourseDto(
                id = 2,
                title = "Generative AI",
                instructor = "Sarah Williams",
                lessons = buildLessons(
                    courseId = 2,
                    titles = listOf(
                        "AI Landscape",
                        "Neural Networks Primer",
                        "Transformers Intro",
                        "Tokenization",
                        "Prompt Engineering",
                        "Few-shot Learning",
                        "RAG Basics",
                        "Embeddings",
                        "Fine-tuning Overview",
                        "Safety & Alignment",
                        "Evaluation Metrics",
                        "Agent Patterns",
                        "Multimodal Models",
                        "Cost Optimization",
                        "Deployment",
                        "Capstone"
                    ),
                    completedCount = 6
                )
            ),
            CourseDto(
                id = 3,
                title = "Full Stack Development",
                instructor = "David Brown",
                lessons = buildLessons(
                    courseId = 3,
                    titles = (1..28).map { index ->
                        when (index) {
                            1 -> "Introduction"
                            2 -> "HTML Foundations"
                            3 -> "CSS Layouts"
                            4 -> "JavaScript Basics"
                            5 -> "DOM APIs"
                            6 -> "Async JS"
                            7 -> "REST Concepts"
                            else -> "Lesson $index"
                        }
                    },
                    completedCount = 7
                )
            )
        )

        private fun buildLessons(
            courseId: Int,
            titles: List<String>,
            completedCount: Int
        ): List<LessonDto> {
            return titles.mapIndexed { index, title ->
                LessonDto(
                    id = courseId * 1000 + index + 1,
                    title = title,
                    isCompleted = index < completedCount
                )
            }
        }
    }
}

fun MockCourseApi.CourseDto.toDomain(): Course {
    return Course(
        id = id,
        title = title,
        instructor = instructor,
        lessons = lessons.map {
            Lesson(
                id = it.id,
                courseId = id,
                title = it.title,
                isCompleted = it.isCompleted
            )
        }
    )
}
