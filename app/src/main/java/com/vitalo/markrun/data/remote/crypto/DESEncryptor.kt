package com.vitalo.markrun.data.remote.crypto

import android.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

object DESEncryptor {
    fun encrypt(data: ByteArray, key: ByteArray): ByteArray {
        val cipher = Cipher.getInstance("DES/ECB/PKCS5Padding")
        cipher.init(Cipher.ENCRYPT_MODE, SecretKeySpec(key, "DES"))
        return cipher.doFinal(data)
    }

    fun decrypt(data: ByteArray, key: ByteArray): ByteArray {
        val cipher = Cipher.getInstance("DES/ECB/PKCS5Padding")
        cipher.init(Cipher.DECRYPT_MODE, SecretKeySpec(key, "DES"))
        return cipher.doFinal(data)
    }

    /**
     * iOS DESEncryptor.decodeKeyIfNeed(key:isKeyEncoded:) 的等价实现。
     * 处理密钥：如果是 Base64 编码则先解码，否则 UTF-8 转字节，最终截取/填充为 8 字节。
     */
    fun decodeKeyIfNeed(key: String, isKeyEncoded: Boolean): ByteArray {
        val keyBytes = if (isKeyEncoded) {
            val standardBase64 = key
                .replace('-', '+')
                .replace('_', '/')
            val padded = standardBase64.let {
                val miss = it.length % 4
                if (miss > 0) it + "=".repeat(4 - miss) else it
            }
            Base64.decode(padded, Base64.DEFAULT)
        } else {
            key.toByteArray(Charsets.UTF_8)
        }

        return when {
            keyBytes.size > 8 -> keyBytes.copyOf(8)
            keyBytes.size < 8 -> keyBytes.copyOf(8)
            else -> keyBytes
        }
    }

    fun encryptToUrlSafeBase64(data: ByteArray, key: ByteArray): String {
        val encrypted = encrypt(data, key)
        return Base64.encodeToString(encrypted, Base64.NO_WRAP)
            .replace('+', '-')
            .replace('/', '_')
            .trimEnd('=')
    }

    fun decryptFromUrlSafeBase64(encoded: String, key: ByteArray): ByteArray {
        var standardBase64 = encoded.replace('-', '+').replace('_', '/')
        val miss = standardBase64.length % 4
        if (miss > 0) standardBase64 += "=".repeat(4 - miss)
        val decoded = Base64.decode(standardBase64, Base64.DEFAULT)
        return decrypt(decoded, key)
    }
}
