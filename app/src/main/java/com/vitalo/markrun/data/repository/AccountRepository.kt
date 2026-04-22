package com.vitalo.markrun.data.repository

import com.vitalo.markrun.data.remote.api.AccountApi
import com.vitalo.markrun.data.remote.model.AutoLoginResult
import com.vitalo.markrun.data.remote.model.CommonResponse
import com.vitalo.markrun.data.remote.model.Token
import com.vitalo.markrun.util.DeviceInfoUtils
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AccountRepository @Inject constructor(
    private val api: AccountApi,
    private val deviceInfoUtils: DeviceInfoUtils
) {
    suspend fun autoLogin(): CommonResponse<AutoLoginResult> {
        val deviceInfo = deviceInfoUtils.getDeviceInfoMap()
        return api.autoLogin(mapOf("device" to deviceInfo))
    }

    suspend fun refreshToken(refreshToken: String): CommonResponse<Token> {
        val deviceInfo = deviceInfoUtils.getDeviceInfoMap()
        val response = api.refreshToken(
            mapOf(
                "device" to deviceInfo,
                "refresh_token" to refreshToken
            )
        )
        return CommonResponse(
            errorCode = response.errorCode,
            errorMessage = response.errorMessage,
            data = response.data?.token
        )
    }
}
