package com.vitalo.markrun.data.remote.model

import com.google.gson.annotations.SerializedName

data class WithdrawalConfig(
    @SerializedName("is_today_withdraw") val isTodayWithdraw: Int?,
    @SerializedName("withdraw_amounts") val withdrawAmounts: List<WithdrawalAmount>?,
    @SerializedName("apply_withdraw_status") val applyWithdrawStatus: Int?,
    @SerializedName("quick_withdraw") val quickWithdraw: Int?,
    @SerializedName("is_first_time_withdraw") val isFirstTimeWithdraw: Int?,
    @SerializedName("new_user_exclusive") val newUserExclusive: Int?
)

data class WithdrawalAmount(
    @SerializedName("cash_out_id") val cashOutId: Int?,
    @SerializedName("prod_name") val prodName: String?,
    @SerializedName("filter_id") val filterId: Int?,
    @SerializedName("coin_code") val coinCode: String?,
    @SerializedName("real_currency") val realCurrency: Double?,
    @SerializedName("withdraw_code") val withdrawCode: String?,
    @SerializedName("withdraw_type") val withdrawType: Int?,
    val state: Int?,
    @SerializedName("limit_per_user") val limitPerUser: Int?,
    @SerializedName("limit_user_per_day") val limitUserPerDay: Int?,
    @SerializedName("coin_amount_v2") val coinAmountV2: Double?,
    @SerializedName("common_state") val commonState: Int?,
    @SerializedName("limit_per_day") val limitPerDay: Int?,
    @SerializedName("coin_amount") val coinAmount: Int?,
    @SerializedName("limit_break_times") val limitBreakTimes: Int?,
    @SerializedName("limit_sign_days") val limitSignDays: Int?,
    @SerializedName("limit_clock_days") val limitClockDays: Int?,
    @SerializedName("watch_ad_times") val watchAdTimes: Int?,
    @SerializedName("crack_egg_fragments") val crackEggFragments: Int?
)

data class WithdrawalResult(
    @SerializedName("gc_claim_code") val gcClaimCode: String?,
    val status: Int?,
    @SerializedName("withdraw_id") val withdrawId: String?,
    val queue: Int?
)

data class WithdrawalInfo(
    val amount: Double?,
    @SerializedName("apply_time") val applyTime: Long?,
    @SerializedName("transfer_time") val transferTime: Long?,
    @SerializedName("withdraw_code") val withdrawCode: String?,
    @SerializedName("response_code") val responseCode: String?,
    @SerializedName("withdraw_id") val withdrawId: String?,
    @SerializedName("coin_code") val coinCode: String?,
    @SerializedName("gc_claim_code") val gcClaimCode: String?,
    val queue: Int?,
    @SerializedName("withdraw_method") val withdrawMethod: Int?,
    val email: String?,
    val status: Int?
)

data class WithdrawalRecords(
    @SerializedName("withdraw_infos") val withdrawInfos: List<WithdrawalInfo>?,
    @SerializedName("next_cursor") val nextCursor: Long?
)

data class MerchantInfo(
    val id: Int?,
    val merchant: String?,
    val partner: Int?,
    @SerializedName("account_status") val accountStatus: Int?
)

data class MerchantInfos(
    @SerializedName("merchant_list") val merchantList: List<MerchantInfo>?
)
