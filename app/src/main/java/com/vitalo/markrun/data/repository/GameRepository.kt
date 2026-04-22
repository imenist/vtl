package com.vitalo.markrun.data.repository

import com.vitalo.markrun.data.remote.api.GameApi
import com.vitalo.markrun.data.remote.model.*
import com.vitalo.markrun.util.DeviceInfoUtils
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GameRepository @Inject constructor(
    private val api: GameApi,
    private val deviceInfoUtils: DeviceInfoUtils
) {
    suspend fun getWithdrawalConfig(): CommonResponse<WithdrawalConfig> {
        return api.getWithdrawalConfig(
            mapOf("device" to deviceInfoUtils.getDeviceInfoMap())
        )
    }

    suspend fun withdraw(
        withdrawCode: String,
        withdrawMethod: Int,
        coinCode: String = "COIN_USD",
        email: String? = null,
        withdrawAccountName: String? = null,
        withdrawAccountId: String? = null
    ): CommonResponse<WithdrawalResult> {
        val payload = mutableMapOf<String, Any>(
            "device" to deviceInfoUtils.getDeviceInfoMap(),
            "withdraw_code" to withdrawCode,
            "withdraw_method" to withdrawMethod,
            "coin_code" to coinCode
        )
        email?.let { payload["email"] = it }
        withdrawAccountName?.let { payload["withdraw_account_name"] = it }
        withdrawAccountId?.let { payload["withdraw_account_id"] = it }
        return api.withdraw(payload)
    }

    suspend fun getWithdrawalRecords(
        startId: Int? = null
    ): CommonResponse<WithdrawalRecords> {
        val payload = mutableMapOf<String, Any>(
            "device" to deviceInfoUtils.getDeviceInfoMap()
        )
        startId?.let { payload["start_id"] = it }
        return api.getWithdrawalRecords(payload)
    }

    suspend fun queryWithdrawalStatus(partner: Int): CommonResponse<MerchantInfos> {
        return api.queryWithdrawalStatus(
            mapOf(
                "device" to deviceInfoUtils.getDeviceInfoMap(),
                "partner" to partner
            )
        )
    }
}
