package com.yanwu.spring.cloud.common.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.util.Assert;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Security;

/**
 * @author Baofeng Xu
 * @date 2021/7/8 11:08.
 * <p>
 * description: SM4加解密算法
 */
@Slf4j
@SuppressWarnings("unused")
public class Sm4Util {
    /*** 使用SM4加密算法 ***/
    private static final String ALGORITHM_NAME = "SM4";
    /*** 分组加密模式 ***/
    private static final String ALGORITHM_NAME_ECB_PADDING = "SM4/ECB/PKCS5Padding";
    /*** 默认的密钥: ByteUtil.bytesToHexStr(ByteUtil.strToAsciiBytes("yanwu0527@163com")) ***/
    private static final String DEFAULT_SECRET_KEY = "79616E77753035323740313633636F6D";

    private Sm4Util() {
        throw new UnsupportedOperationException("Sm4Util should never be instantiated");
    }

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    /**
     * 使用默认密钥对字符串进行加密，输出16进制字符串
     *
     * @param content 明文
     * @return 密文
     */
    public static String encryptToHexStr(String content) {
        return encryptToHexStr(content, DEFAULT_SECRET_KEY);
    }

    /**
     * 使用自定义密钥对字符串进行加密，输出16进制字符串
     *
     * @param content 明文
     * @param key     密钥
     * @return 密文
     */
    public static String encryptToHexStr(String content, String key) {
        byte[] bytes = encryptToBytes(content, key);
        return bytes != null && bytes.length > 0 ? ByteUtil.bytesToHexStr(bytes) : null;
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
            Assert.isTrue(StringUtils.isNotBlank(key), "SM4 encryption error because key is null.");
            Assert.isTrue(StringUtils.isNotBlank(content), "SM4 encryption error because content is null.");
            Cipher cipher = generateEcbCipher(Cipher.ENCRYPT_MODE, ByteUtil.hexStrToBytes(key));
            return cipher.doFinal(content.getBytes());
        } catch (Exception e) {
            log.error("String: [{}], key: [{}]. SM4 encryption error.", content, key, e);
        }
        return null;
    }

    /**
     * 使用默认密钥对16进制字符串进行解密
     *
     * @param content 密文
     * @return 明文
     */
    public static String decryptByHexStr(String content) {
        return decryptByHexStr(content, DEFAULT_SECRET_KEY);
    }

    /**
     * 使用自定义密钥对16进制字符串进行解密
     *
     * @param content 密文
     * @param key     密钥
     * @return 明文
     */
    public static String decryptByHexStr(String content, String key) {
        return decryptByBytes(ByteUtil.hexStrToBytes(content), key);
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
            Assert.isTrue(StringUtils.isNotBlank(key), "SM4 encryption error because key is null.");
            Assert.isTrue((content != null), "SM4 encryption error because content is null.");
            Cipher cipher = generateEcbCipher(Cipher.DECRYPT_MODE, ByteUtil.hexStrToBytes(key));
            return new String(cipher.doFinal(content), StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("String: [{}], key: [{}]. SM4 decryption error.", content, key, e);
        }
        return null;
    }

    /**
     * 生成加密秘钥
     *
     * @param mode 算法名称
     * @param key  模式
     * @return ECB暗号
     * @throws Exception Exception.class
     */
    private static Cipher generateEcbCipher(int mode, byte[] key) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM_NAME_ECB_PADDING, BouncyCastleProvider.PROVIDER_NAME);
        cipher.init(mode, new SecretKeySpec(key, ALGORITHM_NAME));
        return cipher;
    }

}
