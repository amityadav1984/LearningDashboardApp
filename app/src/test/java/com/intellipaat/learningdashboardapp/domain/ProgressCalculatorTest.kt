package com.intellipaat.learningdashboardapp.domain

import com.intellipaat.learningdashboardapp.domain.model.Lesson
import com.intellipaat.learningdashboardapp.domain.model.ProgressCalculator
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Meaningful unit test for course progress business logic.
 * Progress is derived from completed/total lessons and drives both
 * dashboard cards and the details screen.
 */
class ProgressCalculatorTest {

    @Test
    fun calculate_returnsExpectedPercentages_forSampleCourses() {
        assertEquals(0, ProgressCalculator.calculate(emptyList()))

        // Python sample: 13 of 20 => 65%
        assertEquals(65, ProgressCalculator.calculate(lessons(completed = 13, total = 20)))

        // Generative AI sample: 6 of 16 => 37%
        assertEquals(37, ProgressCalculator.calculate(lessons(completed = 6, total = 16)))

        // Full Stack sample: 7 of 28 => 25%
        assertEquals(25, ProgressCalculator.calculate(lessons(completed = 7, total = 28)))

        // Completing one more Python lesson: 14/20 => 70%
        assertEquals(70, ProgressCalculator.calculate(lessons(completed = 14, total = 20)))

        // All complete
        assertEquals(100, ProgressCalculator.calculate(lessons(completed = 5, total = 5)))
    }

    private fun lessons(completed: Int, total: Int): List<Lesson> {
        return List(total) { index ->
            Lesson(
                id = index + 1,
                courseId = 1,
                title = "Lesson ${index + 1}",
                isCompleted = index < completed
            )
        }
    }
}
