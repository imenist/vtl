package com.vitalo.markrun.common.ab

import kotlin.reflect.KClass

class AbConfigContract(
    val sid: Int,
    val type: KClass<out BaseAbConfig>
)
