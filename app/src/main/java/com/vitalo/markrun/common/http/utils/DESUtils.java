package com.vitalo.markrun.common.http.utils;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public final class DESUtils {

    public static byte[] getIv() {
        final byte[] iv = new byte[8];
        new SecureRandom().nextBytes(iv);
        return iv;
    }

    public static byte[] generate(final String seed) throws NoSuchAlgorithmException {
        SecureRandom secureRandom;
        if (seed != null) {
            secureRandom = new SecureRandom(seed.getBytes());
        } else {
            secureRandom = new SecureRandom();
        }

        final KeyGenerator keyGenerator = KeyGenerator.getInstance(Encrypt.ALGORITHM_DES);
        keyGenerator.init(secureRandom);
        final SecretKey secretKey = keyGenerator.generateKey();
        return secretKey.getEncoded();
    }

    public static byte[] getKeyBytes(byte[] key) {
        return Arrays.copyOf(key, 8);
    }

    private static Key getKey(final byte[] key) {
        return new SecretKeySpec(key, Encrypt.ALGORITHM_DES);
    }

    public static byte[] encrypt(final byte[] clearBytes, final byte[] keyBytes)
            throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
            IllegalBlockSizeException, BadPaddingException {
        if (clearBytes == null) {
            throw new IllegalArgumentException("Specified data must not be null");
        }
        if (keyBytes == null) {
            throw new IllegalArgumentException("Specified key must not be null");
        }

        final Cipher cipher = Cipher.getInstance(Encrypt.DES_ECB_ENCRYPTION_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, getKey(keyBytes));
        return cipher.doFinal(clearBytes);
    }

    public static byte[] encrypt(final byte[] clearBytes, final byte[] keyBytes, final byte[] iv)
            throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
            InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
        if (clearBytes == null) {
            throw new IllegalArgumentException("Specified data must not be null");
        }
        if (keyBytes == null) {
            throw new IllegalArgumentException("Specified key must not be null");
        }
        if (iv == null) {
            throw new IllegalArgumentException("Specified iv must not be null");
        }

        final Cipher cipher = Cipher.getInstance(Encrypt.DES_CBC_ENCRYPTION_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, getKey(keyBytes), new IvParameterSpec(iv));
        return cipher.doFinal(clearBytes);
    }

    public static byte[] decrypt(final byte[] cipherBytes, final byte[] keyBytes)
            throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
            IllegalBlockSizeException, BadPaddingException {
        if (cipherBytes == null) {
            throw new IllegalArgumentException("Specified data must not be null");
        }
        if (keyBytes == null) {
            throw new IllegalArgumentException("Specified key must not be null");
        }

        final Cipher cipher = Cipher.getInstance(Encrypt.DES_ECB_ENCRYPTION_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, getKey(keyBytes));
        return cipher.doFinal(cipherBytes);
    }

    public static byte[] decrypt(final byte[] cipherBytes, final byte[] keyBytes, final byte[] iv)
            throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
            InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
        if (cipherBytes == null) {
            throw new IllegalArgumentException("Specified data must not be null");
        }
        if (keyBytes == null) {
            throw new IllegalArgumentException("Specified key must not be null");
        }
        if (iv == null) {
            throw new IllegalArgumentException("Specified iv must not be null");
        }

        final Cipher cipher = Cipher.getInstance(Encrypt.DES_CBC_ENCRYPTION_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, getKey(keyBytes), new IvParameterSpec(iv));
        return cipher.doFinal(cipherBytes);
    }
}
