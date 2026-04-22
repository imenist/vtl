package com.vitalo.markrun.service

import android.util.Log
import com.vitalo.markrun.data.local.prefs.AppPreferences
import com.vitalo.markrun.data.remote.model.AutoLoginResult
import com.vitalo.markrun.data.repository.AccountRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LoginManager @Inject constructor(
    private val accountRepository: AccountRepository,
    private val appPreferences: AppPreferences
) {
    private var currentResult: AutoLoginResult? = null
    private var savedTime: Long? = null

    val currentAccessToken: String?
        get() = currentResult?.token?.accessToken

    init {
        loadFromPrefs()
    }

    val isTokenExpired: Boolean
        get() {
            val token = currentResult?.token ?: return true
            val saved = savedTime ?: return true
            return (System.currentTimeMillis() / 1000 - saved) > token.expiredIn
        }

    suspend fun autoLogin(forceRefresh: Boolean = false): String? {
        Log.d("LoginManager", "autoLogin")

        if (forceRefresh) {
            Log.d("LoginManager", "强制刷新")
            return executeAutoLogin()
        }

        // 1. Token not expired
        if (currentResult != null && !isTokenExpired) {
            Log.d("LoginManager", "Token 不为空且未过期")
            return currentResult?.token?.accessToken
        }

        // 2. Try refresh
        Log.d("LoginManager", "Token 为空或已过期，尝试刷新")
        if (currentResult != null && isTokenExpired) {
            val refreshToken = currentResult?.token?.refreshToken
            if (refreshToken != null) {
                try {
                    val resp = accountRepository.refreshToken(refreshToken)
                    if (resp.isSuccess && resp.data != null) {
                        Log.d("LoginManager", "refreshToken 成功")
                        val oldResult = currentResult!!
                        val newResult = oldResult.copy(
                            token = resp.data
                        )
                        saveResult(newResult)
                        return resp.data.accessToken
                    }
                    if (resp.errorCode == 3008) {
                        Log.d("LoginManager", "refreshToken 失效，走 autoLogin")
                        return executeAutoLogin()
                    }
                } catch (e: Exception) {
                    Log.e("LoginManager", "refreshToken 异常", e)
                }
            }
        }

        // 3. Execute auto login
        return executeAutoLogin()
    }

    private suspend fun executeAutoLogin(): String? {
        return try {
            val resp = accountRepository.autoLogin()
            if (resp.isSuccess && resp.data != null) {
                Log.d("LoginManager", "autoLogin 成功")
                saveResult(resp.data)
                resp.data.token.accessToken
            } else {
                Log.d("LoginManager", "autoLogin 错误码: ${resp.errorCode}")
                null
            }
        } catch (e: Exception) {
            Log.e("LoginManager", "autoLogin 异常", e)
            null
        }
    }

    private fun saveResult(result: AutoLoginResult) {
        currentResult = result
        val now = System.currentTimeMillis() / 1000
        savedTime = now
        appPreferences.setCodable(AppPreferences.KEY_AUTO_LOGIN_RESULT, result)
        appPreferences.setLong(AppPreferences.KEY_AUTO_LOGIN_SAVED_TIME, now)
    }

    private fun loadFromPrefs() {
        currentResult = appPreferences.getCodable(
            AppPreferences.KEY_AUTO_LOGIN_RESULT,
            AutoLoginResult::class.java
        )
        val time = appPreferences.getLong(AppPreferences.KEY_AUTO_LOGIN_SAVED_TIME)
        savedTime = if (time > 0) time else null
    }

    fun clearLoginResult() {
        currentResult = null
        savedTime = null
        appPreferences.remove(AppPreferences.KEY_AUTO_LOGIN_RESULT)
        appPreferences.remove(AppPreferences.KEY_AUTO_LOGIN_SAVED_TIME)
    }
}
