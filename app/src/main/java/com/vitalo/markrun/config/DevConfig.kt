package com.vitalo.markrun.config

class DevConfig : ProductConfig() {

    override val packageName: String
        get() = "com.vitalo.markrun.dev"

    // Dev 环境暂时沿用与生产相同的 API 配置（iOS 的 key）
    // 如有测试环境 URL，在此覆盖即可：
    override val trainingBaseUrl: String
        get() = "https://flash-fit-core-api-stage.3g.net.cn"
    override val trainingApiKey: String
        get() = "ZsxOwSEQvhquD3jSAHkF8z0q"
    override val trainingApiSecret: String
        get() = "wFID5SOkNZtWOAsWSoyCozlIIQWiX6B6"
    override val trainingDesKey: String
        get() = "qEXOutWnp54"

    // ─── NewStoreLite (广告) - 测试服 ───
    override val adBaseUrl: String
        get() = "http://newstorelite-core-api-stage.3g.net.cn"
    override val adCid: Int
        get() = 378
    override val adApiKey: String
        get() = "s0S06qFxp7javlJJiU0U6dzV"
    override val adSecretKey: String
        get() = "uSBFyZcTUBa78dpeDq03gWRrPzXz1BGj"
    override val adDesKey: String
        get() = "yHn9hkWwQ1Q"

    // ─── AccountCenter (测试环境) ───
    override val accountApiKey: String
        get() = "EvWYHmdkhdswjYyoqoERfsxz"
    override val accountApiSecret: String
        get() = "2l7Pty4TM6qd3Rk6nohSbDSf7wAzff2d"
    override val accountDesKey: String
        get() = "mKZBgAx4"
}
