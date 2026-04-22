package com.vitalo.markrun.ab

import kotlin.reflect.KClass

/**
 * 描述一个 AB 实验：
 * @param sid  在 AB 后台创建实验后分配的实验 ID
 * @param type 与该实验对应的配置数据类
 */
class AbConfigContract(
    val sid: Int,
    val type: KClass<out BaseAbConfig>
)
