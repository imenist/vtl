package com.vitalo.markrun.data.remote.model

import com.google.gson.annotations.SerializedName

data class CoinType(
    @SerializedName("coin_code") val coinCode: String,
    @SerializedName("coin_name") val coinName: String,
    val description: String?
)

data class CoinTypeList(
    @SerializedName("coin_type") val coinType: List<CoinType>
)

data class CoinInfo(
    @SerializedName("coin_code") val coinCode: String,
    @SerializedName("total_coin") val totalCoin: Double,
    @SerializedName("used_coin") val usedCoin: Double,
    @SerializedName("existing_coin") val existingCoin: Double
)

data class CoinInfoList(
    @SerializedName("coins_info") val coinsInfo: List<CoinInfo>
)
