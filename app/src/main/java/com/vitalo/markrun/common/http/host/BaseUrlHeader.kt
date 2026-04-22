package com.vitalo.markrun.common.http.host

object BaseUrlHeader {
    const val KEY = "host"
    const val TAG = "tag"

    const val URL_TYPE_ILLUS = "illus"
    const val URL_TYPE_AD = "ad"
    const val URL_TYPE_AB = "ab"
    const val URL_TYPE_STAT = "stat"
    const val URL_TYPE_ELEPHANT = "elephant"
    const val URL_TYPE_ACCOUNT = "account"

    const val BASE_URL_ILLUS_HEADER = "$KEY:$URL_TYPE_ILLUS"
    const val BASE_URL_AD_HEADER = "$KEY:$URL_TYPE_AD"
    const val BASE_URL_AB_HEADER = "$KEY:$URL_TYPE_AB"
    const val BASE_URL_STAT_HEADER = "$KEY:$URL_TYPE_STAT"
    const val BASE_URL_ELEPHANT_HEADER = "$KEY:$URL_TYPE_ELEPHANT"
    const val BASE_URL_ACCOUNT_HEADER = "$KEY:$URL_TYPE_ACCOUNT"

    const val NOT_INTERCEPTOR = "not interceptor"
    const val NOT_INTERCEPTOR_HEADER = "$TAG: $NOT_INTERCEPTOR"
}
