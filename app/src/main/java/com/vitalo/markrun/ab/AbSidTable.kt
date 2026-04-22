package com.vitalo.markrun.ab

import com.vitalo.markrun.ab.impl.CommonAbConfig

/**
 * AB 实验 SID 注册表。
 *
 * 每次在 AB 后台创建新实验后，在此处添加一行：
 *   val MY_EXP = AbConfigContract(<sid>, MyExpConfig::class)
 *
 * 然后在 [AbManager.contracts] 的 init 块里把它加进去即可。
 *
 * 示例（当前占位）：
 *   val TASK  = AbConfigContract(2001, TaskAbConfig::class)
 *   val STORE = AbConfigContract(2002, StoreAbConfig::class)
 */
object AbSidTable {
    // TODO: 在 AB 后台创建实验后，在此注册真实的 SID 和对应的 Config 类
    val COMMON = AbConfigContract(999, CommonAbConfig::class)
}
