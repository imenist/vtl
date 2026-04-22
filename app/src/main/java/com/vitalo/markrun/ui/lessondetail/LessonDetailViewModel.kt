package com.vitalo.markrun.ui.lessondetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vitalo.markrun.data.remote.model.Lesson
import com.vitalo.markrun.data.repository.TrainingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LessonDetailViewModel @Inject constructor(
    private val repository: TrainingRepository
) : ViewModel() {

    private val _lesson = MutableStateFlow<Lesson?>(null)
    val lesson: StateFlow<Lesson?> = _lesson.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isError = MutableStateFlow(false)
    val isError: StateFlow<Boolean> = _isError.asStateFlow()

    private var currentCode: String? = null

    fun load(code: String) {
        currentCode = code
        _isLoading.value = true
        _isError.value = false

        viewModelScope.launch {
            try {
                val response = repository.fetchLessonDetail(code)
                if (response.isSuccess && response.data != null) {
                    _lesson.value = response.data
                } else {
                    _lesson.value = null
                    _isError.value = true
                }
            } catch (_: Exception) {
                _isError.value = true
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun reload() {
        currentCode?.let { load(it) }
    }
}
