package com.vitalo.markrun.data.repository

import com.vitalo.markrun.data.remote.api.CoinApi
import com.vitalo.markrun.data.remote.model.*
import com.vitalo.markrun.util.DeviceInfoUtils
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CoinRepository @Inject constructor(
    private val api: CoinApi,
    private val deviceInfoUtils: DeviceInfoUtils
) {
    suspend fun getCoinTypes(): CommonResponse<CoinTypeList> {
        return api.getCoinTypes(mapOf("device" to deviceInfoUtils.getDeviceInfoMap()))
    }

    suspend fun getCoinInfos(): CommonResponse<CoinInfoList> {
        return api.getCoinInfos(mapOf("device" to deviceInfoUtils.getDeviceInfoMap()))
    }

    suspend fun orderOptCoin(
        optType: Int,
        coinCode: String,
        optCoin: Double,
        description: String
    ): CommonResponse<String> {
        return api.orderOptCoin(
            mapOf(
                "device" to deviceInfoUtils.getDeviceInfoMap(),
                "opt_type" to optType,
                "coin_code" to coinCode,
                "opt_coin" to optCoin,
                "description" to description
            )
        )
    }

    suspend fun optCoin(tranId: String): CommonResponse<EmptyData> {
        return api.optCoin(
            mapOf(
                "device" to deviceInfoUtils.getDeviceInfoMap(),
                "tran_id" to tranId
            )
        )
    }
}
