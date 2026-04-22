package com.vitalo.markrun.ui.lesson

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vitalo.markrun.data.remote.model.Lesson
import com.vitalo.markrun.data.remote.model.Subject
import com.vitalo.markrun.data.remote.model.Training
import com.vitalo.markrun.data.repository.TrainingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class LessonViewModel @Inject constructor(
    private val repository: TrainingRepository
) : ViewModel() {

    private val _subjects = MutableStateFlow<List<Subject>>(emptyList())
    val subjects: StateFlow<List<Subject>> = _subjects.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isError = MutableStateFlow(false)
    val isError: StateFlow<Boolean> = _isError.asStateFlow()

    private val _lessons = MutableStateFlow<Map<String, Lesson>>(emptyMap())
    val lessons: StateFlow<Map<String, Lesson>> = _lessons.asStateFlow()

    private var cursor = 0

    fun loadInitialData() {
        if (_isLoading.value) return
        cursor = 0
        _isLoading.value = true
        _isError.value = false

        viewModelScope.launch {
            try {
                val response = repository.fetchSubjectList(cursor)
                Log.d("LessonViewModel", "fetchSubjectList response: isSuccess=${response.isSuccess}, errorCode=${response.errorCode}, errorMessage=${response.errorMessage}")
                if (response.isSuccess && response.data != null) {
                    _subjects.value = response.data.subject ?: emptyList()
                    cursor = response.data.cursor ?: 0
                    loadTodayLessons()
                } else {
                    Log.e("LessonViewModel", "API error: code=${response.errorCode}, message=${response.errorMessage}")
                }
            } catch (e: Exception) {
                Log.e("LessonViewModel", "Network exception: ${e.message}", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun reload() {
        loadInitialData()
    }

    private suspend fun loadTodayLessons() {
        val todaySubject = getTodaySubject() ?: return
        val trainings = todaySubject.training ?: return
        val codesToFetch = trainings.map { it.code }.filter { _lessons.value[it] == null }
        if (codesToFetch.isEmpty()) return

        val results = codesToFetch.map { code ->
            viewModelScope.async {
                try {
                    val resp = repository.fetchLessonDetail(code)
                    if (resp.isSuccess && resp.data != null) code to resp.data
                    else null
                } catch (_: Exception) {
                    null
                }
            }
        }.awaitAll().filterNotNull()

        val updated = _lessons.value.toMutableMap()
        results.forEach { (code, lesson) -> updated[code] = lesson }
        _lessons.value = updated
    }

    fun getTodaySubject(): Subject? {
        val calendar = Calendar.getInstance()
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) // 1=Sun, 2=Mon...
        val index = (dayOfWeek + 5) % 7 // Mon=0, Sun=6
        return _subjects.value.getOrNull(index)
    }

    // ─── Mock Data for UI Testing ───

    private fun getMockSubjects(): List<Subject> {
        val dayNames = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")
        val trainingNames = listOf(
            listOf("Full Body Burn", "Core Crusher", "HIIT Blast"),
            listOf("Upper Body Power", "Arm Toning", "Shoulder Sculpt"),
            listOf("Lower Body Firm", "Glute Bridge", "Leg Day"),
            listOf("Cardio Burst", "Jump Training", "Speed Run"),
            listOf("Flexibility Flow", "Morning Stretch", "Yoga Basics"),
            listOf("Total Body Strength", "Functional Fit", "Bodyweight Pro"),
            listOf("Active Recovery", "Easy Stretch", "Cool Down")
        )
        val coverUrls = listOf(
            "https://images.unsplash.com/photo-1571019614242-c5c5dee9f50b?w=480&h=320&fit=crop",
            "https://images.unsplash.com/photo-1534438327276-14e5300c3a48?w=480&h=320&fit=crop",
            "https://images.unsplash.com/photo-1517836357463-d25dfeac3438?w=480&h=320&fit=crop",
            "https://images.unsplash.com/photo-1549060279-7e168fcee0c2?w=480&h=320&fit=crop",
            "https://images.unsplash.com/photo-1518611012118-696072aa579a?w=480&h=320&fit=crop",
            "https://images.unsplash.com/photo-1601422407692-ec4eeec1d9b3?w=480&h=320&fit=crop",
            "https://images.unsplash.com/photo-1544367567-0f2fcb009e0b?w=480&h=320&fit=crop"
        )

        return dayNames.mapIndexed { dayIndex, dayName ->
            Subject(
                subjectId = dayIndex + 1,
                name = dayName,
                label = listOf(dayIndex + 1),
                training = trainingNames[dayIndex].mapIndexed { tIndex, tName ->
                    Training(
                        code = "mock_training_${dayIndex}_${tIndex}",
                        name = tName,
                        type = 1,
                        cover = coverUrls[(dayIndex + tIndex) % coverUrls.size],
                        coverSize = "480x320",
                        video = null,
                        durationType = 1,
                        duration = listOf(600, 900, 1200, 1500, 1800)[(dayIndex + tIndex) % 5],
                        calorie = listOf(120.0, 180.0, 250.0, 300.0, 150.0)[(dayIndex + tIndex) % 5],
                        parts = null
                    )
                },
                style = null
            )
        }
    }
}
