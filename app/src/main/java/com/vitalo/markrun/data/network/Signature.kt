package com.vitalo.markrun.data.network

import android.util.Base64
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

object Signature {
    private const val DELIMITER = '\n'

    fun getSign(uri: String, secret: String, queryString: String, payload: String): String {
        val data = "GET$DELIMITER$uri$DELIMITER$queryString$DELIMITER$payload"
        return sign(secret, data)
    }

    fun postSign(uri: String, secret: String, queryString: String, payload: String): String {
        val data = "POST$DELIMITER$uri$DELIMITER$queryString$DELIMITER$payload"
        return sign(secret, data)
    }

    private fun sign(secret: String, data: String): String {
        val mac = Mac.getInstance("HmacSHA256")
        mac.init(SecretKeySpec(secret.toByteArray(), "HmacSHA256"))
        val digest = mac.doFinal(data.toByteArray())
        return Base64.encodeToString(
            digest,
            Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING
        )
    }
}
