package com.vitalo.markrun.common.ab

import com.vitalo.markrun.common.ab.impl.AdConfig
import com.vitalo.markrun.common.ab.impl.BonusConfig
import com.vitalo.markrun.common.ab.impl.WithDrawConfig

/**
 * AB 实验 SID 表
 * 在 AB 后台创建实验后，将实验 ID（sid）和对应的配置类添加到此处
 *
 * 示例：
 * val PAY = AbConfigContract(1234, PayAbConfig::class)
 * val AD = AbConfigContract(5678, AdAbConfig::class)
 */
object AbSidTable {
    // TODO: 在 AB 后台创建实验后添加
    val AD = AbConfigContract(1931, AdConfig::class)
    val WITHDRAW = AbConfigContract(1933, WithDrawConfig::class)
    val BONUS = AbConfigContract(1935, BonusConfig::class)
}
