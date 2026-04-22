package com.vitalo.markrun.ui.onboarding

import androidx.lifecycle.ViewModel
import com.vitalo.markrun.data.local.prefs.AppPreferences
import com.vitalo.markrun.service.Gender
import com.vitalo.markrun.service.User
import com.vitalo.markrun.service.UserManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val userManager: UserManager,
    private val appPreferences: AppPreferences
) : ViewModel() {

    private val _gender = MutableStateFlow<Gender?>(null)
    val gender = _gender.asStateFlow()

    private val _birthday = MutableStateFlow(
        Calendar.getInstance().apply { set(1999, 0, 1) }.timeInMillis
    )
    val birthday = _birthday.asStateFlow()

    private val _heightCm = MutableStateFlow(160)
    val heightCm = _heightCm.asStateFlow()

    private val _weightKg = MutableStateFlow(55)
    val weightKg = _weightKg.asStateFlow()

    fun setGender(gender: Gender) {
        _gender.value = gender
    }

    fun setBirthday(millis: Long) {
        _birthday.value = millis
    }

    fun setHeightCm(cm: Int) {
        _heightCm.value = cm
    }

    fun setWeightKg(kg: Int) {
        _weightKg.value = kg
    }

    fun completeOnboarding() {
        val user = User(
            gender = _gender.value ?: Gender.FEMALE,
            birthday = _birthday.value,
            height = _heightCm.value,
            weight = _weightKg.value
        )
        userManager.save(user)
        appPreferences.setHasLaunchedBefore(true)
    }

    fun completeWithDefaults() {
        val user = User(
            gender = Gender.FEMALE,
            birthday = Calendar.getInstance().apply { set(1999, 0, 1) }.timeInMillis,
            height = 160,
            weight = 55
        )
        userManager.save(user)
        appPreferences.setHasLaunchedBefore(true)
    }
}
