package com.vitalo.markrun.config

interface IAppConfig {
    val packageName: String
    val versionCode: Int
    val versionName: String

    // ─── Training ───
    val trainingBaseUrl: String
    val trainingApiKey: String
    val trainingApiSecret: String
    val trainingDesKey: String

    // ─── AccountCenter ───
    val accountBaseUrl: String
    val accountApiKey: String
    val accountApiSecret: String
    val accountDesKey: String
    val isAccountDesKeyEncoded: Boolean

    // ─── Coin ───
    val coinBaseUrl: String

    // ─── Game ───
    val gameBaseUrl: String

    // ─── ABTest ───
    val abTestBaseUrl: String
    val abTestCid: String
    val abTestProductKey: String
    val abTestAccessKey: String
    val abTestSecretKey: String
    val abTestEntrance: Int

    // ─── NewStoreLite (广告) ───
    val adBaseUrl: String
    val adCid: Int
    val adApiKey: String
    val adSecretKey: String
    val adDesKey: String

    val illusBaseUrl: String
    val illusApiKey: String
    val illusApiSecret: String
    val statBaseUrl: String
    val statProductId: Int
    val statChannelId: Int
    val functionId19: Int
    val functionId45: Int
    val functionId59: Int
    val functionId104: Int
    val functionId105: Int
    val elephantBaseUrl: String
    val elephantProdId: Int
    val elephantProdKey: String
    val elephantAccessKey: String
    val afDevKey: String
    val userFrom: Int
    val buyChannel: String?
    val isBuyUser: Boolean
}
