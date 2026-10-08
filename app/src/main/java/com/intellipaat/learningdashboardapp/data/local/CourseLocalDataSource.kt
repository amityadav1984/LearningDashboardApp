package com.intellipaat.learningdashboardapp.data.local

import android.content.Context
import com.intellipaat.learningdashboardapp.domain.model.Course
import com.intellipaat.learningdashboardapp.domain.model.Lesson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

/**
 * Simple JSON file cache under app filesDir.
 * Enough for offline read/write without Room/SQLite complexity.
 */
class CourseLocalDataSource(context: Context) {

    private val cacheFile = File(context.filesDir, "courses_cache.json")
    private val mutex = Mutex()
    private val _courses = MutableStateFlow<List<Course>>(emptyList())
    val courses: StateFlow<List<Course>> = _courses.asStateFlow()

    init {
        _courses.value = readFromDisk()
    }

    suspend fun saveCourses(courses: List<Course>) = mutex.withLock {
        writeToDisk(courses)
        _courses.value = courses
    }

    suspend fun getCourses(): List<Course> = mutex.withLock {
        _courses.value.ifEmpty { readFromDisk().also { _courses.value = it } }
    }

    suspend fun getCourse(courseId: Int): Course? = getCourses().firstOrNull { it.id == courseId }

    suspend fun markLessonCompleted(courseId: Int, lessonId: Int): Course? = mutex.withLock {
        val current = _courses.value.ifEmpty { readFromDisk() }
        val updated = current.map { course ->
            if (course.id != courseId) {
                course
            } else {
                course.copy(
                    lessons = course.lessons.map { lesson ->
                        if (lesson.id == lessonId) lesson.copy(isCompleted = true) else lesson
                    }
                )
            }
        }
        val course = updated.firstOrNull { it.id == courseId } ?: return@withLock null
        val lessonExists = course.lessons.any { it.id == lessonId }
        if (!lessonExists) return@withLock null

        writeToDisk(updated)
        _courses.value = updated
        course
    }

    private fun readFromDisk(): List<Course> {
        if (!cacheFile.exists()) return emptyList()
        return runCatching {
            val root = JSONArray(cacheFile.readText())
            buildList {
                for (i in 0 until root.length()) {
                    add(root.getJSONObject(i).toCourse())
                }
            }
        }.getOrDefault(emptyList())
    }

    private fun writeToDisk(courses: List<Course>) {
        val root = JSONArray()
        courses.forEach { course ->
            root.put(course.toJson())
        }
        cacheFile.writeText(root.toString())
    }

    private fun Course.toJson(): JSONObject {
        val lessonsJson = JSONArray()
        lessons.forEach { lesson ->
            lessonsJson.put(
                JSONObject()
                    .put("id", lesson.id)
                    .put("courseId", lesson.courseId)
                    .put("title", lesson.title)
                    .put("isCompleted", lesson.isCompleted)
            )
        }
        return JSONObject()
            .put("id", id)
            .put("title", title)
            .put("instructor", instructor)
            .put("lessons", lessonsJson)
    }

    private fun JSONObject.toCourse(): Course {
        val lessonsJson = getJSONArray("lessons")
        val lessons = buildList {
            for (i in 0 until lessonsJson.length()) {
                val item = lessonsJson.getJSONObject(i)
                add(
                    Lesson(
                        id = item.getInt("id"),
                        courseId = item.getInt("courseId"),
                        title = item.getString("title"),
                        isCompleted = item.getBoolean("isCompleted")
                    )
                )
            }
        }
        return Course(
            id = getInt("id"),
            title = getString("title"),
            instructor = getString("instructor"),
            lessons = lessons
        )
    }
}
