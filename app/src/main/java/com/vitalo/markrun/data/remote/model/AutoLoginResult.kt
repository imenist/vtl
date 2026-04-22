package com.vitalo.markrun.data.remote.model

import com.google.gson.annotations.SerializedName

data class AutoLoginResult(
    @SerializedName("has_registered")
    val hasRegistered: Boolean,
    @SerializedName("user_id")
    val userId: Int?,
    val token: Token,
    @SerializedName("binding_accounts")
    val bindingAccounts: Int,
    @SerializedName("register_time")
    val registerTime: Int
)
