package com.vitalo.markrun.util

import com.vitalo.markrun.config.AppConfig

object Constants {
    // Training
    val TRAINING_BASE_URL: String get() = AppConfig.trainingBaseUrl
    val TRAINING_API_KEY: String get() = AppConfig.trainingApiKey
    val TRAINING_API_SECRET: String get() = AppConfig.trainingApiSecret
    val TRAINING_DES_KEY: String get() = AppConfig.trainingDesKey

    // AccountCenter
    val ACCOUNT_BASE_URL: String get() = AppConfig.accountBaseUrl
    val ACCOUNT_API_KEY: String get() = AppConfig.accountApiKey
    val ACCOUNT_API_SECRET: String get() = AppConfig.accountApiSecret
    val ACCOUNT_DES_KEY: String get() = AppConfig.accountDesKey
    val IS_ACCOUNT_DES_KEY_ENCODED: Boolean get() = AppConfig.isAccountDesKeyEncoded

    // Coin (reuses AccountCenter keys)
    val COIN_BASE_URL: String get() = AppConfig.coinBaseUrl

    // Game (reuses AccountCenter keys)
    val GAME_BASE_URL: String get() = AppConfig.gameBaseUrl

    // ABTest
    val ABTEST_BASE_URL: String get() = AppConfig.abTestBaseUrl
    val ABTEST_CID: Int get() = AppConfig.abTestCid
    val ABTEST_PRODUCT_KEY: String get() = AppConfig.abTestProductKey
    val ABTEST_SECRET_KEY: String get() = AppConfig.abTestSecretKey

    // NewStoreLite (广告)
    val AD_BASE_URL: String get() = AppConfig.adBaseUrl
    val AD_CID: Int get() = AppConfig.adCid
    val AD_API_KEY: String get() = AppConfig.adApiKey
    val AD_SECRET_KEY: String get() = AppConfig.adSecretKey
    val AD_DES_KEY: String get() = AppConfig.adDesKey

    object WithdrawType {
        const val QUICK = 0
        const val NORMAL = 2
    }
}
