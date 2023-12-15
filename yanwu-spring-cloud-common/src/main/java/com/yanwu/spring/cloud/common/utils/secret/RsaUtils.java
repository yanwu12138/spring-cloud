package com.yanwu.spring.cloud.common.utils.secret;

import com.yanwu.spring.cloud.common.cache.ExpiredHashMap;
import com.yanwu.spring.cloud.common.pojo.ExpiredNodeCO;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;

import javax.crypto.Cipher;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Map;

/**
 * @author Baofeng Xu
 * @date 2021/7/31 11:56.
 * <p>
 * description:  RSA 加密工具类
 */
@Slf4j
public class RsaUtils {

    /*** 密钥长度 于原文长度对应 以及越长速度越慢 ***/
    private final static int KEY_SIZE = 1024;
    private static final String ALGORITHM_NAME = "RSA";
    /*** 用于缓存公钥与私钥对, 过期时间1分钟 ***/
    private static final Map<String, ExpiredNodeCO<KeyPairCO>> KEY_PAIR_CACHE = new ExpiredHashMap<>(60_000L);

    private RsaUtils() {
        throw new UnsupportedOperationException("RsaUtils should never be instantiated");
    }

    public static void main(String[] args) throws Exception {
        // ===== 生成公钥和私钥
        long temp = System.currentTimeMillis();
        String appId = RandomStringUtils.randomAlphanumeric(32).toLowerCase();
        randomKeyPair(appId);
        String publicKey = getPublicKey(appId), privateKey = getPrivateKey(appId);
        String message = appId + "2023-05-17 11:30:22" + "A01,A02";
        log.info("========================================");
        log.info("AppID: {}", appId);
        log.info("公钥: {}", publicKey);
        log.info("私钥: {}", privateKey);
        log.info("明文: {}", message);
        log.info("生成密钥消耗时间: {}", (System.currentTimeMillis() - temp));
        // ===== 使用公钥加密
        temp = System.currentTimeMillis();
        String encrypt = encrypt(message, publicKey);
        log.info("密文: {}", encrypt);
        log.info("加密时长: {}", System.currentTimeMillis() - temp);
        // ===== 使用私钥解密
        temp = System.currentTimeMillis();
        String decrypt = decrypt(encrypt, privateKey);
        log.info("明文: {}", decrypt);
        log.info("解密时长: {}, 加解密验证: {}", System.currentTimeMillis() - temp, message.equals(decrypt));
        if (!message.equals(decrypt)) {
            log.error("加解密错误, appId: {}", appId);
            throw new RuntimeException();
        }
        log.info("========================================");
    }

    /**
     * 随机生成密钥对, 并加入缓存
     *
     * @param appId 应用ID
     */
    public static void randomKeyPair(String appId) throws NoSuchAlgorithmException {
        // ----- KeyPairGenerator类用于生成公钥和私钥对，基于RSA算法生成对象
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(ALGORITHM_NAME);
        // ----- 初始化密钥对生成器
        keyPairGen.initialize(KEY_SIZE, new SecureRandom(appId.getBytes(StandardCharsets.UTF_8)));
        // ----- 生成一个密钥对，保存在keyPair中
        synchronized (ALGORITHM_NAME) {
            KeyPairCO instance = KeyPairCO.getInstance(appId, keyPairGen.generateKeyPair());
            KEY_PAIR_CACHE.put(appId, ExpiredNodeCO.getInstance(instance));
        }
    }

    /**
     * 从缓存中获取公钥
     *
     * @param appId 应用ID
     * @return 公钥
     */
    public static String getPublicKey(String appId) {
        KeyPairCO cache = getKeyPair(appId);
        return cache == null ? null : cache.getPublicKey();
    }

    /**
     * 从缓存中获取私钥
     *
     * @param appId 应用ID
     * @return 私钥
     */
    public static String getPrivateKey(String appId) {
        KeyPairCO cache = getKeyPair(appId);
        return cache == null ? null : cache.getPrivateKey();
    }

    /**
     * RSA公钥加密
     *
     * @param content   加密字符串
     * @param publicKey 公钥
     * @return 密文
     * @throws Exception 加密过程中的异常信息
     */
    public static String encrypt(String content, String publicKey) throws Exception {
        byte[] inputByte = content.getBytes(StandardCharsets.UTF_8);
        byte[] decoded = Base64.getDecoder().decode(publicKey);
        RSAPublicKey pubKey = (RSAPublicKey) KeyFactory.getInstance(ALGORITHM_NAME).generatePublic(new X509EncodedKeySpec(decoded));
        Cipher cipher = Cipher.getInstance(ALGORITHM_NAME);
        cipher.init(Cipher.ENCRYPT_MODE, pubKey);
        return Base64.getEncoder().encodeToString(cipher.doFinal(inputByte));
    }

    /**
     * RSA私钥解密
     *
     * @param content    加密字符串
     * @param privateKey 私钥
     * @return 明文
     * @throws Exception 解密过程中的异常信息
     */
    public static String decrypt(String content, String privateKey) throws Exception {
        byte[] inputByte = Base64.getDecoder().decode(content);
        byte[] decoded = Base64.getDecoder().decode(privateKey);
        RSAPrivateKey priKey = (RSAPrivateKey) KeyFactory.getInstance(ALGORITHM_NAME).generatePrivate(new PKCS8EncodedKeySpec(decoded));
        Cipher cipher = Cipher.getInstance(ALGORITHM_NAME);
        cipher.init(Cipher.DECRYPT_MODE, priKey);
        return new String(cipher.doFinal(inputByte));
    }

    private static KeyPairCO getKeyPair(String appId) {
        synchronized (ALGORITHM_NAME) {
            return StringUtils.isBlank(appId) ? null : KEY_PAIR_CACHE.get(appId).getValue();
        }
    }

    @Getter
    @Accessors(chain = true)
    public static class KeyPairCO implements Serializable {
        private static final long serialVersionUID = -1909427881604039561L;

        private String appId;
        private String publicKey;
        private String privateKey;
        private Long lastTime;

        public static KeyPairCO getInstance(String appId, KeyPair keyPair) {
            KeyPairCO instance = new KeyPairCO().setAppId(appId).setLastTime(System.currentTimeMillis());
            // ----- 获取公钥
            RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
            instance.setPublicKey(Base64.getEncoder().encodeToString(publicKey.getEncoded()));
            // ----- 获取私钥
            RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
            instance.setPrivateKey(Base64.getEncoder().encodeToString(privateKey.getEncoded()));
            return instance;
        }

        private KeyPairCO() {
        }

        private KeyPairCO setAppId(String appId) {
            this.appId = appId;
            return this;
        }

        private KeyPairCO setLastTime(Long lastTime) {
            this.lastTime = lastTime;
            return this;
        }

        private void setPublicKey(String publicKey) {
            this.publicKey = publicKey;
        }

        private void setPrivateKey(String privateKey) {
            this.privateKey = privateKey;
        }
    }

}