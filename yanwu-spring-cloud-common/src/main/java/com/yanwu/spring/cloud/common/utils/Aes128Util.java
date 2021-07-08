package com.yanwu.spring.cloud.common.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;

/**
 * @author XuBaofeng.
 * @date 2018-10-11 18:55.
 * <p>
 * description: 字符串AES128加密
 */
@Slf4j
@SuppressWarnings("unused")
public class Aes128Util {

    private static final String KEY_ALGORITHM = "AES";
    private static final String SHA1_PRNG = "SHA1PRNG";
    /*** 默认的加密算法 ***/
    private static final String DEFAULT_CIPHER_ALGORITHM = "AES/ECB/PKCS5Padding";
    /*** 默认的密钥 ***/
    private static final String DEFAULT_SECRET_KEY = "yanwu0527@123.com";

    private Aes128Util() {
        throw new UnsupportedOperationException("Aes128Util should never be instantiated");
    }

    /**
     * 使用默认密钥对字符串进行加密，输出Base64转码后的字符串
     *
     * @param content 明文
     * @return 密文
     */
    public static String encryptToStr(String content) {
        return encryptToStr(content, DEFAULT_SECRET_KEY);
    }

    /**
     * 使用自定义密钥对字符串进行加密，输出Base64转码后的字符串
     *
     * @param content 明文
     * @param key     密钥
     * @return 密文
     */
    public static String encryptToStr(String content, String key) {
        byte[] bytes = encryptToBytes(content, key);
        return bytes != null && bytes.length > 0 ? Base64.encodeBase64String(bytes) : null;
    }

    /**
     * 使用默认密钥对字符串进行加密，输出byte数组
     *
     * @param content 明文
     * @return 密文
     */
    public static byte[] encryptToBytes(String content) {
        return encryptToBytes(content, DEFAULT_SECRET_KEY);
    }

    /**
     * 使用自定义密钥对字符串进行加密，输出byte数组
     *
     * @param content 明文
     * @param key     密钥
     * @return 密文
     */
    public static byte[] encryptToBytes(String content, String key) {
        try {
            Assert.isTrue(StringUtils.isNotBlank(key), "AES encryption error because key is null.");
            Assert.isTrue(StringUtils.isNotBlank(content), "AES encryption error because content is null.");
            Cipher cipher = Cipher.getInstance(DEFAULT_CIPHER_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, getSecretKey(key));
            return cipher.doFinal(content.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            log.error("String: [{}], key: [{}]. AES encryption error.", content, key, e);
        }
        return null;
    }

    /**
     * 使用默认密钥对Base64转码后的字符串进行解密
     *
     * @param content 密文
     * @return 明文
     */
    public static String decryptByStr(String content) {
        return decryptByStr(content, DEFAULT_SECRET_KEY);
    }

    /**
     * 使用自定义密钥对Base64转码后的字符串进行解密
     *
     * @param content 密文
     * @param key     密钥
     * @return 明文
     */
    public static String decryptByStr(String content, String key) {
        return decryptByBytes(Base64.decodeBase64(content), key);
    }

    /**
     * 使用默认密钥对byte数组进行解密
     *
     * @param content 密文
     * @return 明文
     */
    public static String decryptByBytes(byte[] content) {
        return decryptByBytes(content, DEFAULT_SECRET_KEY);
    }

    /**
     * 使用自定义密钥对byte数组进行解密
     *
     * @param content 密文
     * @param key     密钥
     * @return 明文
     */
    public static String decryptByBytes(byte[] content, String key) {
        try {
            Assert.isTrue(StringUtils.isNotBlank(key), "AES encryption error because key is null.");
            Assert.isTrue((content != null), "AES encryption error because content is null.");
            Cipher cipher = Cipher.getInstance(DEFAULT_CIPHER_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, getSecretKey(key));
            byte[] result = cipher.doFinal(content);
            return new String(result, StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("String: [{}], key: [{}]. AES decryption error.", content, key, e);
        }
        return null;
    }

    /**
     * 生成加密秘钥
     *
     * @return 密钥
     */
    private static SecretKey getSecretKey(final String key) throws Exception {
        KeyGenerator kg = KeyGenerator.getInstance(KEY_ALGORITHM);
        SecureRandom secureRandom = SecureRandom.getInstance(SHA1_PRNG);
        secureRandom.setSeed(key.getBytes());
        kg.init(secureRandom);
        return kg.generateKey();
    }

}