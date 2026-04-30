package com.vitalo.markrun.config

open class ProductConfig : IAppConfig {

    override val packageName: String
        get() = "com.markrun.app"

    override val versionCode: Int
        get() = 1

    override val versionName: String
        get() = "1.0"

    // ─── Training ───
    override val trainingBaseUrl: String
        get() = "https://flash-fit-core-api.mark-run.com"
    override val trainingApiKey: String
        get() = "aKWoYVkiCQJ8vJFSyLF5pUqt"
    override val trainingApiSecret: String
        get() = "35x8Kf2ITXqyDJZgEKPad3vAfNLPVkyO"
    override val trainingDesKey: String
        get() = "IMjj2eDqGhY"

    // ─── AccountCenter ───
    override val accountBaseUrl: String
        get() = "https://searn-state.3g.net.cn"
    override val accountApiKey: String
        get() = "9Ni0MqG6wBX0UNDBs8hzHUqs"
    override val accountApiSecret: String
        get() = "ihiRaJrOjAj4smJIs3gevKWqVwd6MpNZ"
    override val accountDesKey: String
        get() = "fXT78Zx6"
    override val isAccountDesKeyEncoded: Boolean
        get() = false

    // ─── Coin (reuses AccountCenter keys) ───
    override val coinBaseUrl: String
        get() = "https://scoins-state.3g.net.cn"

    // ─── Game (reuses AccountCenter keys) ───
    override val gameBaseUrl: String
        get() = "https://sfquiz-state.3g.net.cn"

    // ─── ABTest ───
    override val abTestBaseUrl: String
        get() = "https://control.mark-run.com"
    override val abTestCid: Int
        get() = 1125
    override val abTestCid2: Int
        get() = statProductId
    override val abTestProductKey: String
        get() = "SC2GXN9BBG33RJP8Q7JFAA50"
    override val abTestSecretKey: String
        get() = "MFK71I80F1R89RCLJINUQN1BCYUQ"
    override val abTestRequestPkgName: String
        get() = "com.markrun.app"

    // ─── NewStoreLite (广告) - 正式服 ───
    override val adBaseUrl: String
        get() = "https://newstorelite.gomo.com"
    override val adCid: Int
        get() = 378
    override val adApiKey: String
        get() = "DSqHsqbhe5eIzlxfEZ7lNj5Y"
    override val adSecretKey: String
        get() = "GVhcpQU6VXvammuVLL9oDsfQlaGGtvmf"
    override val adDesKey: String
        get() = "mG5kEDcEYj0"
    override val illusBaseUrl: String
        get() = ""
    override val illusApiKey: String
        get() = ""
    override val illusApiSecret: String
        get() = ""


    override val statBaseUrl: String
        get() = ""
    override val statProductId: Int
        get() = 5351
    override val statChannelId: Int
        get() = 200
    override val functionId19: Int
        get() = 5351
    override val functionId45: Int
        get() = 3735
    override val functionId104: Int
        get() = 3734
    override val functionId105: Int
        get() = 5351
    override val elephantBaseUrl: String get() = ""
    override val elephantProdId: Int get() = 0
    override val elephantProdKey: String get() = ""
    override val elephantAccessKey: String get() = ""
    override val afDevKey: String get() = ""
    override val userFrom: Int get() = 0
    override val buyChannel: String? get() = null
    override val isBuyUser: Boolean get() = false
}
