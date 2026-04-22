package com.vitalo.markrun.ui.collection

import androidx.lifecycle.ViewModel
import com.vitalo.markrun.data.local.prefs.AppPreferences
import com.vitalo.markrun.data.remote.model.Card
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class CollectionViewModel @Inject constructor(
    private val appPreferences: AppPreferences
) : ViewModel() {

    val cards: List<Card> = Card.presets.sortedBy { it.id }

    private val _fragmentProgress = MutableStateFlow<Map<Int, Int>>(emptyMap())
    val fragmentProgress: StateFlow<Map<Int, Int>> = _fragmentProgress

    private val gson = Gson()

    init {
        loadProgress()
    }

    fun loadProgress() {
        val json = appPreferences.getString(AppPreferences.KEY_FRAGMENT_PROGRESS)
        if (json != null) {
            try {
                val type = object : TypeToken<Map<Int, Int>>() {}.type
                _fragmentProgress.value = gson.fromJson(json, type) ?: emptyMap()
            } catch (_: Exception) {
                _fragmentProgress.value = emptyMap()
            }
        }
    }

    private fun saveProgress() {
        val json = gson.toJson(_fragmentProgress.value)
        appPreferences.setString(AppPreferences.KEY_FRAGMENT_PROGRESS, json)
    }

    fun progress(card: Card): Int = _fragmentProgress.value[card.fragmentId] ?: 0

    fun isUnlocked(card: Card): Boolean = progress(card) >= card.fragmentRequiredNum

    fun addFragment(card: Card) {
        val current = _fragmentProgress.value.toMutableMap()
        current[card.fragmentId] = (current[card.fragmentId] ?: 0) + 1
        _fragmentProgress.value = current
        saveProgress()
    }
}
