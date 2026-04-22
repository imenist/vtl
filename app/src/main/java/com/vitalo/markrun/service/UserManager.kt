package com.vitalo.markrun.service

import com.vitalo.markrun.data.local.prefs.AppPreferences
import javax.inject.Inject
import javax.inject.Singleton

enum class Gender {
    FEMALE, MALE
}

data class User(
    val gender: Gender?,
    val birthday: Long, // Unix timestamp millis
    val height: Int,    // cm
    val weight: Int     // kg
)

@Singleton
class UserManager @Inject constructor(
    private val appPreferences: AppPreferences
) {
    var currentUser: User? = null
        private set

    init {
        currentUser = load()
    }

    fun save(user: User) {
        appPreferences.setCodable(AppPreferences.KEY_CURRENT_USER, user)
        currentUser = user
    }

    fun load(): User? {
        return appPreferences.getCodable(AppPreferences.KEY_CURRENT_USER, User::class.java)
    }

    fun exists(): Boolean = currentUser != null
}
