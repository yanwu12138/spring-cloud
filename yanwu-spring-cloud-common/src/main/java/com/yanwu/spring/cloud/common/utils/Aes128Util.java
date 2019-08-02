package com.yanwu.spring.cloud.common.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;

/**
 * @author XuBaofeng.
 * @date 2018-10-11 18:55.
 * <p>
 * description: 字符串AES128加密 <加盐>
 */
@Slf4j
public class Aes128Util {

    private static final String KEY_ALGORITHM = "AES";
    /*** 字符编码 ***/
    private static final String CHARACTER_CODING = "UTF-8";
    /*** 默认的加密算法 ***/
    private static final String DEFAULT_CIPHER_ALGORITHM = "AES/ECB/PKCS5Padding";
    /*** 默认的盐 ***/
    private static final String DEFAULT_SECRET_KEY = "yanwu0527@123.com";

    /**
     * AES 加密操作, 使用默认盐
     *
     * @param content 待加密内容
     * @return 返回Base64转码后的加密数据
     */
    public static String encrypt(String content) {
        return encrypt(content, DEFAULT_SECRET_KEY);
    }

    /**
     * AES 加密操作, 自定义盐
     *
     * @param content
     * @param key
     * @return
     */
    public static String encrypt(String content, String key) {
        if (StringUtils.isBlank(content)) {
            return content;
        }
        if (StringUtils.isBlank(key)) {
            key = DEFAULT_SECRET_KEY;
        }
        try {
            Cipher cipher = Cipher.getInstance(DEFAULT_CIPHER_ALGORITHM);
            byte[] byteContent = content.getBytes(CHARACTER_CODING);
            cipher.init(Cipher.ENCRYPT_MODE, getSecretKey(key));
            byte[] result = cipher.doFinal(byteContent);
            return Base64.encodeBase64String(result);
        } catch (Exception e) {
            log.error("String: [{}] Aes128Util encryption error", content, e);
        }
        return null;
    }

    /**
     * AES 解密操作, 使用默认盐
     *
     * @param content
     * @return
     */
    public static String decrypt(String content) {
        return decrypt(content, DEFAULT_SECRET_KEY);
    }

    /**
     * AES 解密操作, 自定义盐
     *
     * @param content
     * @param key
     * @return
     */
    public static String decrypt(String content, String key) {
        if (StringUtils.isBlank(content)) {
            return content;
        }
        if (StringUtils.isBlank(key)) {
            key = DEFAULT_SECRET_KEY;
        }
        try {
            Cipher cipher = Cipher.getInstance(DEFAULT_CIPHER_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, getSecretKey(key));
            byte[] result = cipher.doFinal(Base64.decodeBase64(content));
            return new String(result, CHARACTER_CODING);
        } catch (Exception e) {
            log.error("String: [{}] Aes128Util decryption error", content, e);
        }
        return null;
    }

    /**
     * 生成加密秘钥
     *
     * @return
     */
    private static SecretKeySpec getSecretKey(final String key) throws Exception {
        KeyGenerator kg = KeyGenerator.getInstance(KEY_ALGORITHM);
        kg.init(128, new SecureRandom(key.getBytes()));
        SecretKey secretKey = kg.generateKey();
        return new SecretKeySpec(secretKey.getEncoded(), KEY_ALGORITHM);
    }

}