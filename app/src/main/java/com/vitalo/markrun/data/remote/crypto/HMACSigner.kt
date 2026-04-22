package com.vitalo.markrun.data.remote.crypto

import android.util.Base64
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

object HMACSigner {
    /**
     * HMAC-SHA256 签名，输出 URL-safe Base64（无 padding）。
     * 对应 iOS HMACSigner.hmacSHA256(message:key:)
     */
    fun hmacSHA256(message: String, key: String): String {
        val mac = Mac.getInstance("HmacSHA256")
        mac.init(SecretKeySpec(key.toByteArray(Charsets.UTF_8), "HmacSHA256"))
        val result = mac.doFinal(message.toByteArray(Charsets.UTF_8))
        return Base64.encodeToString(result, Base64.NO_WRAP)
            .replace('+', '-')
            .replace('/', '_')
            .trimEnd('=')
    }
}
