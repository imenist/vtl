package com.vitalo.markrun.common.ab.impl

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import com.vitalo.markrun.common.ab.BaseAbConfig

@Keep
class WithdrawGradeConfig(
    @SerializedName("2_grade_money")
    val grade2Money: Double = 0.0,
    @SerializedName("no_ad_1_grade__code")
    val noAd1GradeCode: String = "0",
    @SerializedName("2_grade_code")
    val grade2Code: String = "0",
    @SerializedName("no_ad_1_grade__money")
    val noAd1GradeMoney: Double = 0.0,
    @SerializedName("2_grade_need_ads")
    val grade2NeedAds: Int = 0,
    @SerializedName("sign_1_grade__money")
    val sign1GradeMoney: Double = 0.0,
    @SerializedName("sign_1_grade__code")
    val sign1GradeCode: String = "",
    @SerializedName("sign_2_grade__money")
    val sign2GradeMoney: Double = 0.0,
    @SerializedName("sign_2_grade__code")
    val sign2GradeCode: String = "",
    @SerializedName("sign_3_grade__money")
    val sign3GradeMoney: Double = 0.0,
    @SerializedName("sign_3_grade__code")
    val sign3GradeCode: String = "",
    @SerializedName("cfg_tb_id")
    val cfgTbId: Int = 0
) : BaseAbConfig()