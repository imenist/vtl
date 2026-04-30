package com.vitalo.markrun.config

object AppConfig : IAppConfig {

    private lateinit var current: IAppConfig

    fun install(appConfig: IAppConfig) {
        current = appConfig
    }

    val isInstalled: Boolean
        get() = ::current.isInitialized

    override val packageName: String get() = current.packageName
    override val versionCode: Int get() = current.versionCode
    override val versionName: String get() = current.versionName

    // ─── Training ───
    override val trainingBaseUrl: String get() = current.trainingBaseUrl
    override val trainingApiKey: String get() = current.trainingApiKey
    override val trainingApiSecret: String get() = current.trainingApiSecret
    override val trainingDesKey: String get() = current.trainingDesKey

    // ─── AccountCenter ───
    override val accountBaseUrl: String get() = current.accountBaseUrl
    override val accountApiKey: String get() = current.accountApiKey
    override val accountApiSecret: String get() = current.accountApiSecret
    override val accountDesKey: String get() = current.accountDesKey
    override val isAccountDesKeyEncoded: Boolean get() = current.isAccountDesKeyEncoded

    // ─── Coin ───
    override val coinBaseUrl: String get() = current.coinBaseUrl

    // ─── Game ───
    override val gameBaseUrl: String get() = current.gameBaseUrl

    // ─── ABTest ───
    override val abTestBaseUrl: String get() = current.abTestBaseUrl
    override val abTestCid: Int get() = current.abTestCid
    override val abTestCid2: Int get() = current.abTestCid2
    override val abTestProductKey: String get() = current.abTestProductKey
    override val abTestSecretKey: String get() = current.abTestSecretKey
    override val abTestRequestPkgName: String get() = current.abTestRequestPkgName

    // ─── NewStoreLite (广告) ───
    override val adBaseUrl: String get() = current.adBaseUrl
    override val adCid: Int get() = current.adCid
    override val adApiKey: String get() = current.adApiKey
    override val adSecretKey: String get() = current.adSecretKey
    override val adDesKey: String get() = current.adDesKey
    override val illusBaseUrl: String get() = current.illusBaseUrl
    override val illusApiKey: String get() = current.illusApiKey
    override val illusApiSecret: String get() = current.illusApiSecret

    override val statBaseUrl: String get() = current.statBaseUrl
    override val statProductId: Int get() = current.statProductId
    override val statChannelId: Int get() = current.statChannelId
    override val functionId19: Int get() = current.functionId19
    override val functionId45: Int get() = current.functionId45
    override val functionId104: Int get() = current.functionId104
    override val functionId105: Int get() = current.functionId105
    override val elephantBaseUrl: String get() = current.elephantBaseUrl
    override val elephantProdId: Int get() = current.elephantProdId
    override val elephantProdKey: String get() = current.elephantProdKey
    override val elephantAccessKey: String get() = current.elephantAccessKey
    override val afDevKey: String get() = current.afDevKey
    override val userFrom: Int get() = current.userFrom
    override val buyChannel: String? get() = current.buyChannel
    override val isBuyUser: Boolean get() = current.isBuyUser
}
